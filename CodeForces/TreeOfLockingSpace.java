// Question :
// Given a world map in the form of Generic M-ary Tree consisting of N nodes and an array queries[], the task is to implement the functions Lock, Unlock and Upgrade for the given tree. For each query in queries[], the functions return true when the operation is performed successfully, otherwise it returns false. The functions are defined as: 

// X: Name of the node in the tree and will be unique
// uid: User Id for the person who accesses node X

// 1. Lock(X, uid): Lock takes exclusive access to the subtree rooted.

//     Once Lock(X, uid) succeeds, then lock(A, any user) should fail, where A is a descendant of X.
//     Lock(B. any user) should fail where X is a descendant of B.
//     Lock operation cannot be performed on a node that is already locked.

// 2. Unlock(X, uid): To unlock the locked node.

//     The unlock reverts what was done by the Lock operation.
//     It can only be called on same and unlocked by same uid.

// 3. UpgradeLock(X, uid): The user uid can upgrade their lock to an ancestor node.

//     It is only possible if any ancestor node is only locked by the same user uid.
//     The Upgrade should fail if there is any node that is locked by some other uid Y below.

// Examples:

//     Input: N = 7, M = 2, nodes = ['World', 'Asia', 'Africa', 'China', 'India', 'SouthAfrica', 'Egypt'],  
//     queries =  ['1 China 9', '1 India 9', '3 Asia 9', '2 India 9', '2 Asia 9']
//     Output: true true true false true

//     Input: N = 3, M = 2, nodes = ['World', 'China', 'India'],  
//     queries =  ['3 India 1', '1 World 9']
//     Output: false true 

import java.util.*;

class Node {
    String name;
    Node parent;
    boolean isLocked = false;
    int lockedBy = -1;
    // Storing descendants as Node references makes bulk unlocking highly efficient
    Set<Node> lockedDescendants = new HashSet<>();

    Node(String name) {
        this.name = name;
    }
}

class LockingTree {
    Map<String, Node> map = new HashMap<>();

    public LockingTree(String[] nodes, int m) {
        Node[] nodeArr = new Node[nodes.length];
        
        // 1. Initialize all nodes and populate the map
        for (int i = 0; i < nodes.length; i++) {
            nodeArr[i] = new Node(nodes[i]);
            map.put(nodes[i], nodeArr[i]);
        }
        
        // 2. Build the Generic M-ary Tree relationships
        for (int i = 1; i < nodes.length; i++) {
            int parentIndex = (i - 1) / m;
            nodeArr[i].parent = nodeArr[parentIndex];
        }
    }

    public boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null || curr.isLocked || !curr.lockedDescendants.isEmpty() || hasLockedAncestor(curr)) {
            return false;
        }

        curr.isLocked = true;
        curr.lockedBy = user;

        // Propagate the locked status to all ancestors
        Node temp = curr.parent;
        while (temp != null) {
            temp.lockedDescendants.add(curr);
            temp = temp.parent;
        }

        return true;
    }

    public boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null || !curr.isLocked || curr.lockedBy != user) {
            return false;
        }

        curr.isLocked = false;
        curr.lockedBy = -1;

        // Remove this node from all ancestors' locked descendants sets
        Node temp = curr.parent;
        while (temp != null) {
            temp.lockedDescendants.remove(curr);
            temp = temp.parent;
        }

        return true;
    }

    public boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        
        if (curr == null || curr.isLocked || curr.lockedDescendants.isEmpty() || hasLockedAncestor(curr)) {
            return false;
        }

        // Verify ALL locked descendants are locked by the SAME user
        for (Node descendant : curr.lockedDescendants) {
            if (descendant.lockedBy != user) {
                return false;
            }
        }

        // Create a copy to avoid ConcurrentModificationException during iteration
        List<Node> copy = new ArrayList<>(curr.lockedDescendants);
        for(Node des : copy) {
            unlock(des.name, des.lockedBy);
            curr.lockedDescendants.remove(des);
        }

        curr.isLocked = true;
        curr.lockedBy = user;

        Node parent = curr.parent;
        while (parent != null) {
            parent.lockedDescendants.add(curr);
            parent = parent.parent;
        }

        return true;
    }

    private boolean hasLockedAncestor(Node curr) {
        Node parent = curr.parent;
        while (parent != null) {
            if (parent.isLocked) {
                return true;
            }
            parent = parent.parent;
        }
        return false;
    }
}

public class TreeOfLockingSpace {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(), m = sc.nextInt();
        sc.nextLine();
        String[] nodes = new String[n];
        for(int i=0;i<n;i++) 
            nodes[i] = sc.nextLine();
        
        LockingTree lt = new LockingTree(nodes, m);

        int tc = sc.nextInt();
        while(tc-->0) {
            int op = sc.nextInt();
            String str = sc.next();
            int val = sc.nextInt();

            switch (op) {
                case 1 -> System.out.println(lt.lock(str,val));
                case 2 -> System.out.println(lt.unlock(str, val));
                case 3 -> System.out.println(lt.upgrade(str, val));
            }
        }
        
        sc.close();
    }
}