import java.io.*;
import java.util.*;

class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    Set<Node> lockedDescendants = new HashSet<>();

    Node(String name) {
        this.name = name;
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
        }
    }

    boolean lock (String name, int user) {
        Node curr = map.get(name);
        // System.out.println("lock " + curr.name);
        if(curr == null || curr.isLocked || !curr.lockedDescendants.isEmpty() || hasLockedAncestors(curr))
            return false;
            
        curr.isLocked = true;
        curr.lockedBy = user;
        
        Node temp = curr.parent;
        while(temp != null) {
            temp.lockedDescendants.add(curr);
            temp = temp.parent;
        }
        return true;
    }

    boolean unlock (String name, int user) {
        Node curr = map.get(name);
        // System.out.println("unlock " + curr.name);
        if(curr == null || !curr.isLocked || curr.lockedBy != user)
            return false;
            
        curr.isLocked = false;
        curr.lockedBy = -1;
        
        Node temp = curr.parent;
        while(temp != null) {
            temp.lockedDescendants.remove(curr);
            temp = temp.parent;
        }
        return true;
    }

    boolean upgrade (String name, int user) {
        Node curr = map.get(name);
        if(curr == null || curr.isLocked || curr.lockedDescendants.isEmpty() || hasLockedAncestors(curr))
            return false;
            
        for (Node des : curr.lockedDescendants) {
            if (des.lockedBy != user)
                return false;
        }
                
        List<Node> copy = new ArrayList<>(curr.lockedDescendants);
        for(Node des : copy)
            unlock(des.name, user);
            
        curr.isLocked = true;
        curr.lockedBy = user;
        
        Node temp = curr.parent;
        while(temp != null) {
            temp.lockedDescendants.add(curr);
            temp = temp.parent;
        }
        
        return true;
    }

    boolean hasLockedAncestors(Node curr) {
        Node temp = curr.parent;
        while(temp != null) {
            if(temp.isLocked)
                return true;
            temp = temp.parent;
        }
        return false;
    }
}

class FastReader {
    BufferedReader br ;
    StringTokenizer st;
    
    FastReader() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
    String next() {
        while( st == null || !st.hasMoreTokens()) {
            try{
                String line = br.readLine();
                if(line == null)
                    return null;
                st = new StringTokenizer(line);
            }
            catch(IOException e){
                return null;
            }
        }
        return st.nextToken();
    }
    
    int nextInt() {
        return Integer.parseInt(next());
    }
}

class TestClass {
    public static void main(String args[]) throws Exception {
        FastReader sc = new FastReader();
        int n = sc.nextInt();
        int m = sc.nextInt();
        int tc = sc.nextInt();
        // sc.nextLine();
        
        String names[] = new String[n];
        for(int i=0;i<n;i++) {
            names[i] = sc.next();
        }
        
        TreeOfLocking tol = new TreeOfLocking(names, m, n) ;
        
        StringBuilder op = new StringBuilder();
        
        while(tc --> 0) {
            int query = sc.nextInt();
            String name = sc.next();
            int user = sc.nextInt();
            
            switch(query) {
                case 1 :
                    if(tol.lock(name, user))
                        op.append(true).append("\n");
                    else
                        op.append(false).append("\n");
                    break;
                case 2 :
                    if(tol.unlock(name, user))
                        op.append(true).append("\n");
                    else
                        op.append(false).append("\n");
                    break;
                case 3 :
                    if(tol.upgrade(name, user))
                        op.append(true).append("\n");
                    else
                        op.append(false).append("\n");
                    break;
            }
        }
        
        System.out.println(op);
    }
}