
```java
class Node {
    Node[] child = new Node[26];
    boolean isEnd;
}

class Trie {

    Node root;

    public Trie() {
        root = new Node();
    }

    private Node find(String s) {
        Node curr = root;

        for (char c : s.toCharArray()) {
            int idx = c - 'a';

            if (curr.child[idx] == null)
                return null;

            curr = curr.child[idx];
        }

        return curr;
    }

    public void insert(String word) {
        Node curr = root;

        for (char c : word.toCharArray()) {
            int idx = c - 'a';

            if (curr.child[idx] == null)
                curr.child[idx] = new Node();

            curr = curr.child[idx];
        }

        curr.isEnd = true;
    }

    public boolean search(String word) {
        Node node = find(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }
}
```