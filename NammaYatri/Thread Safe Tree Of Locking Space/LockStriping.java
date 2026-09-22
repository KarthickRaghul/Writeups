import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    
    // Notice: We removed all atomic variables and locks from the Node class.
    // It is just a plain, lightweight data structure.
    int lockedDescendantCount;

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.lockedDescendantCount = 0;
        this.children = new ArrayList<>();
    }
}

class StripedLockTree {
    private final HashMap<String, Node> map;
    
    // Fixed array of locks to drastically reduce memory footprint
    private final int NUM_LOCKS = 256;
    private final ReentrantLock[] locks = new ReentrantLock[NUM_LOCKS];

    public StripedLockTree(String[] names, int m, int n) {
        map = new HashMap<>();
        
        for (int i = 0; i < NUM_LOCKS; i++) {
            locks[i] = new ReentrantLock();
        }

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

    // 1. Helper to gather, deduplicate, and SORT locks to prevent deadlocks
    private List<Integer> getSortedLockIndices(List<Node> path) {
        Set<Integer> uniqueIndices = new HashSet<>();
        for (Node node : path) {
            // Ensure positive hash code
            int hash = (node.name.hashCode() & 0x7fffffff) % NUM_LOCKS;
            uniqueIndices.add(hash);
        }
        List<Integer> sortedIndices = new ArrayList<>(uniqueIndices);
        Collections.sort(sortedIndices);
        return sortedIndices;
    }

    public boolean lock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        List<Integer> lockIndices = getSortedLockIndices(path);

        // 2. Acquire locks strictly in ascending order
        for (int idx : lockIndices) {
            locks[idx].lock();
        }

        try {
            // --- CRITICAL SECTION (100% Safe to use plain variables) ---
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
            // 3. Release locks in descending order
            for (int i = lockIndices.size() - 1; i >= 0; i--) {
                locks[lockIndices.get(i)].unlock();
            }
        }
    }

    public boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        List<Integer> lockIndices = getSortedLockIndices(path);

        for (int idx : lockIndices) locks[idx].lock();

        try {
            if (!curr.isLocked || curr.lockedBy != user) return false;

            curr.isLocked = false;
            curr.lockedBy = -1;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).lockedDescendantCount--;
            }
            return true;

        } finally {
            for (int i = lockIndices.size() - 1; i >= 0; i--) {
                locks[lockIndices.get(i)].unlock();
            }
        }
    }

    public boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        List<Integer> lockIndices = getSortedLockIndices(path);

        // Acquiring these locks effectively freezes the entire subtree
        for (int idx : lockIndices) locks[idx].lock();

        try {
            if (curr.isLocked || curr.lockedDescendantCount == 0) return false;
            
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) return false;
            }

            List<Node> lockedDescendants = new ArrayList<>();
            if (!verifyAndGatherLocked(curr, user, lockedDescendants)) return false;

            // Mutate descendants safely
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
            for (int i = lockIndices.size() - 1; i >= 0; i--) {
                locks[lockIndices.get(i)].unlock();
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
                if (!verifyAndGatherLocked(child, user, lockedNodes)) {
                    return false;
                }
            }
        }
        return true;
    }
}

class FastReader {
    BufferedReader br;
    StringTokenizer st;
    
    FastReader(){
        br = new BufferedReader(new InputStreamReader(System.in)); 
    }
    
    String next() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                String line = br.readLine();
                if (line == null) return null;
                st = new StringTokenizer(line);
            } catch(IOException e) { return null; }
        }
        return st.nextToken();
    }
    int nextInt() { return Integer.parseInt(next()); }
}

public class LockStriping {
    public static void main(String[] args) throws Exception {
        FastReader sc = new FastReader();
        int n = sc.nextInt();
        int m = sc.nextInt();
        int tc = sc.nextInt();
        
        String[] names = new String[n];
        for (int i = 0; i < n; i++) names[i] = sc.next();
        
        StripedLockTree tree = new StripedLockTree(names, m, n);
        StringBuilder out = new StringBuilder();
        
        while (tc-- > 0) {
            int query = sc.nextInt();
            String name = sc.next();
            int user = sc.nextInt();
            
            if (query == 1) out.append(tree.lock(name, user)).append("\n");
            else if (query == 2) out.append(tree.unlock(name, user)).append("\n");
            else if (query == 3) out.append(tree.upgrade(name, user)).append("\n");
        }
        System.out.print(out);
    }
}