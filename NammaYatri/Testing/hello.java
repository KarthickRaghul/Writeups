import java.util.*;

class TreeNode {

    String name;
    TreeNode parent;
    List<TreeNode> children = new ArrayList<>();

    boolean locked = false;
    int lockedBy = -1;

    // Number of locked descendants
    int lockedDescendantCount = 0;

    TreeNode(String name) {
        this.name = name;
    }
}


class LockingTree {

    private final Object treeLock = new Object();

    // -----------------------------------------
    // ADD CHILD
    // -----------------------------------------

    public void addChild(TreeNode parent, TreeNode child) {

        synchronized (treeLock) {
            child.parent = parent;
            parent.children.add(child);
        }
    }


    // -----------------------------------------
    // LOCK NODE
    // -----------------------------------------

    public boolean lock(TreeNode node, int userId) {

        synchronized (treeLock) {

            // Node already locked
            if (node.locked) {
                return false;
            }

            // Some descendant is locked
            if (node.lockedDescendantCount > 0) {
                return false;
            }

            // Some ancestor is locked
            if (hasLockedAncestor(node)) {
                return false;
            }

            // ---------------------------------
            // Critical update
            // ---------------------------------

            node.locked = true;
            node.lockedBy = userId;

            // Update ancestors
            TreeNode current = node.parent;

            while (current != null) {
                current.lockedDescendantCount++;
                current = current.parent;
            }

            return true;
        }
    }


    // -----------------------------------------
    // UNLOCK NODE
    // -----------------------------------------

    public boolean unlock(TreeNode node, int userId) {

        synchronized (treeLock) {

            if (!node.locked) {
                return false;
            }

            // Only owner can unlock
            if (node.lockedBy != userId) {
                return false;
            }

            node.locked = false;
            node.lockedBy = -1;

            // Update ancestors
            TreeNode current = node.parent;

            while (current != null) {
                current.lockedDescendantCount--;
                current = current.parent;
            }

            return true;
        }
    }


    // -----------------------------------------
    // CHECK LOCKED ANCESTOR
    // -----------------------------------------

    private boolean hasLockedAncestor(TreeNode node) {

        TreeNode current = node.parent;

        while (current != null) {

            if (current.locked) {
                return true;
            }

            current = current.parent;
        }

        return false;
    }
}


public class hello{
    public static void main(String[] args) throws InterruptedException {

        /*
                   A
                  / \
                 B   C
                / \
               D   E
        */

        TreeNode A = new TreeNode("A");
        TreeNode B = new TreeNode("B");
        TreeNode C = new TreeNode("C");
        TreeNode D = new TreeNode("D");
        TreeNode E = new TreeNode("E");

        LockingTree tree = new LockingTree();

        tree.addChild(A, B);
        tree.addChild(A, C);
        tree.addChild(B, D);
        tree.addChild(B, E);


        // -----------------------------------------
        // THREAD 1
        // -----------------------------------------

        Thread t1 = new Thread(() -> {

            boolean result = tree.lock(D, 101);

            System.out.println(
                "Thread 1 -> Lock D -> " + result
            );
        });


        // -----------------------------------------
        // THREAD 2
        // -----------------------------------------

        Thread t2 = new Thread(() -> {

            boolean result = tree.lock(B, 102);

            System.out.println(
                "Thread 2 -> Lock B -> " + result
            );
        });


        // -----------------------------------------
        // THREAD 3
        // -----------------------------------------

        Thread t3 = new Thread(() -> {

            boolean result = tree.lock(A, 103);

            System.out.println(
                "Thread 3 -> Lock A -> " + result
            );
        });


        // Start all threads
        t1.start();
        t2.start();
        t3.start();


        // Wait for all threads
        t1.join();
        t2.join();
        t3.join();


        System.out.println("\nFinal state:");

        System.out.println(
            "A locked = " + A.locked
        );

        System.out.println(
            "B locked = " + B.locked
        );

        System.out.println(
            "D locked = " + D.locked
        );
    }
}