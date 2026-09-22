import java.io.*;
import java.util.*;

// 1. Custom Read-Write Lock using core Java primitives
class CustomRWLock {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0; // Prevents writer starvation

    public synchronized void acquireRead() throws InterruptedException {
        // Wait if there is an active writer OR a writer waiting in line
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public synchronized void releaseRead() {
        readers--;
        if (readers == 0) {
            notifyAll(); // Wake up any waiting writers
        }
    }

    public synchronized void acquireWrite() throws InterruptedException {
        writeRequests++;
        // Wait until all readers and active writers are gone
        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void releaseWrite() {
        writers--;
        notifyAll(); // Wake up waiting readers and writers
    }
}

// 2. Node class with localized synchronization for thread safety
class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    
    CustomRWLock rwLock;
    private int lockedDescendantCount; // Replaces AtomicInteger

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.lockedDescendantCount = 0;
        this.rwLock = new CustomRWLock();
        this.children = new ArrayList<>();
    }

    // Localized synchronized methods replace AtomicInteger operations
    synchronized void addCount(int val) {
        this.lockedDescendantCount += val;
    }

    synchronized int getCount() {
        return this.lockedDescendantCount;
    }

    synchronized void setCount(int val) {
        this.lockedDescendantCount = val;
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
        int lockedLevel = -1;
        boolean writeLocked = false;
        
        try {
            // 1. Acquire Read Locks Top-Down
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).rwLock.acquireRead();
                lockedLevel = i;
            }
            // Acquire Write Lock on target
            curr.rwLock.acquireWrite();
            writeLocked = true;

            // 2. Validate state
            if (curr.isLocked || curr.getCount() > 0) 
                return false;
                
            for (int i = 0; i < path.size() - 1; i++) {
                if (path.get(i).isLocked) 
                    return false;
            }
            
            // 3. Execute lock
            curr.isLocked = true;
            curr.lockedBy = user;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).addCount(1);
            }
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // 4. Safely Release Locks Bottom-Up based on exactly what was acquired
            if (writeLocked) curr.rwLock.releaseWrite();
            for (int i = lockedLevel; i >= 0; i--) {
                path.get(i).rwLock.releaseRead();
            }
        }
    }

    boolean unlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        int lockedLevel = -1;
        boolean writeLocked = false;
        
        try {
            // 1. Acquire locks
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).rwLock.acquireRead();
                lockedLevel = i;
            }
            curr.rwLock.acquireWrite();
            writeLocked = true;

            // 2. Validate state
            if (!curr.isLocked || curr.lockedBy != user) 
                return false;
                
            // 3. Execute unlock
            curr.isLocked = false;
            curr.lockedBy = -1;
            
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).addCount(-1);
            }
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // 4. Safely Release Locks
            if (writeLocked) curr.rwLock.releaseWrite();
            for (int i = lockedLevel; i >= 0; i--) {
                path.get(i).rwLock.releaseRead();
            }
        }
    }

    boolean upgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null) return false;

        List<Node> path = getPath(curr);
        int lockedLevel = -1;
        boolean writeLocked = false;
        
        try {
            // 1. Acquire locks
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).rwLock.acquireRead();
                lockedLevel = i;
            }
            curr.rwLock.acquireWrite();
            writeLocked = true;

            // 2. Validate state
            if (curr.isLocked || curr.getCount() == 0) 
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
                    temp.addCount(-1);
                    temp = temp.parent;
                }
            }
            
            curr.isLocked = true;
            curr.lockedBy = user;
            curr.setCount(0); // Reset since all locked descendants are now unlocked
            
            // Adjust ancestor counts based on the net change of locked nodes in the subtree
            int netChange = 1 - lockedDescendants.size();
            for (int i = 0; i < path.size() - 1; i++) {
                path.get(i).addCount(netChange);
            }
            
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // 4. Safely Release Locks
            if (writeLocked) curr.rwLock.releaseWrite();
            for (int i = lockedLevel; i >= 0; i--) {
                path.get(i).rwLock.releaseRead();
            }
        }
    }

    // Traversal is 100% thread-safe because the Write Lock on curr ensures 
    // no other thread can acquire locks on any descendants.
    private boolean verifyAndGatherLocked(Node node, int user, List<Node> lockedNodes) {
        if (node.isLocked) {
            if (node.lockedBy != user) return false;
            lockedNodes.add(node);
        }
        
        if (node.getCount() == 0) return true; // Pruned DFS
        
        for (Node child : node.children) {
            if (child.isLocked || child.getCount() > 0) {
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
    
    FastReader() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
    String next() {
        while (st == null || !st.hasMoreTokens()) {
            try {
                String line = br.readLine();
                if (line == null) return null;
                st = new StringTokenizer(line);
            } catch(IOException e) {
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
        
        String names[] = new String[n];
        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
        }
        
        TreeOfLocking tol = new TreeOfLocking(names, m, n);
        StringBuilder op = new StringBuilder();
        
        while (tc-- > 0) {
            int query = sc.nextInt();
            String name = sc.next();
            int user = sc.nextInt();
            
            switch (query) {
                case 1 :
                    op.append(tol.lock(name, user)).append("\n");
                    break;
                case 2 :
                    op.append(tol.unlock(name, user)).append("\n");
                    break;
                case 3 :
                    op.append(tol.upgrade(name, user)).append("\n");
                    break;
            }
        }
        System.out.print(op);
    }
}