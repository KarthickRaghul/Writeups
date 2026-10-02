import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// 1. The Custom Spinlock: Bypasses the OS entirely for raw CPU speed
class CustomSpinLock {
    private final AtomicBoolean lock = new AtomicBoolean(false);

    public void lock() {
        // Spin continuously until compareAndSet succeeds. 
        // No OS context switching, no thread sleeping.
        while (!lock.compareAndSet(false, true)) {
            // Thread.onSpinWait() tells the CPU we are in a spin loop, 
            // preventing the CPU from overheating and optimizing power.
            Thread.onSpinWait(); 
        }
    }

    public void unlock() {
        lock.set(false);
    }
}

// 2. The Node class equipped with our Custom Spinlock
class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    
    int lockedDescendantCount;
    CustomSpinLock spinLock; // Replaces ReentrantLock/synchronized

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.lockedDescendantCount = 0;
        this.children = new ArrayList<>();
        this.spinLock = new CustomSpinLock();
    }
}

class ConcurrentTreeOfSpace {
    private final HashMap<String, Node> map;

    public ConcurrentTreeOfSpace(String[] names, int m, int n) {
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
        Collections.reverse(path); // Root to target order
        return path;
    }

    public boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);

        // 1. Acquire Spinlocks Top-Down to prevent deadlocks
        for (Node node : path) {
            node.spinLock.lock();
        }

        try {
            // 2. Critical Section: Extremely fast, fully protected
            if (curr.isLocked || curr.lockedDescendantCount > 0) return false;
            
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) return false;
            }

            curr.isLocked = true;
            curr.lockedBy = user;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount++;
            }
            return true;

        } finally {
            // 3. Release Spinlocks Bottom-Up
            for (int i = path.size() - 1; i >= 0; i--) {
                path.get(i).spinLock.unlock();
            }
        }
    }

    public boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);

        for (Node node : path) node.spinLock.lock();

        try {
            if (!curr.isLocked || curr.lockedBy != user) return false;

            curr.isLocked = false;
            curr.lockedBy = -1;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount--;
            }
            return true;

        } finally {
            for (int i = path.size() - 1; i >= 0; i--) {
                path.get(i).spinLock.unlock();
            }
        }
    }

    public boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);

        for (Node node : path) node.spinLock.lock();

        try {
            if (curr.isLocked || curr.lockedDescendantCount == 0) return false;
            
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) return false;
            }

            List<Node> lockedDescendants = new ArrayList<>();
            if (!verifyAndGatherLocked(curr, user, lockedDescendants)) return false;

            for (Node des : lockedDescendants) {
                des.isLocked = false;
                des.lockedBy = -1;
                
                Node temp = des.parent;
                while (temp != curr) {
                    temp.lockedDescendantCount--;
                    temp = temp.parent;
                }
            }

            curr.isLocked = true;
            curr.lockedBy = user;
            curr.lockedDescendantCount = 0; 
            
            int netChange = 1 - lockedDescendants.size();
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount += netChange;
            }
            return true;

        } finally {
            for (int i = path.size() - 1; i >= 0; i--) {
                path.get(i).spinLock.unlock();
            }
        }
    }

    private boolean verifyAndGatherLocked(Node node, int user, List<Node> lockedNodes) {
        if (node.isLocked) {
            if (node.lockedBy != user) return false;
            lockedNodes.add(node);
        }
        if (node.lockedDescendantCount == 0) return true;
        
        for (Node child : node.children) {
            if (child.isLocked || child.lockedDescendantCount > 0) {
                if (!verifyAndGatherLocked(child, user, lockedNodes)) return false;
            }
        }
        return true;
    }
}