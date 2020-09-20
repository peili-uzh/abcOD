package org.music.segmentation.baseline;

import org.music.abcod.Evaluation;
import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class IterativeScaleProcessor extends ScaleProcessor {

    public IterativeScaleProcessor(ArrayList<LinkedHashMap<Integer, Integer>> timelists,
                                   ArrayList<LinkedHashMap<Integer, ReleaseLabel>> releaselists, int deltat) {
        super(timelists, releaselists, deltat);
    }

    private ArrayList<Integer> positionList;
    private ArrayList<Integer> removed;
    private ArrayList<Integer> list;

    /**
     * Iteratively find extremal element in each block, and label scales.
     *
     * @throws Exception
     */
    public void process() throws Exception {

        ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

        for (int count = 0; count < timelists.size(); count++) {

            LinkedHashMap<Integer, Integer> timelist = timelists.get(count);
            LinkedHashMap<Integer, ReleaseLabel> releaselist = releaselists.get(count);

            // System.out.println("# records in a block: " + timelist.size());

            // if (releaselist.get(0).getId() == 33166737) {

            // Prepare input extremList.
            sequence = getSequence(timelist);
            positionList = getPositions();

            removed = new ArrayList<Integer>();

            int old_count = 0;
            int current_count = 0;
            int i = 0;

            do {
                System.out.println("~~~~~~~ " + i + "-th iteration");
                old_count = current_count;
                getExtrems();

                // System.out.println("# of extrems \t" + extremList);

                // Label extremal records with scales
                ScaleLabeller scaleLabber = new ScaleLabeller();
                scaleLabber.scaleLabelling(extremList);

                // Partition the data to k segments according to labelled scales
                int k = extremList.size() - 1;
                list = quasiSegment(k);

                /* printPartition */
                // printPartitioning(list);

                current_count = removed.size();
                i++;

            } while (old_count != current_count);

            // printSequence();

            /**
             * Get series.
             */
            getSeries(series, list, releaselist);
            System.out.println("# of series \t" + series.size());
            for (int m = 0; m < series.size(); m++) {
                LinkedHashMap<Integer, ReleaseLabel> map = series.get(m);
                System.out.println("series size \t" + map.size());
            }

            // }

        }
        /**
         * Evaluate series.
         */
        Evaluation evaluation = new Evaluation();
        evaluation.evaluationPartition(series);
    }

    protected void getSeries(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series, ArrayList<Integer> list,
                             LinkedHashMap<Integer, ReleaseLabel> releaselist) {
        int start = 0;
        System.out.println("series cutoff list: " + list);
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        for (int i = 1; i < list.size(); i += 2) {
            int index = list.get(i);
            tempList.add(tempList.size(), index);
        }
        LinkedHashMap<Integer, ReleaseLabel> map = new LinkedHashMap<Integer, ReleaseLabel>();
        for (int i = 0; i < sequence.size(); i++) {
            ReleaseLabel release = releaselist.get(i);
            if (tempList.contains(i)) {
                start = i + 1;
                series.add(series.size(), map);
                map = new LinkedHashMap<Integer, ReleaseLabel>();
                System.out.println("\t\t series cutoff \t" + i);
            }

            map.put(map.size(), release);
        }
        series.add(series.size(), map);
    }

    private void printSequence() {
        System.out.println("cutoff points: \t" + list);

        System.out.println("\t year \t error");
        for (int i = 0; i < sequence.size(); i++) {

            String yr = "";
            if (sequence.get(i) != 0)
                yr = String.valueOf(sequence.get(i));

            if (positionList.contains(i))
                System.out.println(i + "\t" + yr);
            else
                System.out.println(i + "\t\t" + yr);

            if (list.contains(i))
                System.out.println();
        }

    }

    private ArrayList<Integer> quasiSegment(int k) {
        ScalePartitioner scalePartitioner = new ScalePartitioner(this.deltat);
        ArrayList<Integer> labeledList = scalePartitioner.scalePartitioning(k, extremList);
        //Get overlapping segments
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
        //System.out.println("cutoff points: "+list);

        int old_start = -1;
        int current_start = -1;
        int j = 0;

        for (int i = 0; i < labelledList.size(); i++) {
            int index = labelledList.get(i);

            current_start = getCurrentStartPosition(index);
            int current_end = getCurrentEndPosition(index);

            //System.out.println(i+"\t"+index+"\t"+current_start+"\t"+current_end);
            int start_position = positionList.indexOf(old_start);
            int end_position = positionList.indexOf(current_end);

            if (old_start < current_end) {
                Segment segment = new Segment(old_start, current_end, (end_position - start_position + 1));
                segments.add(segments.size(), segment);
                System.out.println(j + "\t segment: \t [" + old_start + ", " + current_end + "]");
                //list.add(list.size(), current_start);
                if (old_start >= 0)
                    list.add(list.size(), current_end);
                old_start = current_start;// current_end + 1;
                j++;

            }

            /*
             * Remove nodes as outliders if they are singleton border nodes.
             **/
            // System.out.println(Math.abs(current_start - current_end));
            if (current_start == current_end && current_start != 0 && current_start != sequence.size() - 1) {
                removed.add(current_start);

                labelledList.remove(i);
                // System.out.println("remove: \t"+current_start+"\t"+list);

                positionList.remove(end_position);
                // System.out.println("remove:\t"+end_position+"\t"+positionList);
            }
        }
        System.out.println("aaa list: " + list);
        return segments;
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
            if (!removed.contains(i) && next_value != 0) {
                if (next_value != value)
                    stop = true;
                else end = i;
            }
            i++;
        }

        return end;
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
            if (!removed.contains(i) && previous_value != 0) {
                if (previous_value != value)
                    stop = true;
                else start = i;
            }

            i--;
        }

        return start;
    }

    protected void printPartitioning(ArrayList<Integer> list) {
        System.out.println("\t year \t error");
        for (int i = 0; i < sequence.size(); i++) {

            String yr = "";
            if (sequence.get(i) != 0)
                yr = String.valueOf(sequence.get(i));
			
			/*if(list.contains(i))
				System.out.println();*/

            if (positionList.contains(i))
                System.out.println(i + "\t" + yr);
            else
                System.out.println(i + "\t\t" + yr);
        }

    }

    /**
     * Find extremal element from sequence, and add them into extremList in order.
     *
     * @param removed
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
            if (year != 0 && !removed.contains(i)) {

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
     * Remove contiguous elements in the list if they are also contiguous in the original sequence
     *
     * @param list
     * @param removed
     */
    protected void removeNoise(ArrayList<Integer> list) {
        // TODO Auto-generated method stub
        int i = 0;
        while (i < list.size() - 1) {
            int position = list.get(i);
            int next_position = list.get(i + 1);

            int p_index = positionList.indexOf(position);
            int next_p_index = positionList.indexOf(next_position);

            System.out.println("\t" + position + "\t" + next_position + "\t" + p_index + "\t" + next_p_index);

            //if (next_position - position == 1){
            if (next_p_index - p_index == 1) {


                list.remove(i);
                //System.out.println("remove: \t"+position+"\t"+list);
                //list.remove(i);
                //System.out.println("remove: \t"+next_position+"\t"+list);

                positionList.remove(p_index);
                System.out.println("remove: \t" + p_index + "\t" + positionList);
                //positionList.remove(p_index);
                //System.out.println("remove: \t"+next_p_index+"\t"+positionList);


                removed.add(position);
                //removed.add(next_position);
            } else
                i++;
        }
    }
}
