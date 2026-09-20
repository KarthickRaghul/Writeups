import java.util.*;

class Node {
    
    String name;
    Node parent;

    boolean isLocked;
    int lockedBy = -1;

    Set<Node> lockedDecendants = new HashSet<>();

    Node(String name) {
        this.name = name;
    }
}

class TreeOfLocking {

    HashMap<String,Node> map = new HashMap<>();
    Node nodes[];

    TreeOfLocking(String[] nodes, int m, int n) {
        
        this.nodes = new Node[n];
        for(int i=0;i<n;i++) {
            
            this.nodes[i] = new Node(nodes[i]);
            map.put(nodes[i], this.nodes[i]);

            if(i != 0) {
                int p = (i - 1) / m;
                this.nodes[i].parent = this.nodes[p];
            }
        }
    }

    boolean lock (String name, int user) {
        
        Node node = map.get(name);
        if(node == null || node.isLocked || !node.lockedDecendants.isEmpty() || hasLockedAncestor(node)) 
            return false;

        node.isLocked = true;
        node.lockedBy = user;

        Node parent = node.parent;
        while (parent != null) {
            parent.lockedDecendants.add(node);
            parent = parent.parent;
        }

        return true;
    }

    boolean unlock(String name, int user) {

        Node node = map.get(name);
        if(node == null || !node.isLocked || node.lockedBy != user) 
            return false;

        node.isLocked = false;
        node.lockedBy = -1;

        Node parent = node.parent;
        while (parent != null) {
            parent.lockedDecendants.remove(node);
            parent = parent.parent;
        }

        return true;
    }

    boolean upgrade(String name, int user) {

        Node node = map.get(name);
        if(node == null || node.isLocked || node.lockedDecendants.isEmpty() || hasLockedAncestor(node)) 
            return false;

        for(Node des : node.lockedDecendants) {
            if(des.lockedBy != user)
                return false;
        }

        List<Node> copy = new ArrayList<>(node.lockedDecendants);
        for(Node des : copy) {
            unlock(des.name, des.lockedBy);
        }

        node.isLocked = true;
        node.lockedBy = user;

        Node parent = node.parent;
        while (parent != null) {
            parent.lockedDecendants.add(node);
            parent = parent.parent;
        }

        return true;
    }

    boolean hasLockedAncestor(Node node) {

        Node parent = node.parent;
        while(parent != null) { 
            if(parent.isLocked)
                return true;
            parent = parent.parent;
        }

        return false;
    }
}

public class TreeOfLockingSpace{
    public static void main (String args[]) {
        
        try(Scanner sc = new Scanner (System.in)){
            int n = sc.nextInt();
            int m = sc.nextInt();
            sc.nextLine();

            String nodes[] = new String[n];
            for(int i=0;i<n;i++) 
                nodes[i] = sc.nextLine();
            TreeOfLocking tol = new TreeOfLocking(nodes, m, n);
            
            int tc = sc.nextInt();

            while(tc-->0) {
                int query = sc.nextInt();
                String name = sc.next();
                int user = sc.nextInt();

                switch(query) {
                    case 1 ->  System.out.println(tol.lock(name, user));
                    case 2 ->  System.out.println(tol.unlock(name, user));
                    case 3 ->  System.out.println(tol.upgrade(name, user));
                    default -> System.out.println(false);
                }
            }
        }
    }
}