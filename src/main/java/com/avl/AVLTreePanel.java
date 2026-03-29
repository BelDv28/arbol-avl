package com.avl;

import javax.swing.*;
import java.awt.*;

public class AVLTreePanel extends JPanel {

    private AVLNode root;
    private static final int NODE_RADIUS = 22;
    private static final int V_GAP       = 70;
    private static final Color COLOR_ROOT   = new Color(124, 58, 237);
    private static final Color COLOR_NODE   = new Color(30, 58, 138);
    private static final Color COLOR_BORDER = new Color(0, 212, 255);
    private static final Color COLOR_EDGE   = new Color(37, 99, 235);
    private static final Color COLOR_TEXT   = new Color(226, 232, 240);
    private static final Color COLOR_FE     = new Color(100, 116, 139);
    private static final Color COLOR_FE_BAD = new Color(248, 113, 113);

    public AVLTreePanel() {
        setBackground(new Color(10, 14, 26));
    }

    public void setTree(AVLNode root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root == null) {
            g.setColor(new Color(100, 116, 139));
            g.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g.drawString("(árbol vacío)", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        // Antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        assignPositions(root, 0, getWidth(), 0, 0);
        drawEdges(g2, root);
        drawNodes(g2, root, true);
    }

    // ── Asignar posiciones X,Y a cada nodo ───────────────────────
    private void assignPositions(AVLNode node, int xMin, int xMax,
                                 int depth, int parentX) {
        if (node == null) return;
        node.x = (xMin + xMax) / 2;
        node.y = 50 + depth * V_GAP;
        assignPositions(node.left,  xMin,   node.x, depth + 1, node.x);
        assignPositions(node.right, node.x, xMax,   depth + 1, node.x);
    }

    // ── Dibujar aristas (líneas entre nodos) ──────────────────────
    private void drawEdges(Graphics2D g2, AVLNode node) {
        if (node == null) return;
        g2.setColor(COLOR_EDGE);
        g2.setStroke(new BasicStroke(2f));
        if (node.left != null) {
            g2.drawLine(node.x, node.y, node.left.x, node.left.y);
            drawEdges(g2, node.left);
        }
        if (node.right != null) {
            g2.drawLine(node.x, node.y, node.right.x, node.right.y);
            drawEdges(g2, node.right);
        }
    }

    // ── Dibujar nodos (círculos con texto) ────────────────────────
    private void drawNodes(Graphics2D g2, AVLNode node, boolean isRoot) {
        if (node == null) return;

        int fe = height(node.left) - height(node.right);

        // Sombra
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(node.x - NODE_RADIUS + 3,
                node.y - NODE_RADIUS + 3,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Relleno del círculo
        g2.setColor(isRoot ? COLOR_ROOT : COLOR_NODE);
        g2.fillOval(node.x - NODE_RADIUS,
                node.y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Borde del círculo
        g2.setColor(COLOR_BORDER);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(node.x - NODE_RADIUS,
                node.y - NODE_RADIUS,
                NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Texto: clave del nodo
        g2.setColor(COLOR_TEXT);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        String keyStr = String.valueOf(node.key);
        int tx = node.x - fm.stringWidth(keyStr) / 2;
        int ty = node.y + fm.getAscent() / 2 - 2;
        g2.drawString(keyStr, tx, ty);

        // Texto: factor de equilibrio debajo del nodo
        g2.setColor(Math.abs(fe) > 1 ? COLOR_FE_BAD : COLOR_FE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String feStr = "fe:" + fe;
        int fx = node.x - g2.getFontMetrics().stringWidth(feStr) / 2;
        g2.drawString(feStr, fx, node.y + NODE_RADIUS + 12);

        drawNodes(g2, node.left,  false);
        drawNodes(g2, node.right, false);
    }

    private int height(AVLNode n) {
        return n == null ? 0 : n.height;
    }
}