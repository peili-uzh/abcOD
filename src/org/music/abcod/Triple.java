package org.music.abcod;

public class Triple<S, E, T> {

    private int start;
    private int end;
    private int ground;
    private int avg;
    private int error;

    public Triple(int start, int end, int ground) {
        this.start = start;
        this.end = end;
        this.ground = ground;
    }

    public Triple(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Triple() {
    }

    ;

    public int getGround() {
        return ground;
    }

    public void setGround(int ground) {
        this.ground = ground;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getAvg() {
        return avg;
    }

    public void setAvg(int avg) {
        this.avg = avg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }


}
