package org.fitness.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class AVLTree {
    private class Node {
        String key;
        Set<String> documents;
        Node left, right;
        int height;

        Node(String key, String document) {
            this.key = key;
            this.documents = new HashSet<>();
            this.documents.add(document);
            this.height = 1; // New nodes are initially added as leaf nodes
        }
    }

    private Node root = null;

    // Insert a key and a document into the AVL tree
    public void insert(String key, String document) {
        root = insert(root, key, document);
    }

    private Node insert(Node node, String key, String document) {
        if (node == null) {
            return new Node(key, document);
        }

        int compareResult = key.compareTo(node.key);

        if (compareResult < 0) {
            node.left = insert(node.left, key, document);
        } else if (compareResult > 0) {
            node.right = insert(node.right, key, document);
        } else {
            // Key already exists, just add the document
            node.documents.add(document);
            return node;
        }

        // Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor of this ancestor node to check whether
        // this node became unbalanced
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        // Return the (unchanged) node pointer
        return node;
    }

    // A utility function to right rotate subtree rooted with y
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    // A utility function to left rotate subtree rooted with x
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    // Get the height of the tree
    private int height(Node N) {
        if (N == null)
            return 0;

        return N.height;
    }

    // Get balance factor of node N
    private int getBalance(Node N) {
        if (N == null)
            return 0;

        return height(N.left) - height(N.right);
    }

    // Call this method to print the documents in sorted order of terms
    public void inOrderTraversal(BiConsumer<String, Set<String>> action) {
        inOrderTraversal(root, action);
    }

    private void inOrderTraversal(Node node, BiConsumer<String, Set<String>> action) {
        if (node != null) {
            inOrderTraversal(node.left, action);
            action.accept(node.key, node.documents); // Apply the action
            inOrderTraversal(node.right, action);
        }
    }
}

