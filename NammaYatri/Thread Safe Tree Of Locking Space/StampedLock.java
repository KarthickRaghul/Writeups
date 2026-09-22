import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    
    // StampedLock for OCC on the isLocked state
    StampedLock sl;
    // AtomicInteger handles the descendant math without requiring ancestor WriteLocks
    AtomicInteger lockedDescendantCount; 

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.sl = new StampedLock();
        this.lockedDescendantCount = new AtomicInteger(0);
        this.children = new ArrayList<>();
    }
}

class TreeOfLockingOCC {
    HashMap<String, Node> map;

    public TreeOfLockingOCC(String[] names, int m, int n) {
        map = new HashMap<>();
        Node[] nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(names[i]);
            map.put(names[i], nodes[i]);
        }
        for (int i = 1; i < n; i++) {
            int p = (i - 1) / m;
            nodes[i].parent = nodes[p];
            nodes[p].children.add(nodes[i]);
        }
    }

    private List<Node> getPath(Node node) {
        List<Node> path = new ArrayList<>();
        Node temp = node;
        while (temp != null) {
            path.add(temp);
            temp = temp.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        
        // Spin-retry loop for Optimistic concurrency
        while (true) {
            long[] stamps = new long[path.size()];
            boolean pathClear = true;

            // 1. OPTIMISTIC READ: No locks acquired! Just reading the state.
            for (int i = 0; i < path.size() - 1; i++) {
                Node anc = path.get(i);
                stamps[i] = anc.sl.tryOptimisticRead();
                
                // If tryOptimisticRead returns 0, a WriteLock is actively held.
                if (stamps[i] == 0L || anc.isLocked) {
                    pathClear = false;
                    break;
                }
            }

            if (!pathClear) {
                // If a writer is holding an ancestor, yield and retry.
                Thread.yield();
                continue; 
            }

            // 2. Try to acquire the WriteLock on the target node
            long writeStamp = curr.sl.tryWriteLock();
            if (writeStamp == 0L) {
                Thread.yield(); // Another thread is locking/unlocking this exact node
                continue;
            }

            try {
                // 3. VALIDATION: Did any ancestor change while we were getting the WriteLock?
                boolean valid = true;
                for (int i = 0; i < path.size() - 1; i++) {
                    if (!path.get(i).sl.validate(stamps[i])) {
                        valid = false;
                        break;
                    }
                }

                if (!valid) {
                    // Collision detected! Release our write lock and retry the optimistic path.
                    curr.sl.unlockWrite(writeStamp);
                    Thread.yield();
                    continue;
                }

                // --- CRITICAL SECTION: 100% SAFE ---
                if (curr.isLocked || curr.lockedDescendantCount.get() > 0) {
                    return false;
                }

                curr.isLocked = true;
                curr.lockedBy = user;

                // Atomic counts mean we don't need WriteLocks on ancestors to update them
                for (int i = 0; i < path.size() - 1; i++) {
                    path.get(i).lockedDescendantCount.incrementAndGet();
                }
                return true;

            } finally {
                // 4. Release target node WriteLock
                curr.sl.unlockWrite(writeStamp);
            }
        }
    }

    public boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        // Unlock only requires local validation, so we just block for the WriteLock
        long stamp = curr.sl.writeLock();
        try {
            if (!curr.isLocked || curr.lockedBy != user) {
                return false;
            }

            curr.isLocked = false;
            curr.lockedBy = -1;

            Node temp = curr.parent;
            while (temp != null) {
                temp.lockedDescendantCount.decrementAndGet();
                temp = temp.parent;
            }
            return true;
        } finally {
            curr.sl.unlockWrite(stamp);
        }
    }

    public boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);

        while (true) {
            long[] stamps = new long[path.size()];
            boolean pathClear = true;

            // 1. Optimistic ancestor check
            for (int i = 0; i < path.size() - 1; i++) {
                Node anc = path.get(i);
                stamps[i] = anc.sl.tryOptimisticRead();
                if (stamps[i] == 0L || anc.isLocked) {
                    pathClear = false;
                    break;
                }
            }

            if (!pathClear) {
                Thread.yield();
                continue;
            }

            // 2. Lock the target node (This prevents any new descendants from being locked)
            long writeStamp = curr.sl.tryWriteLock();
            if (writeStamp == 0L) {
                Thread.yield();
                continue;
            }

            try {
                // 3. Validate ancestor stamps
                boolean valid = true;
                for (int i = 0; i < path.size() - 1; i++) {
                    if (!path.get(i).sl.validate(stamps[i])) {
                        valid = false;
                        break;
                    }
                }

                if (!valid) {
                    curr.sl.unlockWrite(writeStamp);
                    Thread.yield();
                    continue;
                }

                // --- CRITICAL SECTION ---
                if (curr.isLocked || curr.lockedDescendantCount.get() == 0) {
                    return false;
                }

                List<Node> lockedNodes = new ArrayList<>();
                if (!verifyAndGatherLocked(curr, user, lockedNodes)) {
                    return false;
                }

                // 4. Safely modify descendants 
                for (Node des : lockedNodes) {
                    // Acquire write lock on descendant just to be architecturally pure
                    long desStamp = des.sl.writeLock();
                    try {
                        des.isLocked = false;
                        des.lockedBy = -1;
                    } finally {
                        des.sl.unlockWrite(desStamp);
                    }
                    
                    Node temp = des.parent;
                    while (temp != curr) {
                        temp.lockedDescendantCount.decrementAndGet();
                        temp = temp.parent;
                    }
                }

                curr.isLocked = true;
                curr.lockedBy = user;
                curr.lockedDescendantCount.set(0);

                // Correctly apply the net change to ancestors
                int netChange = 1 - lockedNodes.size();
                for (int i = 0; i < path.size() - 1; i++) {
                    path.get(i).lockedDescendantCount.addAndGet(netChange);
                }

                return true;

            } finally {
                curr.sl.unlockWrite(writeStamp);
            }
        }
    }

    private boolean verifyAndGatherLocked(Node node, int user, List<Node> lockedNodes) {
        if (node.isLocked) {
            if (node.lockedBy != user) return false;
            lockedNodes.add(node);
        }
        
        if (node.lockedDescendantCount.get() == 0) return true;
        
        for (Node child : node.children) {
            if (child.isLocked || child.lockedDescendantCount.get() > 0) {
                if (!verifyAndGatherLocked(child, user, lockedNodes)) {
                    return false;
                }
            }
        }
        return true;
    }
}