package com.avl;

import arbolB.ui.ArbolBGUI;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame{
    public Main() {
        setTitle("Menú Principal");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnAVL = new JButton("Árbol AVL");
        JButton btnB = new JButton("Árbol B");

        // Acción botón AVL
        btnAVL.addActionListener(e -> {
            AVLTreeGUI avl = new AVLTreeGUI();
            avl.setVisible(true);
        });

        // Acción botón Árbol B
        btnB.addActionListener(e -> {
            ArbolBGUI arbolB = new ArbolBGUI();
            arbolB.setVisible(true);
        });

        add(btnAVL);
        add(btnB);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new Main().setVisible(true);
        });
    }
}