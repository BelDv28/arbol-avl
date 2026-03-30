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
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton btnAVL = new JButton("Árbol AVL");
        JButton btnB = new JButton("Árbol B");

        btnAVL.addActionListener(e -> {
            AVLTreeGUI avl = new AVLTreeGUI();
            avl.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            avl.setVisible(true);
        });

        btnB.addActionListener(e -> {
            ArbolBGUI arbolB = new ArbolBGUI();
            arbolB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            arbolB.setVisible(true);
        });

        gbc.gridy = 0;
        panel.add(btnAVL, gbc);

        gbc.gridy = 1;
        panel.add(btnB, gbc);

        add(panel);
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