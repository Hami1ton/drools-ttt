package org.example.drools.uttt.model;

public class Mark {

    public final int row;

    public final int col;

    public final String mark;

    public final int globalRow;

    public final int globalCol;

    public final int localRow;

    public final int localCol;

    public Mark(int row, int col, String mark) {
        this.row = row;
        this.col = col;
        this.mark = mark;

        this.globalRow = row / 3;
        this.globalCol = row / 3;
        this.localRow = row % 3;
        this.localCol = row % 3;
    }

}
