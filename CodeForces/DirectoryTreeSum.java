import java.util.*;

class Node {

    String name;
    String path;
    Node parent;

    ArrayList<Node> children = new ArrayList<>();

    int descendants;

    Node(String name, String path) {
        this.name = name;
        this.path = path;
    }
}

class DirectoryTree {

    HashMap<String, Node> map = new HashMap<>();
    Node root;

    DirectoryTree(String rootName) {
        root = new Node(rootName, rootName);
        map.put(rootName, root);
    }

    Node getNode(String path) {
        return map.get(path);
    }

    void add(String parentPath, String name) {

        Node parent = getNode(parentPath);

        String path = parentPath + "/" + name;

        Node child = new Node(name, path);
        child.parent = parent;

        parent.children.add(child);
        map.put(path, child);

        Node curr = parent;

        while (curr != null) {
            curr.descendants++;
            curr = curr.parent;
        }
    }

    int countDescendants(String path) {
        return getNode(path).descendants;
    }

    boolean isInside(Node node, Node parent) {

        while (node != null) {
            if (node == parent)
                return true;

            node = node.parent;
        }

        return false;
    }

    void cutPaste(String srcPath, String destPath) {

        Node src = getNode(srcPath);
        Node dest = getNode(destPath);

        if (src == null || dest == null || src == root)
            return;

        // Cannot move a directory inside itself
        if (isInside(dest, src))
            return;

        int size = src.descendants + 1;

        // Remove from old parent
        Node oldParent = src.parent;
        oldParent.children.remove(src);

        // Old ancestors lose this subtree
        Node curr = oldParent;

        while (curr != null) {
            curr.descendants -= size;
            curr = curr.parent;
        }

        // Add to new parent
        dest.children.add(src);
        src.parent = dest;

        // New ancestors gain this subtree
        curr = dest;

        while (curr != null) {
            curr.descendants += size;
            curr = curr.parent;
        }

        // Update paths in map
        updatePaths(src, srcPath, dest.path + "/" + src.name);
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

        if (src == null || dest == null)
            return;

        if (isInside(dest, src))
            return;

        String newPath = dest.path + "/" + src.name;

        Node copy = copyTree(src, dest, newPath);

        dest.children.add(copy);

        int size = copy.descendants + 1;

        Node curr = dest;

        while (curr != null) {
            curr.descendants += size;
            curr = curr.parent;
        }
    }

    Node copyTree(Node node, Node parent, String path) {

        Node copy = new Node(node.name, path);
        copy.parent = parent;

        map.put(path, copy);

        for (Node child : node.children) {

            String childPath = path + "/" + child.name;

            Node childCopy = copyTree(
                    child,
                    copy,
                    childPath
            );

            copy.children.add(childCopy);

            copy.descendants += childCopy.descendants + 1;
        }

        return copy;
    }
}

public class DirectoryTreeSum {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        String rootName = sc.next();

        DirectoryTree tree = new DirectoryTree(rootName);

        for (int i = 1; i < n; i++) {

            String parent = sc.next();
            String child = sc.next();

            tree.add(parent, child);
        }

        int q = sc.nextInt();

        while (q-- > 0) {

            String operation = sc.next();

            if (operation.equals("count")) {

                String path = sc.next();

                System.out.println(
                        tree.countDescendants(path)
                );

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