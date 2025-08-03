package org.example.drools.ttt.model;

import javax.swing.JButton;

public class Field {

    private int x;

    private int y;

    public boolean isFilled = false;

    private JButton btn; 

    public Field(int x, int y, JButton btn) {
        this.x = x;
        this.y = y;
        this.btn = btn;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public JButton getBtn(){
        return btn;
    }


}
