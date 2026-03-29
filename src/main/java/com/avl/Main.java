package com.avl;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Lanza la ventana en el hilo correcto de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            AVLTreeGUI gui = new AVLTreeGUI();
            gui.setVisible(true);
        });
    }
}