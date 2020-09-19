package org.music.segmentation.baseline;

/**
 * An extremum record is  an "border" element of the input sequence, i.e., its value is either local minimum or maximum.
 * An extremum record has three variables: index, value, and trend (sense), indicating whether the extremum is a maximum or minimum.
 *
 * @author peili
 */
public class ExtremumRecord {

    public int index;
    public int value;
    public String sense;
    public int scale;
    public boolean split;

    //public ExtremumRecord(){}

    public ExtremumRecord(int index, int value, String sense) {

        this.index = index;
        this.value = value;
        this.sense = sense;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSense() {
        return sense;
    }

    public void setSense(String sense) {
        this.sense = sense;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }


}
