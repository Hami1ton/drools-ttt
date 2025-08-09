package org.example.drools.ttt.command;


public class PlaceCommand {

    public final int row;

    public final int col;

    public PlaceCommand(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public String toString() {
        return "row: " + this.row + ", col: " + this.col;
    }
}
