package org.example.drools.ttt.model;

import javax.swing.JButton;

public class Field {

    public int x;

    public int y;

    public boolean isFilled = false;

    public JButton btn; 

    public Field(int x, int y, JButton btn) {
        this.x = x;
        this.y = y;
        this.btn = btn;
    }
}
