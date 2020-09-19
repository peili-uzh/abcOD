package org.music.missingtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class SyntheticMiss extends SyntheticData {

    public SyntheticMiss(int range, LinkedHashMap<Integer, Integer> timelist, double percent, int deltat, int theta)
            throws Exception {
        super(range, timelist, percent, deltat, theta);
    }

    /**
     * Sample without replacement on elements with correct values.
     *
     * @param sequence
     * @return
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<Integer> sampleWithoutReplacement() {
        // System.out.println("sample without replacement");
        ArrayList<Integer> tempSequence = new ArrayList<Integer>();
        tempSequence.addAll(correctSequence);

        ArrayList<Integer> newSequence = new ArrayList<Integer>();
        repairs = new HashMap<Integer, Triple>();
        truths = new HashMap<Integer, Integer>();
        newSequence.addAll(sequence);

        Random random = new Random();
        int n = (int) Math.max(0, tempSequence.size() * percent);
        n = (int) Math.min(n, tempSequence.size() - 1);

        // System.out.println("# possible missing: \t" + n);

        for (int i = 0; i < n; i++) {
            int index = random.nextInt(tempSequence.size());
            int key = tempSequence.get(index);
            newSequence.set(key, 0);
            // System.out.println("\t" + key + "\t" + sequence.get(key));

            Triple triple = new Triple();
            triple.setGround(sequence.get(key));
            repairs.put(key, triple);
            truths.put(key, sequence.get(key));
            triple.setError(0);

            tempSequence.remove(index);
        }

        return newSequence;

    }

    /**
     * Sample with replacement on raw data.
     *
     * @param sequence
     * @return
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<Integer> generateSyntheticData(ArrayList<Integer> sequence) {

        ArrayList<Integer> newSequence = new ArrayList<Integer>();
        repairs = new HashMap<Integer, Triple>();
        newSequence.addAll(sequence);

        Random random = new Random();
        int n = (int) Math.max(1, range * percent);
        n = (int) Math.min(n, range - 1);

        System.out.println("# possible missing: \t" + n);

        for (int i = 0; i < n; i++) {
            int index = random.nextInt(range);
            newSequence.set(index, 0);

            if (sequence.get(index) != 0) {
                Triple triple = new Triple();
                triple.setGround(sequence.get(index));
                repairs.put(index, triple);

                //System.out.println(i+"\t"+index+"\t"+sequence.get(index)+"\t"+newSequence.get(index));
            }

        }
        return newSequence;
    }


}
