package org.music.missingtime;

import java.util.ArrayList;

public class SeriesProcessor {

    private int deltat;
    private int theta;
    private ArrayList<Integer> sequence;
    private ArrayList<Piece> pieces;

    public SeriesProcessor(int deltat, int theta, ArrayList<Integer> sequence, ArrayList<Piece> pieces) {
        this.deltat = deltat;
        this.theta = theta;
        this.sequence = sequence;
        this.pieces = pieces;
    }

    public SeriesProcessor() {
    }

    public ArrayList<Piece> seriesDiscovery() {
        /*
         * intialize chopping X that starts&ends at each piece; G stores the gain of each chopping in X
         **/
        ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
        ArrayList<Double> gains = new ArrayList<Double>();

        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            // System.out.println("piece: [" + piece.getStart() + "," +
            // piece.getEnd() + "]\t" + (piece.size));

            double gain = piece.computeGain(sequence, deltat);
            piece.setGain(gain);

            // add piece i to chopping i
            ArrayList<Piece> chopping = new ArrayList<Piece>();
            chopping.add(chopping.size(), piece);

            // add chopping i to choppings; add gain i to gains
            choppings.add(i, chopping);
            gains.add(i, gain);

        }

        /*
         * find the best chopping from piece 0 to piece i
         */
        for (int i = 1; i < pieces.size(); i++) { // 6; i++){// // loop from the 2nd piece
            Piece current_piece = pieces.get(i);
            //System.out.println("process piece \t"+i+"\t"+current_piece.getStart()+" to "+current_piece.getEnd());
            ArrayList<Piece> max_chopping = new ArrayList<Piece>();
            double max_gain = 0;

            for (int j = 0; j <= i; j++) {
                double gain = 0;
                ArrayList<Piece> chopping = new ArrayList<Piece>();
                // merge piece (i-j) to piece i
                Piece previous_piece = pieces.get(i - j);
                //System.out.println("\t\t merge piece "+previous_piece.getStart()+" to "+current_piece.getEnd());
                Piece new_piece = new Piece();
                new_piece.setStart(previous_piece.getStart());
                new_piece.setEnd(current_piece.getEnd());
                double new_gain = new_piece.computeGain(sequence, deltat);
                new_piece.setGain(new_gain);

                //System.out.println("\t\t max no of errors \t"+new_piece.getMaxErrors());

                if (new_piece.getMaxErrors() < theta) {

                    // get the best chopping from piece 0 to piece i-j-1, and its gain
                    if (i - j - 1 >= 0) {
                        ArrayList<Piece> previous_chopping = choppings.get(i - j - 1);
                        chopping.addAll(previous_chopping);
                        chopping.add(chopping.size(), new_piece);

                        double previous_gain = gains.get(i - j - 1);
                        gain = new_gain + previous_gain;
                    } else {
                        gain = new_gain;
                        chopping.add(chopping.size(), new_piece);
                    }

                    // check if current chopping is the best
                    if (gain > max_gain) {
                        max_chopping.clear();
                        max_chopping.addAll(chopping);
                        max_gain = gain;
                    }
                    //System.out.println("\t\t current gain / max_gain : "+gain+" ; "+max_gain);
                }
            }
            // update the best chopping from piece 0 to piece i, and gain
            choppings.set(i, max_chopping);
            gains.set(i, max_gain);

        }

        // best chopping:

        int n = choppings.size();
        ArrayList<Piece> chopping = new ArrayList<Piece>();
        // System.out.println("best chopping \t" + choppings.size());
        chopping = choppings.get(n - 1);
        for (int i = 0; i < chopping.size(); i++) {
            Piece piece = chopping.get(i);
            //System.out.println("\t"+i+"\t series: ["+piece.getStart()+","+piece.getEnd()+"] \t"+piece.getGain());
        }
        //System.out.println("\t max gain:"+gains.get(n-1));
        return chopping;

    }
}
