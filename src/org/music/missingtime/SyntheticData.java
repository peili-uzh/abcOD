package org.music.missingtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SyntheticData {

    protected int range;
    protected double percent;
    @SuppressWarnings("rawtypes")
    public HashMap<Integer, Triple> repairs;
    protected LinkedHashMap<Integer, Integer> rawData;
    protected int deltat;
    protected int theta;
    protected ArrayList<Integer> sequence;
    protected ArrayList<Integer> correctSequence;
    protected ArrayList<Integer> errorSequence;
    public HashMap<Integer, Integer> truths;
    protected HashMap<Integer, Integer> distanceSequence;
    protected int numSeries;
    protected double avgSeriesSize;
    protected int minSeriesSize;
    protected int maxSeriesSize;

    public int getNumSeries() {
        return numSeries;
    }


    public void setNumSeries(int numSeries) {
        this.numSeries = numSeries;
    }


    public SyntheticData(int range, LinkedHashMap<Integer, Integer> timelist, double percent, int deltat, int theta)
            throws Exception {
        this.range = range;
        this.percent = percent;
        rawData = timelist;
        this.deltat = deltat;
        this.theta = theta;
        detectErrors();
    }


    protected void detectErrors() throws Exception {

        //System.out.println("orginal series of \t"+range+" records");
        Block block = new Block(rawData);
        sequence = block.getSequence();
        // System.out.println("sequence:" + sequence);

        ArrayList<Piece> pieces = new ArrayList<Piece>();
        if (sequence.size() < 100)
            pieces = block.withoutPieces();
        else
            pieces = block.findLCMBPieces(deltat);
        // System.out.println("# pieces :" + pieces.size());
        SeriesProcessor seriesProcessor = new SeriesProcessor(deltat, theta, sequence, pieces);
        ArrayList<Piece> chopping = seriesProcessor.seriesDiscovery();
        this.numSeries = chopping.size();

        correctSequence = new ArrayList<Integer>();
        errorSequence = new ArrayList<Integer>();

        //System.out.println("# of series \t"+chopping.size());

        this.avgSeriesSize = Double.valueOf(sequence.size()) / Double.valueOf(this.numSeries);
        int max = 0;
        int min = sequence.size();
        for (int i = 0; i < chopping.size(); i++) {
            Piece piece = chopping.get(i);
            correctSequence.addAll(piece.getLMB());
            piece.setSize(piece.end - piece.start + 1);

            errorSequence.addAll(piece.getErrors());
            max = Math.max(max, piece.size);
            min = Math.min(min, piece.size);
        }

        this.maxSeriesSize = max;
        this.minSeriesSize = min;
        repairValues(chopping);

    }

    @SuppressWarnings("rawtypes")
    public double evaluateRepairs() {
        // System.out.println("evalute repair");
        int correct = 0;
        //System.out.println("# of sampled values: \t"+repairs.size());
        // System.out.println("\t id \t truth \t lower \t upper");
        for (Map.Entry<Integer, Triple> e : repairs.entrySet()) {
            Triple triple = e.getValue();
            if (triple.getStart() <= triple.getEnd()) {
                if (triple.getGround() <= triple.getEnd() && triple.getGround() >= triple.getStart())
                    correct++;
                // else
                // System.out.println("\t"+e.getKey()+"\t"+triple.getGround()+"\t"+triple.getStart()+"\t"+triple.getEnd());//System.out.println("");//
            } else {
                if (triple.getGround() <= triple.getEnd() || triple.getGround() >= triple.getStart())
                    correct++;
                // else
                // System.out.println("\t"+e.getKey()+"\t"+triple.getGround()+"\t"+triple.getStart()+"\t"+triple.getEnd());//System.out.println("");//
            }

        }

        double precision = Double.valueOf(correct) / Double.valueOf(repairs.size());
        return precision;
    }

    @SuppressWarnings("rawtypes")
    public ArrayList<Integer> evaluatePairs() {
        // System.out.println("evalute repair");
        int correct = 0;
        // System.out.println("# of sampled values: \t" + repairs.size());
        // System.out.println("\t id \t truth \t lower \t upper");
        for (Map.Entry<Integer, Triple> e : repairs.entrySet()) {
            Triple triple = e.getValue();

            /**
             * only used for LMB
             */
            // if (triple.getStart() == triple.getEnd()) {
            // triple.setStart(triple.getStart() - deltat);
            // triple.setEnd(triple.getEnd() + deltat);
            // }

            if (triple.getStart() <= triple.getEnd()) {
                if (triple.getGround() <= triple.getEnd() && triple.getGround() >= triple.getStart())
                    correct++;
                // else
                // System.out.println("\t" + e.getKey() + "\t" +
                // triple.getGround() + "\t" + triple.getStart() + "\t"
                // + triple.getEnd());// System.out.println("");//
            } else {
                if (triple.getGround() <= triple.getEnd() || triple.getGround() >= triple.getStart())
                    correct++;
                // else
                // System.out.println("\t" + e.getKey() + "\t" +
                // triple.getGround() + "\t" + triple.getStart() + "\t"
                // + triple.getEnd());// System.out.println("");//
            }
        }

        ArrayList<Integer> precision = new ArrayList<Integer>();
        precision.add(0, correct);
        precision.add(1, repairs.size());
        // System.out.println("\t" + correct + "\t" + repairs.size() + "\t"
        // + (Double.valueOf(correct) / Double.valueOf(repairs.size())));
        // Double.valueOf(correct) / Double.valueOf(repairs.size());
        return precision;
    }

    public HashMap<Integer, Triple> getRepairs() {
        return repairs;
    }

    public void setRepairs(HashMap<Integer, Triple> repairs) {
        this.repairs = repairs;
    }

    protected static void log(Object msg) {
        System.out.println(String.valueOf(msg));
    }


    public HashMap<Integer, Integer> getTruths() {
        return truths;
    }


    public void setTruths(HashMap<Integer, Integer> truths) {
        this.truths = truths;
    }

    @SuppressWarnings("rawtypes")
    private void repairValues(ArrayList<Piece> chopping) {

        HashMap<Integer, Triple> repairs = new HashMap<Integer, Triple>();
        truths = new HashMap<Integer, Integer>();
        distanceSequence = new HashMap<Integer, Integer>();

        for (int i = 0; i < chopping.size(); i++) {
            Piece piece = chopping.get(i);
            Piece prePiece = null;//new Piece();
            Piece nextPiece = null;//new Piece();
            if (i > 0)
                prePiece = chopping.get(i - 1);
            if (i < chopping.size() - 1)
                nextPiece = chopping.get(i + 1);
            piece.repairPiece(sequence, deltat, repairs, prePiece, nextPiece);
        }

        for (int i = 0; i < errorSequence.size(); i++) {
            int index = errorSequence.get(i);
            if (repairs.containsKey(index)) {
                Triple triple = repairs.get(index);
                int distance = sequence.get(index) - triple.getAvg();
                //System.out.println(index+"\t distance \t"+distance+"\t"+sequence.get(index)+"\t"+triple.getAvg());
                truths.put(index, triple.getAvg());
                distanceSequence.put(index, distance);
            }
        }
    }


    public HashMap<Integer, Integer> getDistanceSequence() {
        return distanceSequence;
    }


    public void setDistanceSequence(HashMap<Integer, Integer> distanceSequence) {
        this.distanceSequence = distanceSequence;
    }


    public double getPercent() {
        return percent;
    }


    public void setPercent(double percent) {
        this.percent = percent;
    }


    public double getAvgSeriesSize() {
        return avgSeriesSize;
    }


    public void setAvgSeriesSize(double avgSeriesSize) {
        this.avgSeriesSize = avgSeriesSize;
    }


    public int getMinSeriesSize() {
        return minSeriesSize;
    }


    public void setMinSeriesSize(int minSeriesSize) {
        this.minSeriesSize = minSeriesSize;
    }


    public int getMaxSeriesSize() {
        return maxSeriesSize;
    }


    public void setMaxSeriesSize(int maxSeriesSize) {
        this.maxSeriesSize = maxSeriesSize;
    }
}
