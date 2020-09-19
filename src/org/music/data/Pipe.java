package org.music.data;

public class Pipe {

    int lower1; // lower bound of low
    int lower2; // upper bound of low
    int upper1; // lower bound of up
    int upper2; // upper bound of up

    int id;

    int time;

    public int getLower1() {
        return lower1;
    }

    public void setLower1(int lower1) {
        this.lower1 = lower1;
    }

    public int getLower2() {
        return lower2;
    }

    public void setLower2(int lower2) {
        this.lower2 = lower2;
    }

    public int getUpper1() {
        return upper1;
    }

    public void setUpper1(int upper1) {
        this.upper1 = upper1;
    }

    public int getUpper2() {
        return upper2;
    }

    public void setUpper2(int upper2) {
        this.upper2 = upper2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


}
