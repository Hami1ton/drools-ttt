package org.example.drools.ttt.model;

public class CurrentPlayer {

    private String mark;

    public CurrentPlayer() {}

    public CurrentPlayer(String mark) {
        this.mark = mark;
    }

    public String getMark(){
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "mark: " + mark;
    }

}
