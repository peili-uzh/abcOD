package org.music.segmentation.baseline;

import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class Processor {

    public ArrayList<LinkedHashMap<Integer, Integer>> timelists;
    public ArrayList<LinkedHashMap<Integer, ReleaseLabel>> releaselists;

}
