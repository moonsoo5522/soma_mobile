package com.sangjin.soma7th.beans;

/**
 * Created by user on 2016. 7. 15..
 */
public class Rank implements Comparable<Rank> {
    private int rank;
    private String id;
    private int win;
    private int lose;
    private String record;
    private int percent;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Override
    public int compareTo(Rank arg) {
        if (this.percent > arg.percent)   // 내림차순 , 오름차순으로 하려면 < 으로~
            return -1;
        else if (this.percent == arg.percent)
            return 0;
        else
            return 1;
    }
}
