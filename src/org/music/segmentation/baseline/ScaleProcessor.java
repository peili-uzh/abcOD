package org.music.segmentation.baseline;

import org.music.abcod.Block;
import org.music.abcod.Evaluation;
import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This processor finds extremal element, label scales
 *
 * @author peili
 */

/**
 * @author Pei
 *
 */
public class ScaleProcessor extends Processor {

    public ScaleProcessor(ArrayList<LinkedHashMap<Integer, Integer>> timelists,
                          ArrayList<LinkedHashMap<Integer, ReleaseLabel>> releaselists, int deltat) {

        this.timelists = timelists;
        this.releaselists = releaselists;
        this.deltat = deltat;
    }

    public ScaleProcessor(int deltat) {
        this.deltat = deltat;
    }

    public ArrayList<Integer> sequence;
    public SequenceList extremList;
    public int deltat;
    private ArrayList<Integer> list;
    private ArrayList<Integer> positionList;
    public ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series;

    /**
     * find extremal element in each block, and label scales
     *
     * @throws Exception
     */
    public void process() throws Exception {

        ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

        /**
         * Within each block
         */
        for (int count = 0; count < timelists.size(); count++) {

            LinkedHashMap<Integer, Integer> timelist = timelists.get(count);
            LinkedHashMap<Integer, ReleaseLabel> releaselist = releaselists.get(count);

            // Prepare input extremList.
            sequence = getSequence(timelist);

            if (sequence.size() > 10) {

                positionList = getPositions();
                getExtrems();

                // System.out.println("# of extrems \t"+extremList.size());

                // Label extremal records with scales
                ScaleLabeller scaleLabber = new ScaleLabeller();
                scaleLabber.scaleLabelling(extremList);

                // Partition the data to k segments according to labelled scales
                ScalePartitioner scalePartitioner = new ScalePartitioner(this.deltat);
                int k = extremList.size() - 1;

                ArrayList<Integer> list = quasiSegment(k);
                // scalePartitioner.scalePartitioning(k, extremList);

                // printPartition();
                // printPartitioning(timelist, list);
                getSeries(series, list, releaselist);
                // System.out.println("# of series \t" + series.size());
                for (int m = 0; m < series.size(); m++) {
                    LinkedHashMap<Integer, ReleaseLabel> map = series.get(m);
                    for (int j = 0; j < map.size(); j++) {
                        ReleaseLabel label = map.get(j);
                        int year = label.getDate();
                        // System.out.println(label.getId() + "\t" + year);
                    }
                    // System.out.println();
                    // System.out.println("series size \t" + map.size());
                }
            }
        }
        /**
         * Evaluate series.
         */
        Evaluation evaluation = new Evaluation();
        evaluation.evaluationPartition(series);

        this.series = series;
    }

    /**
     * find extremal element in a block, and label scales
     *
     * @param sequence
     * @param releaselist
     */
    public void processBlock(ArrayList<Integer> sequence, LinkedHashMap<Integer, ReleaseLabel> releaselist) {

        ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

        /**
         * Within each block
         */


        // Prepare input extremList.
        this.sequence = sequence;
        positionList = getPositions();
        getExtrems();

        // System.out.println("# of extrems \t" + extremList.size());

        // Label extremal records with scales
        ScaleLabeller scaleLabber = new ScaleLabeller();
        scaleLabber.scaleLabelling(extremList);

        // Partition the data to k segments according to labelled scales
        ScalePartitioner scalePartitioner = new ScalePartitioner(this.deltat);
        int k = extremList.size() - 1;

        ArrayList<Integer> list = quasiSegment(k);
        // scalePartitioner.scalePartitioning(k, extremList);

        // printPartition();
        // printPartitioning(timelist, list);
        getSeries(series, list, releaselist);
        // System.out.println("# of series \t" + series.size());
        for (int m = 0; m < series.size(); m++) {
            LinkedHashMap<Integer, ReleaseLabel> map = series.get(m);
            for (int j = 0; j < map.size(); j++) {
                ReleaseLabel label = map.get(j);
                int year = label.getDate();
                // System.out.println(label.getId() + "\t" + year);
            }
            // System.out.println();
            // System.out.println("series size \t" + map.size());
        }

        /**
         * Evaluate series.
         *
         * Evaluation evaluation = new Evaluation();
         * evaluation.evaluationPartition(series);
         */

        this.series = series;
    }

    private ArrayList<Integer> quasiSegment(int k) {
        ScalePartitioner scalePartitioner = new ScalePartitioner(this.deltat);
        ArrayList<Integer> labeledList = scalePartitioner.scalePartitioning(k, extremList);
        // Get overlapping segments
        list = new ArrayList<Integer>();
        getSegments(labeledList);
        return list;
    }

    /**
     * Output overlapping segments given a list of local extremal values.
     *
     * @param labelledList
     * @return
     */
    private ArrayList<Segment> getSegments(ArrayList<Integer> labelledList) {

        ArrayList<Segment> segments = new ArrayList<Segment>();
        // System.out.println("cutoff points: "+list);

        int old_start = -1;
        int current_start = -1;
        int j = 0;

        for (int i = 0; i < labelledList.size(); i++) {
            int index = labelledList.get(i);

            current_start = getCurrentStartPosition(index);
            int current_end = getCurrentEndPosition(index);

            // System.out.println(i+"\t"+index+"\t"+current_start+"\t"+current_end);
            int start_position = positionList.indexOf(old_start);
            int end_position = positionList.indexOf(current_end);

            if (old_start < current_end) {
                Segment segment = new Segment(old_start, current_end, (end_position - start_position + 1));
                segments.add(segments.size(), segment);
                if (old_start >= 0) {
                    // System.out.println(j + "\t segment: \t [" + old_start +
                    // ", " + current_end + "]");
                    // System.out.println("\t\tcurrent start: " +
                    // current_start);
                    if (current_start >= current_end && current_end < sequence.size() - 1
                            && old_start + 1 < current_end)
                        list.add(list.size(), current_end);
                }
                old_start = current_start;
                j++;

            }
        }
        // System.out.println("aaa list: " + list);
        return segments;
    }

    /**
     * Scan backward in the sequence until a new value occurs.
     *
     * @param index
     * @return
     */
    private int getCurrentStartPosition(int index) {
        int start = index;
        int value = sequence.get(index);

        boolean stop = false;
        int i = index - 1;
        while (i >= 0 && !stop) {
            int previous_value = sequence.get(i);
            if (previous_value != 0) {
                if (previous_value != value)
                    stop = true;
                else
                    start = i;
            }

            i--;
        }
        return start;
    }

    /**
     * Scan forward in the sequence until a new value occurs.
     *
     * @param index
     * @return
     */
    private int getCurrentEndPosition(int index) {

        int end = index;
        int value = sequence.get(index);

        boolean stop = false;
        int i = index + 1;
        while (i < sequence.size() && !stop) {
            int next_value = sequence.get(i);
            if (next_value != 0) {
                if (next_value != value)
                    stop = true;
                else
                    end = i;
            }
            i++;
        }

        return end;
    }

    protected void getSeries(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series, ArrayList<Integer> list,
                             LinkedHashMap<Integer, ReleaseLabel> releaselist) {
        int start = 0;
        // System.out.println("series cutoff list: " + list);
        LinkedHashMap<Integer, ReleaseLabel> map = new LinkedHashMap<Integer, ReleaseLabel>();
        for (int i = 0; i < sequence.size(); i++) {
            ReleaseLabel release = releaselist.get(i);
            if (list.contains(i)) {
                start = i + 1;
                series.add(series.size(), map);
                map = new LinkedHashMap<Integer, ReleaseLabel>();
                // System.out.println("\t\t series cutoff \t" + i);
            }
            map.put(map.size(), release);
        }
        series.add(series.size(), map);
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }

    protected void printPartitioning(LinkedHashMap<Integer, Integer> timelist,
                                     ArrayList<Integer> list) {
        System.out.println("\t year");
        for (int i = 0; i < timelist.size(); i++) {

            String yr = "";
            if (timelist.get(i) != 0)
                yr = String.valueOf(timelist.get(i));
            System.out.println(i + "\t" + yr);
            if (list.contains(i))
                System.out.println();
        }

    }

    /**
     * convert input data from LinkedHashMap to ArrayList
     *
     * @param timelist
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected ArrayList<Integer> getSequence(LinkedHashMap<Integer, Integer> timelist) throws Exception {
        ArrayList<Integer> sequence = new ArrayList<Integer>();
        Block block = new Block(timelist);
        sequence = block.getSequence();
        return sequence;
    }

    /**
     * Find extremal element from sequence, and add them into extremList in order.
     * @return a SequenceList, where the element is an ExtremumRecord.
     */

    protected void getExtrems() {

        extremList = new SequenceList();
        ArrayList<Integer> tempList = new ArrayList<Integer>();

        int previousYear = 0;
        int previousIndex = -1;
        int previousDistance = 3000;
        int distance = 3000;

        int last = 0;
        boolean isFirst = true;

        for (int i = 0; i < sequence.size(); i++) {
            int year = sequence.get(i);
            if (year != 0) {

                last = i;
                if (isFirst) {
                    isFirst = false;
                    tempList.add(tempList.size(), i);
                    //System.out.println(i+"\t\t"+year);
                }

                if (previousYear != 0) {
                    distance = year - previousYear;
                    if (previousDistance != 3000 && previousDistance * distance < 0) {
                        tempList.add(tempList.size(), previousIndex);
                        //System.out.println(previousIndex+"\t\t"+previousYear);
                    }
                }

                if (distance != 0) {
                    previousYear = year;
                    previousIndex = i;
                    previousDistance = distance;
                }

            }
            //System.out.println(i+"\t"+year);
        }

        tempList.add(tempList.size(), last);
        generateExtremList(tempList);
    }

    /**
     * Take each element in an ArrayList, and transfer them a SequenceList.
     * @param list ArrayList, where the element is Integer.
     */
    protected void generateExtremList(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i);
            int value = sequence.get(index);
            String sense = getSense(list, i, value);
            //System.out.println("\t"+index+"\t"+value+"\t"+sense);
            ExtremumRecord extRecord = new ExtremumRecord(index, value, sense);
            extremList.appendElement(extRecord);
        }
    }

    /**
     * Decide if the sense of an extremal value, i.e., if it is a local maximal or local minimal value.
     * @param list ArrayList of extremal values
     * @param i index of list
     * @param value value of index i
     * @return String, either "max" or "min"
     */
    protected String getSense(ArrayList<Integer> list, int i, int value) {
        String sense = "min";
        if (!sequence.isEmpty() && !list.isEmpty()) {

            int distance = 0;
            if ((i + 1) < list.size()) {
                distance = sequence.get(list.get(i + 1)) - value;
            } else {
                distance = 0;// sequence.get(list.get(i - 1)) - value;
            }
            if (distance < 0)
                sense = "max";
        }
        return sense;
    }

    /**
     * Keep the valid positions of the original sequence in a list.
     * Valid positions mean: 1. value is not 0; 2. element is not removed in the process.
     * @param sequence
     * @return
     */
    public ArrayList<Integer> getPositions() {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < sequence.size(); i++) {
            int value = sequence.get(i);
            if (value != 0)
                list.add(list.size(), i);
        }
        return list;
    }
}
