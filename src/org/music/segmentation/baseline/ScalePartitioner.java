package org.music.segmentation.baseline;

import java.util.ArrayList;

/**
 * Partition input sequence into K quasi-monotonic segments.
 *
 * @author peili
 */
public class ScalePartitioner {

    protected int deltat;

    public ScalePartitioner(int deltat) {
        this.deltat = deltat;
    }

    /**
     * Given the scale labelling, the algorithm returns a segmentation using at most k segments. It is assumed that there are at least k+1 extrema to begin with.
     *
     * @param k
     * @param data
     * @return
     */
    public ArrayList<Integer> scalePartitioning(int k, SequenceList data) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        ArrayList<Integer> list = new ArrayList<Integer>();

        //System.out.println("===== partition data");

        // sort scales in decreasing order, and keep the top k+2 in list
        for (int i = 0; i < data.size(); i++) {
            ExtremumRecord e = (ExtremumRecord) data.getElement(i);
            int scale = e.getScale();
            InsertList(temp, scale, k);
        }

        removeElement(k, data, temp);

        // System.out.println("k= "+(temp.size()-1));

        // insert cutoff point into data
        list = insertPartitionPoint(data, temp);
        //System.out.println(list.size()+"\t"+list);


        return list;
    }

    private void removeElement(int k, SequenceList data, ArrayList<Integer> temp) {

		/*
		// remove the last few element in list which has the same value (smallest value) in list
		if(temp.size()> (k+1))
			removeMin(temp);
		
		ExtremumRecord first = (ExtremumRecord) data.getElement(0);
		ExtremumRecord last = (ExtremumRecord) data.getElement(data.size()-1);
		
		boolean containsFirst = temp.contains(first.getScale());
		boolean containsLast = temp.contains(last.getScale());
		
		if(!(containsFirst && containsLast)){ // if none or one of elements (last, first) are not included
			if((containsFirst || containsLast ) && temp.size() == (k+1))
				removeMin(temp);				
			
			if((!containsFirst && !containsLast) && temp.size() >= k)
				removeMin(temp);	
		}*/

        // int min = temp.get(temp.size()-1);
        // if(min == 1)
        // removeMin(temp);

        int min = 0;
        int max = temp.get(0);
        // System.out.println("scale processor.deltat = " + this.deltat);
        while (min <= this.deltat && max > this.deltat) {
            // System.out.println("before removing: " + temp);
            removeMin(temp);
            min = temp.get(temp.size() - 1);
            // System.out.println("min: " + min);
            // System.out.println("after removing: " + temp);
        }
        // System.out.println("delta: "+temp);
    }

    /**
     * Get the index of delta-extremal element, and check whether the first and the last element are included. If not, the smallest delta-extrema will be removed in favor of the first and the last elements.
     * The number of segments can be less than k since several extrema can take the same delta value and can be removed together.
     *
     * @param data
     * @param temp
     * @return
     */
    private ArrayList<Integer> insertPartitionPoint(SequenceList data, ArrayList<Integer> temp) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < data.size(); i++) {
            ExtremumRecord e = (ExtremumRecord) data.getElement(i);
            if (temp.contains(e.getScale())) { //&& i>0 && i< (data.size()-1)
                list.add(list.size(), e.getIndex());
                //System.out.println("Add to delta-extrema list \t"+e.getScale()+"\t"+e.getIndex()+"\t"+e.getValue());
            }

        }

        return list;
    }

    /**
     * Remove the set of minimal elements in the list.
     *
     * @param list
     */
    private void removeMin(ArrayList<Integer> list) {
        int min = list.get(list.size() - 1);
        int max = list.get(0);
        if (max > min) {
            while (list.get(list.size() - 1) == min) {
                list.remove(list.size() - 1);
            }
        }

    }

    /**
     * Insert a value into list so that the list is sorted in decreasing order.
     *
     * @param list
     * @param scale
     * @param k
     */
    private void InsertList(ArrayList<Integer> list, int scale, int k) {

        int position = NativeSearch(list, scale);
        list.add(position, scale);
        if (list.size() == (k + 3))
            list.remove(list.size() - 1);
    }

    /**
     * Find the position of a value in a decreasing-ordered list. Return 0 if the list is empty.
     *
     * @param list
     * @param value
     * @return
     */
    private int NativeSearch(ArrayList<Integer> list, int value) {
        int position = 0;
        if (!list.isEmpty()) {
            boolean stop = false;
            int i = 0;
            while (!stop && i < list.size()) {
                int local = list.get(i);
                if (local < value) {
                    position = i;
                    stop = true;
                }
                i++;
            }
            if (!stop)
                position = list.size();

        }
        return position;
    }
}
