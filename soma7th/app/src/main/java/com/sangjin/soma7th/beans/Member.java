package com.sangjin.soma7th.beans;

/**
 * Created by user on 2016. 7. 14..
 */


public class Member {
    private String id;
    private String password;
    private String name;
    private int point;
    private int win, lose;
    private int dol;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
    public int getWin() {
        return win;
    }
    public void setWin(int win) {
        this.win = win;
    }
    public int getLose() {
        return lose;
    }
    public void setLose(int lose) {
        this.lose = lose;
    }
    public int getDol() {
        return dol;
    }
    public void setDol(int dol) {
        this.dol = dol;
    }

}
