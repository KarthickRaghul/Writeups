import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class Node {
    String name;
    Node parent;
    
    // CAS State: -1 (locked by user), 0 (free), >0 (number of locked descendants)
    AtomicInteger state = new AtomicInteger(0);
    AtomicReference<Integer> lockedBy = new AtomicReference<>(null);
    Set<Node> lockedDescendants = ConcurrentHashMap.newKeySet();

    Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
    }
} 

class ConcurrentLockingTree {
    private final ConcurrentHashMap<String, Node> map = new ConcurrentHashMap<>();

    // Build the tree dynamically using M-ary math
    public ConcurrentLockingTree(String[] nodes, int m) {
        map.put(nodes[0], new Node(nodes[0], null));
        
        for (int i = 1; i < nodes.length; i++) {
            // Mathematical formula to find parent in a level-order M-ary tree
            String parentName = nodes[(i - 1) / m];
            Node parentNode = map.get(parentName);
            map.put(nodes[i], new Node(nodes[i], parentNode));
        }
    }

    public boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        if (!curr.state.compareAndSet(0, -1)) return false;

        Node temp = curr.parent;
        while (temp != null) {
            int expectedState;
            boolean success;
            do {
                expectedState = temp.state.get();
                if (expectedState == -1) {
                    rollback(curr, temp);
                    return false; 
                }
                success = temp.state.compareAndSet(expectedState, expectedState + 1);
            } while (!success); 

            temp.lockedDescendants.add(curr);
            temp = temp.parent;
        }

        curr.lockedBy.set(user);
        return true;
    }

    public boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        if (curr.lockedBy.get() == null || curr.lockedBy.get() != user || curr.state.get() != -1) {
            return false;
        }

        Node temp = curr.parent;
        while (temp != null) {
            int expectedState;
            do {
                expectedState = temp.state.get();
            } while (!temp.state.compareAndSet(expectedState, expectedState - 1));
            
            temp.lockedDescendants.remove(curr);
            temp = temp.parent;
        }

        curr.lockedBy.set(null);
        curr.state.set(0); 
        return true;
    }

    public boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        int currentState = curr.state.get();
        if (currentState == -1 || curr.lockedDescendants.isEmpty()) {
            return false; 
        }

        for (Node desc : curr.lockedDescendants) {
            if (desc.lockedBy.get() == null || desc.lockedBy.get() != user) {
                return false;
            }
        }

        if (!curr.state.compareAndSet(currentState, -1)) {
            return false; 
        }

        List<Node> toUnlock = new ArrayList<>(curr.lockedDescendants);
        for (Node desc : toUnlock) {
            unlock(desc.name, user);
        }

        curr.lockedBy.set(user);
        
        Node temp = curr.parent;
        while (temp != null) {
            int expected;
            do {
                expected = temp.state.get();
            } while (!temp.state.compareAndSet(expected, expected + 1));
            
            temp.lockedDescendants.add(curr);
            temp = temp.parent;
        }

        return true;
    }

    private void rollback(Node curr, Node failedAncestor) {
        Node rollbackNode = curr.parent;
        while (rollbackNode != failedAncestor) {
            int expectedState;
            do {
                expectedState = rollbackNode.state.get();
            } while (!rollbackNode.state.compareAndSet(expectedState, expectedState - 1));
            
            rollbackNode.lockedDescendants.remove(curr);
            rollbackNode = rollbackNode.parent;
        }
        curr.state.set(0); 
    }
}

public class ConcurrentLockingTreeMultithreading {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Safety check for empty inputs
        if (!sc.hasNextInt()) return; 

        // 1. Read structural parameters
        int n = sc.nextInt();
        int m = sc.nextInt();
        int q = sc.nextInt(); // Number of queries

        // 2. Read node names
        String[] nodes = new String[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = sc.next();
        }

        // 3. Build the Tree
        ConcurrentLockingTree tree = new ConcurrentLockingTree(nodes, m);

        // 4. Process queries and build the space-separated output
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < q; i++) {
            int type = sc.nextInt();
            String name = sc.next();
            int uid = sc.nextInt();

            boolean res = false;
            if (type == 1) {
                res = tree.lock(name, uid);
            } else if (type == 2) {
                res = tree.unlock(name, uid);
            } else if (type == 3) {
                res = tree.upgrade(name, uid);
            }

            // Append "true" or "false" followed by a space
            out.append(res).append(" ");
        }

        // Output exactly what the judge expects
        System.out.println(out.toString().trim());
        sc.close();
    }
}