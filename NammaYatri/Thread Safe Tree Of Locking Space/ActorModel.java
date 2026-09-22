import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// 1. The pure, non-concurrent Node class
class Node {
    String name;
    boolean isLocked;
    int lockedBy;
    Node parent;
    List<Node> children;
    
    // Standard int, no Atomic variables needed
    int lockedDescendantCount;

    Node(String name) {
        this.name = name;
        this.isLocked = false;
        this.lockedBy = -1;
        this.lockedDescendantCount = 0;
        this.children = new ArrayList<>();
    }
}

// 2. The Request Object (The "Message" passed to the Actor)
class LockRequest {
    int type; // 1: lock, 2: unlock, 3: upgrade
    String name;
    int user;
    
    // This allows the single Actor thread to send the boolean result 
    // back to the specific client thread that submitted the request.
    CompletableFuture<Boolean> future;

    public LockRequest(int type, String name, int user) {
        this.type = type;
        this.name = name;
        this.user = user;
        this.future = new CompletableFuture<>();
    }
}

class ActorTree {
    // Standard, non-concurrent HashMap. Only the Actor thread will touch this.
    private final HashMap<String, Node> map = new HashMap<>();
    
    // The Inbox: A lock-free concurrent queue where client threads drop messages
    private final ConcurrentLinkedQueue<LockRequest> inbox = new ConcurrentLinkedQueue<>();

    public ActorTree(String[] names, int m, int n) {
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
        
        // Start the single Actor thread
        Thread actorThread = new Thread(this::processInbox, "Tree-Actor-Thread");
        actorThread.setDaemon(true); // Allows JVM to exit if this is the only thread left
        actorThread.start();
    }

    // =========================================================================
    // PUBLIC API (Called by hundreds of concurrent client threads)
    // =========================================================================

    public boolean lock(String name, int user) {
        return submitRequest(1, name, user);
    }

    public boolean unlock(String name, int user) {
        return submitRequest(2, name, user);
    }

    public boolean upgrade(String name, int user) {
        return submitRequest(3, name, user);
    }

    private boolean submitRequest(int type, String name, int user) {
        LockRequest req = new LockRequest(type, name, user);
        inbox.offer(req); // Drop message in the queue (non-blocking)
        
        try {
            // Block the client thread until the Actor completes the future
            return req.future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // =========================================================================
    // THE ACTOR (Runs on exactly ONE background thread)
    // =========================================================================

    private void processInbox() {
        while (true) {
            LockRequest req = inbox.poll();
            if (req != null) {
                boolean result = false;
                
                // Route the request to standard, single-threaded logic
                if (req.type == 1) {
                    result = doLock(req.name, req.user);
                } else if (req.type == 2) {
                    result = doUnlock(req.name, req.user);
                } else if (req.type == 3) {
                    result = doUpgrade(req.name, req.user);
                }
                
                // Complete the future, waking up the waiting client thread
                req.future.complete(result);
            } else {
                // If queue is empty, yield to prevent 100% CPU burn on an empty loop
                Thread.yield(); 
            }
        }
    }

    // =========================================================================
    // INTERNAL LOGIC (100% Lock-Free, Single-Threaded execution)
    // =========================================================================

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

    private boolean doLock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null || curr.isLocked || curr.lockedDescendantCount > 0) return false;

        List<Node> path = getPath(curr);
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).isLocked) return false;
        }

        curr.isLocked = true;
        curr.lockedBy = user;
        
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).lockedDescendantCount++;
        }
        return true;
    }

    private boolean doUnlock(String name, int user) {
        Node curr = map.get(name);
        if (curr == null || !curr.isLocked || curr.lockedBy != user) return false;

        curr.isLocked = false;
        curr.lockedBy = -1;
        
        Node temp = curr.parent;
        while (temp != null) {
            temp.lockedDescendantCount--;
            temp = temp.parent;
        }
        return true;
    }

    private boolean doUpgrade(String name, int user) {
        Node curr = map.get(name);
        if (curr == null || curr.isLocked || curr.lockedDescendantCount == 0) return false;

        List<Node> path = getPath(curr);
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).isLocked) return false;
        }

        List<Node> lockedNodes = new ArrayList<>();
        if (!verifyAndGatherLocked(curr, user, lockedNodes)) return false;

        for (Node des : lockedNodes) {
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
        
        int netChange = 1 - lockedNodes.size();
        for (int i = 0; i < path.size() - 1; i++) {
            path.get(i).lockedDescendantCount += netChange;
        }
        return true;
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

// ---------------------------------------------------------------------------
// Standard IO for Competitive Programming / Platform Testing
// ---------------------------------------------------------------------------
class FastReader {
    BufferedReader br;
    StringTokenizer st;
    
    FastReader() { br = new BufferedReader(new InputStreamReader(System.in)); }
    
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

public class ActorModel {
    public static void main(String[] args) throws Exception {
        FastReader sc = new FastReader();
        int n = sc.nextInt();
        int m = sc.nextInt();
        int tc = sc.nextInt();
        
        String[] names = new String[n];
        for (int i = 0; i < n; i++) names[i] = sc.next();
        
        ActorTree tree = new ActorTree(names, m, n);
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