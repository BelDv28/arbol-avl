package com.avl;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.Consumer;

public class AVLTree {

    private AVLNode root;
    private final Consumer<String> logger;

    public AVLTree(Consumer<String> logger) {
        this.logger = logger;
    }

    // ── Altura ───────────────────────────────────────────────────
    private int height(AVLNode n) {
        return n == null ? 0 : n.height;
    }

    private void updateHeight(AVLNode n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int balanceFactor(AVLNode n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    // ── Rotaciones ────────────────────────────────────────────────
    private AVLNode rotateRight(AVLNode y) {
        logger.accept("   Rotación simple DERECHA en nodo: " + y.key);
        AVLNode x  = y.left;
        AVLNode t2 = x.right;
        x.right = y;
        y.left  = t2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private AVLNode rotateLeft(AVLNode x) {
        logger.accept("   Rotación simple IZQUIERDA en nodo: " + x.key);
        AVLNode y  = x.right;
        AVLNode t2 = y.left;
        y.left  = x;
        x.right = t2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // ── Balance ───────────────────────────────────────────────────
    private AVLNode balance(AVLNode node) {
        updateHeight(node);
        int bf = balanceFactor(node);

        logger.accept("  Factor de equilibrio en [" + node.key + "] = " + bf
                + " | Alt.Izq=" + height(node.left)
                + " | Alt.Der=" + height(node.right));

        // Izquierda-Izquierda
        if (bf > 1 && balanceFactor(node.left) >= 0)
            return rotateRight(node);

        // Izquierda-Derecha
        if (bf > 1 && balanceFactor(node.left) < 0) {
            logger.accept("   Rotación doble IZQ-DER en nodo: " + node.key);
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Derecha-Derecha
        if (bf < -1 && balanceFactor(node.right) <= 0)
            return rotateLeft(node);

        // Derecha-Izquierda
        if (bf < -1 && balanceFactor(node.right) > 0) {
            logger.accept("   Rotación doble DER-IZQ en nodo: " + node.key);
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // ── Inserción ─────────────────────────────────────────────────
    public void insert(int key) {
        logger.accept("─── Insertando clave: " + key);
        boolean isFirst = (root == null);
        root = insertRec(root, key, isFirst);
    }

    private AVLNode insertRec(AVLNode node, int key, boolean esRaiz) {
        if (node == null) {
            if (esRaiz) logger.accept("   Creación del nodo RAÍZ: " + key);
            else        logger.accept("   Nuevo nodo hoja creado: " + key);
            return new AVLNode(key);
        }
        if (key < node.key) {
            logger.accept("   Subárbol IZQUIERDO de " + node.key);
            node.left  = insertRec(node.left,  key, false);
        } else if (key > node.key) {
            logger.accept("   Subárbol DERECHO de " + node.key);
            node.right = insertRec(node.right, key, false);
        } else {
            logger.accept("  Clave duplicada ignorada: " + key);
            return node;
        }
        return balance(node);
    }

    // ── Getter ────────────────────────────────────────────────────
    public AVLNode getRoot() { return root; }

    // ── Construir JTree ───────────────────────────────────────────
    public DefaultMutableTreeNode buildJTreeNode(AVLNode node) {
        if (node == null) return null;
        int fe = balanceFactor(node);
        DefaultMutableTreeNode jtNode = new DefaultMutableTreeNode(
                node.key + "  [FE=" + fe + "]"
        );
        DefaultMutableTreeNode left  = buildJTreeNode(node.left);
        DefaultMutableTreeNode right = buildJTreeNode(node.right);
        if (left  != null) jtNode.add(left);
        if (right != null) jtNode.add(right);
        return jtNode;
    }
}