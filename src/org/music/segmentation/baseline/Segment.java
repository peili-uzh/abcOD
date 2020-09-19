package org.music.segmentation.baseline;

import org.music.missingtime.Piece;

public class Segment extends Piece {

    public Segment(int start, int end, int size) {
        this.start = start;
        this.end = end;
        this.size = size;
    }
}
