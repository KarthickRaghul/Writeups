import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Node {
    boolean isLocked;
    int lockedBy;
    Node parent;
    AtomicInteger lockedDescendantCount;
    ReentrantReadWriteLock lock;

    ArrayList<Node> descendants ;

    Node() {
        descendants = new ArrayList<>();
        lockedDescendantCount = new AtomicInteger();
        lock = new ReentrantReadWriteLock();
    }
}

class TreeOfLocking {
    HashMap<String, Node> map;

    public TreeOfLocking(String[] names, int m, int n) {
        map = new HashMap<>();
        Node[] nodes = new Node[n];
        
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            map.put(names[i], nodes[i]);
        
            if(i != 0) {
                int p = (i - 1) / m;
                nodes[i].parent = nodes[p];
                nodes[p].descendants.add(nodes[i]);
            }
        }
    }

    ArrayList getPath(Node curr) {
        curr = curr.parent;
        ArrayList<Node> path = new ArrayList<>();
        while(curr != null) {
            path.add(curr);
            curr = curr.parent;
        }
        Collections.reverse(path);
        return path;
    }

    boolean lock (String name, int user) {
        Node curr = map.get(name);
        if(curr == null)
            return false;
        
        ArrayList<Node> path = getPath(curr);
        for(Node temp : path) {
            temp.lock.readLock().lock();
        }
        curr.lock.writeLock().lock();

        try {
            
            if(curr.isLocked || curr.lockedDescendantCount.get() != 0)
                return false;

            Node temp = curr.parent;
            while(temp != null) {
                if(temp.isLocked)
                    return false;
                temp = temp.parent;
            }

            curr.isLocked = true;
            curr.lockedBy = user;
            
            temp = curr.parent;
            while(temp != null) {
                temp.lockedDescendantCount.incrementAndGet();
                temp = temp.parent;
            }
            return true;
        }
        finally {
            curr.lock.writeLock().unlock();
            for(Node temp : path) {
                temp.lock.readLock().unlock();
            }
        }
    }

    boolean unlock (String name, int user) {
        Node curr = map.get(name);
        if(curr == null)
            return false;
        
        ArrayList<Node> path = getPath(curr);
        for(Node temp : path) {
            temp.lock.readLock().lock();
        }
        curr.lock.writeLock().lock();

        try {
            
            if(!curr.isLocked || curr.lockedBy != user)
                return false;

            curr.isLocked = false;
            curr.lockedBy = -1;
            
            Node temp = curr.parent;
            while(temp != null) {
                temp.lockedDescendantCount.decrementAndGet();
                temp = temp.parent;
            }
            return true;
        }
        finally {
            curr.lock.writeLock().unlock();
            for(Node temp : path) {
                temp.lock.readLock().unlock();
            }
        }
    }

    boolean upgrade (String name, int user) {

        Node curr = map.get(name);
        if(curr == null)
            return false;
        
        ArrayList<Node> path = getPath(curr);
        for(Node temp : path) {
            temp.lock.readLock().lock();
        }
        curr.lock.writeLock().lock();

        try {
            
            if(curr.isLocked || curr.lockedDescendantCount.get() == 0)
                return false;

            for(Node temp : path) {
                if(temp.isLocked)
                    return false;
            }

            List<Node> lockedDescendants = new ArrayList<>();
            if(!verifyAndGather(curr, user, lockedDescendants))
                return false;

            for(Node desc : lockedDescendants) {
                desc.isLocked = false;
                desc.lockedBy = -1;

                Node temp = desc.parent; 
                while(temp != curr) {
                    temp.lockedDescendantCount.decrementAndGet();
                    temp = temp.parent;
                }
            }

            curr.isLocked = true;
            curr.lockedBy = user;
            curr.lockedDescendantCount.set(0);
            
            int diff = 1 - lockedDescendants.size();
            for(Node temp : path) {
                temp.lockedDescendantCount.addAndGet(diff);
            }
            return true;
        }
        finally {
            curr.lock.writeLock().unlock();
            for(Node temp : path) {
                temp.lock.readLock().unlock();
            }
        }
    }

    boolean verifyAndGather(Node curr,int user, List<Node> lockedDescendants) {

        if (curr.isLocked) {
            if (curr.lockedBy != user) return false;
            lockedDescendants.add(curr);
        }

        if(curr.lockedDescendantCount.get() == 0)
            return true;

        for(Node child : curr.descendants) {
            if(child.isLocked || child.lockedDescendantCount.get() > 0)
                if(!verifyAndGather(child, user, lockedDescendants))
                    return false;
        }
        return true;
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

public class ReEntrantAndAtomic {
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