package com.avl;

public class AVLNode {
    int key;
    AVLNode left, right;
    int height;
    int x, y; // posición gráfica para dibujar en el panel

    public AVLNode(int key) {
        this.key    = key;
        this.height = 1;
    }
}