package com.sangjin.soma7th;

public class Dol {
    public int x;
    public int y;
    public int degree;
    public int savedDegree;
    public boolean isBlack;
    public boolean isPlace;

    public Dol() {
        this.x = 0;
        this.y = 0;
    }

    public Dol(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
