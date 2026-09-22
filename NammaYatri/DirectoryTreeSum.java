import java.io.*;
import java.util.*;

class Node {
    String name;
    Node parent;
    // Using a HashMap for children gives O(1) child lookups when traversing paths
    HashMap<String, Node> children;
    int descendants;

    Node(String name) {
        this.name = name;
        this.children = new HashMap<>();
        this.descendants = 0;
    }
}

class DirectoryTree {
    Node root;

    DirectoryTree(String rootName) {
        root = new Node(rootName);
    }

    // O(depth) lookup instead of O(1) global hashmap.
    // This allows cutPaste to be O(1) for the subtree, preventing TLE.
    Node getNode(String path) {
        String[] parts = path.split("/");
        if (!parts[0].equals(root.name)) return null;
        
        Node curr = root;
        for (int i = 1; i < parts.length; i++) {
            curr = curr.children.get(parts[i]);
            if (curr == null) return null; // Path doesn't exist
        }
        return curr;
    }

    void add(String parentPath, String name) {
        Node parent = getNode(parentPath);
        if (parent == null) return;

        Node child = new Node(name);
        child.parent = parent;
        parent.children.put(name, child);

        // Update descendant counts for all ancestors O(height)
        Node curr = parent;
        while (curr != null) {
            curr.descendants++;
            curr = curr.parent;
        }
    }

    int countDescendants(String path) {
        Node node = getNode(path);
        return node == null ? -1 : node.descendants;
    }

    boolean isInside(Node dest, Node src) {
        Node curr = dest;
        while (curr != null) {
            if (curr == src) return true;
            curr = curr.parent;
        }
        return false;
    }

    void cutPaste(String srcPath, String destPath) {
        Node src = getNode(srcPath);
        Node dest = getNode(destPath);

        if (src == null || dest == null || src == root || isInside(dest, src)) return;

        int subtreeSize = src.descendants + 1;

        // 1. Remove from old parent and update old ancestors' descendant counts
        Node oldParent = src.parent;
        oldParent.children.remove(src.name);
        
        Node curr = oldParent;
        while (curr != null) {
            curr.descendants -= subtreeSize;
            curr = curr.parent;
        }

        // 2. Add to new parent and update new ancestors' descendant counts
        dest.children.put(src.name, src);
        src.parent = dest;
        
        curr = dest;
        while (curr != null) {
            curr.descendants += subtreeSize;
            curr = curr.parent;
        }
        // Notice we NO LONGER need updatePaths() - massive performance boost!
    }

    void copyPaste(String srcPath, String destPath) {
        Node src = getNode(srcPath);
        Node dest = getNode(destPath);

        if (src == null || dest == null || isInside(dest, src)) return;

        Node copy = cloneTree(src, dest);
        dest.children.put(copy.name, copy);

        int subtreeSize = copy.descendants + 1;

        // Update ancestors of the destination
        Node curr = dest;
        while (curr != null) {
            curr.descendants += subtreeSize;
            curr = curr.parent;
        }
    }

    // O(K) where K is the size of the subtree being copied
    Node cloneTree(Node node, Node parent) {
        Node copy = new Node(node.name);
        copy.parent = parent;

        for (Node child : node.children.values()) {
            Node childCopy = cloneTree(child, copy);
            copy.children.put(childCopy.name, childCopy);
            copy.descendants += childCopy.descendants + 1;
        }
        return copy;
    }
}

public class DirectoryTreeSum {
    // Fast I/O is MANDATORY for Juspay
    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    String line = br.readLine();
                    if (line == null) return null;
                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    public static void main(String[] args) {
        FastReader sc = new FastReader();
        
        String nStr = sc.next();
        if (nStr == null) return;
        int n = Integer.parseInt(nStr);
        
        String rootName = sc.next();
        DirectoryTree tree = new DirectoryTree(rootName);

        for (int i = 1; i < n; i++) {
            String parentPath = sc.next();
            String childName = sc.next();
            tree.add(parentPath, childName);
        }

        int q = sc.nextInt();
        while (q-- > 0) {
            String operation = sc.next();
            if (operation.equals("count")) {
                String path = sc.next();
                System.out.println(tree.countDescendants(path));
            } else if (operation.equals("cut")) {
                String src = sc.next();
                String dest = sc.next();
                tree.cutPaste(src, dest);
            } else if (operation.equals("copy")) {
                String src = sc.next();
                String dest = sc.next();
                tree.copyPaste(src, dest);
            }
        }
    }
}