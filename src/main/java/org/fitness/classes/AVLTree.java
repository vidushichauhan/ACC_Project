package org.fitness.classes;

import java.util.Comparator;
import java.util.function.BiConsumer;

public class AVLTree<K, V> {

    private AVLNode<K, V> root;
    private Comparator<? super K> comparator;

    public AVLTree() {
        this.root = null;
        this.comparator = null;
    }

    public AVLTree(Comparator<? super K> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private AVLNode<K, V> put(AVLNode<K, V> node, K key, V value) {
        if (node == null) {
            return new AVLNode<>(key, value);
        }

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && compare(key, node.left.key) < 0) {
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && compare(key, node.right.key) > 0) {
            return rotateLeft(node);
        }

        // Left Right Case
        if (balance > 1 && compare(key, node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Left Case
        if (balance < -1 && compare(key, node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public V get(K key) {
        AVLNode<K, V> node = get(root, key);
        return node == null ? null : node.value;
    }

    private AVLNode<K, V> get(AVLNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node;
        }
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private int height(AVLNode<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(AVLNode<K, V> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private int compare(K key1, K key2) {
        if (comparator != null) {
            return comparator.compare(key1, key2);
        } else {
            @SuppressWarnings("unchecked")
            Comparable<? super K> k1 = (Comparable<? super K>) key1;
            return k1.compareTo(key2);
        }
    }

    public void inOrderTraversal(BiConsumer<K, V> consumer) {
        inOrderTraversal(root, consumer);
    }

    private void inOrderTraversal(AVLNode<K, V> node, BiConsumer<K, V> consumer) {
        if (node != null) {
            inOrderTraversal(node.left, consumer);
            consumer.accept(node.key, node.value);
            inOrderTraversal(node.right, consumer);
        }
    }

    private static class AVLNode<K, V> {
        private K key;
        private V value;
        private AVLNode<K, V> left;
        private AVLNode<K, V> right;
        private int height;

        public AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }
}
