import java.util.*;

class Node {
    String name;
    String path;
    Node parent;
    ArrayList<Node> children;
    int descendants;

    Node(String name, String path) {
        this.name = name;
        this.path = path;
        this.children = new ArrayList<>();
        this.descendants = 0;
    }
}

class DirectoryTree {
    HashMap<String, Node> map;
    Node root;

    DirectoryTree(String rootName) {
        map = new HashMap<>();
        root = new Node(rootName, rootName);
        map.put(rootName, root);
    }

    Node getNode(String path) {
        return map.get(path);
    }

    void add(String parentPath, String name) {
        Node parent = getNode(parentPath);
        if (parent == null) return;

        String path = parentPath + "/" + name;
        Node child = new Node(name, path);
        child.parent = parent;
        parent.children.add(child);
        map.put(path, child);

        // Update descendant counts for all ancestors
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

    // Checks if 'dest' is a descendant of 'src' (or the same node)
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

        // Edge cases: nodes don't exist, moving root, or moving into itself/child
        if (src == null || dest == null || src == root || isInside(dest, src)) return;

        int subtreeSize = src.descendants + 1;

        // 1. Remove from old parent and update old ancestors' descendant counts
        Node oldParent = src.parent;
        oldParent.children.remove(src);
        Node curr = oldParent;
        while (curr != null) {
            curr.descendants -= subtreeSize;
            curr = curr.parent;
        }

        // 2. Add to new parent and update new ancestors' descendant counts
        dest.children.add(src);
        src.parent = dest;
        curr = dest;
        while (curr != null) {
            curr.descendants += subtreeSize;
            curr = curr.parent;
        }

        // 3. Recursively update the paths in the HashMap for the moved subtree
        String newSrcPath = dest.path + "/" + src.name;
        updatePaths(src, srcPath, newSrcPath);
    }

    void updatePaths(Node node, String oldPath, String newPath) {
        map.remove(oldPath);
        node.path = newPath;
        map.put(newPath, node);

        for (Node child : node.children) {
            String oldChildPath = oldPath + "/" + child.name;
            String newChildPath = newPath + "/" + child.name;
            updatePaths(child, oldChildPath, newChildPath);
        }
    }

    void copyPaste(String srcPath, String destPath) {
        Node src = getNode(srcPath);
        Node dest = getNode(destPath);

        if (src == null || dest == null || isInside(dest, src)) return;

        String newPath = dest.path + "/" + src.name;
        Node copy = cloneTree(src, dest, newPath);
        dest.children.add(copy);

        int subtreeSize = copy.descendants + 1;

        // Update ancestors of the destination
        Node curr = dest;
        while (curr != null) {
            curr.descendants += subtreeSize;
            curr = curr.parent;
        }
    }

    Node cloneTree(Node node, Node parent, String path) {
        Node copy = new Node(node.name, path);
        copy.parent = parent;
        map.put(path, copy);

        for (Node child : node.children) {
            String childPath = path + "/" + child.name;
            Node childCopy = cloneTree(child, copy, childPath);
            copy.children.add(childCopy);
            copy.descendants += childCopy.descendants + 1;
        }
        return copy;
    }
}

public class DirectoryTreeSum {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (!sc.hasNextInt()) return;

        int n = sc.nextInt();
        String rootName = sc.next();
        DirectoryTree tree = new DirectoryTree(rootName);

        // Reading N-1 edges to construct the initial tree
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
        sc.close();
    }
}