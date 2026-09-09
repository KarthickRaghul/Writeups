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
    int parent;
    int val;
    List<Node> decendant = new ArrayList<>();
    boolean isLocked = false;
    int lockedBy = -1;
    Set<Integer> lockedDecendants = new HashSet<>();

    Node (int val, int parent) {
        this.val = val;
        this.parent = parent;
    }
}

class LockingTree {

    Node tree;
    Map<Integer, Node> map = new HashMap<>();

    public LockingTree(int[] parent) {

        for(int i = 0 ; i < parent.length ; i++)
            map.put(i,new Node(i,parent[i]));

        tree = new Node(0, parent[0]);
        map.put(0, tree);

        for (int i = 1; i < parent.length; i++) {

            Node curr = map.get(i);
            Node p = map.get(parent[i]);
            p.decendant.add(curr);

        }
    }

    public boolean lock(int num, int user) {
        

//     Once Lock(X, uid) succeeds, then lock(A, any user) should fail, where A is a descendant of X.
//     Lock(B. any user) should fail where X is a descendant of B.
//     Lock operation cannot be performed on a node that is already locked.

        Node curr = map.get(num);
        if (curr.isLocked || !curr.lockedDecendants.isEmpty() || hasLockedAncestor(curr))
            return false;

        curr.isLocked = true;
        curr.lockedBy = user;

        Node temp = map.get(curr.parent);
        while(temp != null) {
            temp.lockedDecendants.add(curr.val);
            temp = map.get(temp.parent);
        }

        return true;
    }

    public boolean unlock(int num, int user) {

//     The unlock reverts what was done by the Lock operation.
//     It can only be called on same and unlocked by same uid.

        Node curr = map.get(num);
        if (!curr.isLocked || curr.lockedBy != user)
            return false;

        curr.isLocked = false;
        curr.lockedBy = -1;

        Node temp = map.get(curr.parent);
        while(temp != null) {
            temp.lockedDecendants.remove(curr.val);
            temp = map.get(temp.parent);
        }

        return true;
    }

    public boolean upgrade(int num, int user) {

//     It is only possible if any ancestor node is only locked by the same user uid.
//     The Upgrade should fail if there is any node that is locked by some other uid Y below.
        
        Node curr = map.get(num);
        
        if(curr.isLocked)
            return false;

        //Check decendants for lock
        boolean oneLockedDes = curr.lockedDecendants.size() == 0;

        //Check parent
        boolean noParentLock = hasLockedAncestor(curr);

        if(oneLockedDes || noParentLock)
            return false;
        
        for (int nodeVal : curr.lockedDecendants) {
            Node node = map.get(nodeVal);

            if (node.lockedBy != user)
                return false;
        }

        //Unlock All decendants 
        List<Integer> lockedNodes =
                new ArrayList<>(curr.lockedDecendants);

        for (int nodeVal : lockedNodes) {
            unlock(nodeVal, user);
        }

        return lock(num, user);

    }

    private boolean hasLockedAncestor(Node curr) {

        Node parent = map.get(curr.parent);
        while (parent != null) {

            if (parent.isLocked)
                return true;

            parent = map.get(parent.parent);
        }

        return false;
    }
}