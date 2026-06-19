package org.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * A generic, mutable binary search tree.
 *
 * <p>Traversal methods return {@link List} snapshots rather than printing, so callers
 * (and tests) can assert on the exact resulting order without capturing stdout.
 *
 * @param <T> element type, must define a total order via {@link Comparable}
 */
public class BinarySearchTree<T extends Comparable<T>> {

    private static final class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Inserts a value, ignoring duplicates.
     *
     * @return true if the value was inserted, false if it already existed
     */
    public boolean insert(T value) {
        Objects.requireNonNull(value, "value must not be null");
        if (root == null) {
            root = new Node<>(value);
            size++;
            return true;
        }

        Node<T> current = root;
        while (true) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) {
                return false;
            } else if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    size++;
                    return true;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    size++;
                    return true;
                }
                current = current.right;
            }
        }
    }

    public boolean contains(T value) {
        return findNode(value) != null;
    }

    /**
     * Deletes a value from the tree.
     *
     * @return true if the value was found and removed, false otherwise
     */
    public boolean delete(T value) {
        Objects.requireNonNull(value, "value must not be null");
        Node<T> parent = null;
        Node<T> current = root;

        while (current != null && current.value.compareTo(value) != 0) {
            parent = current;
            current = value.compareTo(current.value) < 0 ? current.left : current.right;
        }

        if (current == null) {
            return false;
        }

        deleteNode(current, parent);
        size--;
        return true;
    }

    private void deleteNode(Node<T> target, Node<T> parent) {
        if (target.left != null && target.right != null) {
            Node<T> successorParent = target;
            Node<T> successor = target.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            target.value = successor.value;
            deleteNode(successor, successorParent);
            return;
        }

        Node<T> child = (target.left != null) ? target.left : target.right;
        if (parent == null) {
            root = child;
        } else if (parent.left == target) {
            parent.left = child;
        } else {
            parent.right = child;
        }
    }

    private Node<T> findNode(T value) {
        Objects.requireNonNull(value, "value must not be null");
        Node<T> current = root;
        while (current != null) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) {
                return current;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    /** Breadth-first (level-order) traversal. */
    public List<T> bfs() {
        List<T> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        Deque<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            result.add(node.value);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        return result;
    }

    public enum DfsOrder { PRE_ORDER, IN_ORDER, POST_ORDER }

    /** Depth-first traversal in the requested order. In-order on a BST yields sorted values. */
    public List<T> dfs(DfsOrder order) {
        List<T> result = new ArrayList<>(size);
        dfs(root, order, result);
        return result;
    }

    private void dfs(Node<T> node, DfsOrder order, List<T> out) {
        if (node == null) {
            return;
        }
        if (order == DfsOrder.PRE_ORDER) {
            out.add(node.value);
        }
        dfs(node.left, order, out);
        if (order == DfsOrder.IN_ORDER) {
            out.add(node.value);
        }
        dfs(node.right, order, out);
        if (order == DfsOrder.POST_ORDER) {
            out.add(node.value);
        }
    }

    /** Convenience: in-order traversal, i.e. all values in ascending sorted order. */
    public List<T> toSortedList() {
        return dfs(DfsOrder.IN_ORDER);
    }

    /** Height of the tree; an empty tree has height -1, a single node has height 0. */
    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }
}
