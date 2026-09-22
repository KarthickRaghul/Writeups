import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    
    // Thread-safe constructs
    AtomicInteger lockedDescendantCount;
    ReentrantReadWriteLock rwl;
    List<Node> children; 

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.lockedDescendantCount = new AtomicInteger(0);
        this.rwl = new ReentrantReadWriteLock();
        this.children = new ArrayList<>();
    }
}

class TreeOfLocking {
    HashMap<String, Node> map;

    public TreeOfLocking(String[] names, int m, int n) {
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

    // Helper to get the path from Root to the target node
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

    boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        
        // 1. Acquire locks Top-Down to prevent deadlock
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).rwl.readLock().lock();
        }
        curr.rwl.writeLock().lock();

        try {
            // 2. Validate state
            if (curr.isLocked || curr.lockedDescendantCount.get() > 0) 
                return false;
                
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) 
                    return false;
            }
            
            // 3. Execute lock
            curr.isLocked = true;
            curr.lockedBy = user;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount.incrementAndGet();
            }
            return true;
            
        } finally {
            // 4. Release locks Bottom-Up
            curr.rwl.writeLock().unlock();
            for (int i = path.size() - 2; i >= 0; i--) {
                path.get(i).rwl.readLock().unlock();
            }
        }
    }

    boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        
        // 1. Acquire locks
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).rwl.readLock().lock();
        }
        curr.rwl.writeLock().lock();

        try {
            // 2. Validate state
            if (!curr.isLocked || curr.lockedBy != user) 
                return false;
                
            // 3. Execute unlock
            curr.isLocked = false;
            curr.lockedBy = -1;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount.decrementAndGet();
            }
            return true;
            
        } finally {
            // 4. Release locks
            curr.rwl.writeLock().unlock();
            for (int i = path.size() - 2; i >= 0; i--) {
                path.get(i).rwl.readLock().unlock();
            }
        }
    }

    boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        
        // 1. Acquire locks
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).rwl.readLock().lock();
        }
        // Write lock on curr prevents ANY concurrent modifications in the entire subtree
        curr.rwl.writeLock().lock();

        try {
            // 2. Validate state
            if (curr.isLocked || curr.lockedDescendantCount.get() == 0) 
                return false;
                
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) 
                    return false;
            }

            List<Node> lockedDescendants = new ArrayList<>();
            if (!verifyAndGatherLocked(curr, user, lockedDescendants)) {
                return false;
            }
                
            // 3. Execute upgrade
            for (Node des : lockedDescendants) {
                des.isLocked = false;
                des.lockedBy = -1;
                
                // Update internal descendant counts within the subtree
                Node temp = des.parent;
                while (temp != curr) {
                    temp.lockedDescendantCount.decrementAndGet();
                    temp = temp.parent;
                }
            }
            
            curr.isLocked = true;
            curr.lockedBy = user;
            curr.lockedDescendantCount.set(0);
            
            // Adjust ancestor counts based on the net change of locked nodes in the subtree
            int netChange = 1 - lockedDescendants.size();
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount.addAndGet(netChange);
            }
            
            return true;
            
        } finally {
            // 4. Release locks
            curr.rwl.writeLock().unlock();
            for (int i = path.size() - 2; i >= 0; i--) {
                path.get(i).rwl.readLock().unlock();
            }
        }
    }

    // Unchanged: Traversal is safe without additional locks because the Write Lock on curr 
    // guarantees exclusive access to the subtree structure.
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