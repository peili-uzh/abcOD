package org.music.abcod;

import org.music.abod.ABDiscovery;
import org.music.abod.Outlier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ABCDiscovery {
    public ArrayList<Integer> computeSeriesWithoutPiece(double[] input, double bandWidth, int errorThreshold) {
        ArrayList<Integer> pieceBoundaries = new ArrayList<>();
        for (int i = 0; i <= input.length; i++) {
            pieceBoundaries.add(i);
        }

        ArrayList<Integer> seriesBoundaries = new ArrayList<>();
//        if(input.length>1000){
//            System.out.println(input.length+"\t"+pieceBoundaries.size());
//        }

        double[] gains = new double[pieceBoundaries.size()];
        int[] seriesIndices = new int[pieceBoundaries.size()];

        for (int i = 0; i < pieceBoundaries.size(); i++) {
            gains[i] = 0;
        }

        for (int j = 0; j < pieceBoundaries.size() - 1; j++) {
            int startIndex = pieceBoundaries.get(j);
            int endIndex = pieceBoundaries.get(j + 1);
            int pieceLength = endIndex - startIndex;

            if (j == 0) {
                double gain = Math.pow(pieceLength, 2);
                gains[j] = gain;
                seriesIndices[j] = j;
            } else {
                for (int i = 0; i <= j; i++) {
                    int subSequenceLength = endIndex - pieceBoundaries.get(i);
                    double[] subSequence = new double[subSequenceLength];
                    System.arraycopy(input, pieceBoundaries.get(i), subSequence, 0, subSequenceLength);
                    Outlier outlier = new Outlier();

                    int[] lmb = new ABDiscovery().computeLMB(subSequence, bandWidth, outlier);

                    double gain = Math.pow(lmb.length, 2) - Math.pow((subSequenceLength - lmb.length), 2);
                    if (outlier.getMaxOutlierCount() <= errorThreshold) {
                        double currentGain = gains[j];
                        double accumulatedGain = 0;
                        if (i > 0) {
                            accumulatedGain = gains[i - 1];
                        }
                        double candidateGain = accumulatedGain + gain;
                        if (candidateGain > currentGain) {
                            gains[j] = candidateGain;
                            seriesIndices[j] = i;
                        }
                    }
                }

            }
        }


        int index = seriesIndices.length - 2;
//        seriesBoundaries.add(input.length);
        while (index >= 0) {
            int inputIndex = pieceBoundaries.get(seriesIndices[index]);
            index = seriesIndices[index] - 1;
//            System.out.println("\t\t"+inputIndex);
            seriesBoundaries.add(inputIndex);
        }
//        seriesBoundaries.add(0);

        return seriesBoundaries;
    }

    public ArrayList<Integer> computeSeries(double[] input, double bandWidth, int errorThreshold) {
        ArrayList<Integer> pieceBoundaries = generatePieces(input, bandWidth);
//                for(int piece: pieceBoundaries){
//                    System.out.println("\t"+piece);
//                }
        ArrayList<Integer> seriesBoundaries = new ArrayList<>();
//        if(input.length>1000){
//            System.out.println(input.length+"\t"+pieceBoundaries.size());
//        }

        double[] gains = new double[pieceBoundaries.size()];
        int[] seriesIndices = new int[pieceBoundaries.size()];

        for (int i = 0; i < pieceBoundaries.size(); i++) {
            gains[i] = 0;
        }

        for (int j = 0; j < pieceBoundaries.size() - 1; j++) {
            int startIndex = pieceBoundaries.get(j);
            int endIndex = pieceBoundaries.get(j + 1);
            int pieceLength = endIndex - startIndex;

            if (j == 0) {
                double gain = Math.pow(pieceLength, 2);
                gains[j] = gain;
                seriesIndices[j] = j;
            } else {
                for (int i = 0; i <= j; i++) {
                    int subSequenceLength = endIndex - pieceBoundaries.get(i);
                    double[] subSequence = new double[subSequenceLength];
                    System.arraycopy(input, pieceBoundaries.get(i), subSequence, 0, subSequenceLength);
                    Outlier outlier = new Outlier();

                    int[] lmb = new ABDiscovery().computeLMB(subSequence, bandWidth, outlier);

                    double gain = Math.pow(lmb.length, 2) - Math.pow((subSequenceLength - lmb.length), 2);
                    if (outlier.getMaxOutlierCount() <= errorThreshold) {
                        double currentGain = gains[j];
                        double accumulatedGain = 0;
                        if (i > 0) {
                            accumulatedGain = gains[i - 1];
                        }
                        double candidateGain = accumulatedGain + gain;
                        if (candidateGain > currentGain) {
                            gains[j] = candidateGain;
                            seriesIndices[j] = i;
                        }
                    }
                }

            }
        }


        int index = seriesIndices.length - 2;
//        seriesBoundaries.add(input.length);
        while (index >= 0) {
            int inputIndex = pieceBoundaries.get(seriesIndices[index]);
            index = seriesIndices[index] - 1;
//            System.out.println("\t\t"+inputIndex);
            seriesBoundaries.add(inputIndex);
        }
//        seriesBoundaries.add(0);

        return seriesBoundaries;
    }

    public ArrayList<Integer> generatePieces(double[] input, double bandwidth) {
        double[][] prePieces = new double[input.length][2];
        int[][] prePieceLengths = new int[input.length][2];

        // initialise pre-pieces
        prePieces[0][0] = input[0];
        prePieces[0][1] = input[0];
        prePieceLengths[0][0] = 1;
        prePieceLengths[0][1] = 1;

        for (int i = 1; i < input.length; i++) {
            double year = input[i];
            double maxYearIncPiece = prePieces[i - 1][0];
            double minYearDecPiece = prePieces[i - 1][1];
            int incPieceLength = prePieceLengths[i - 1][0];
            int decPieceLength = prePieceLengths[i - 1][1];

            if (year + bandwidth >= maxYearIncPiece) {
                prePieces[i][0] = Math.max(year, maxYearIncPiece);
                prePieceLengths[i][0] = incPieceLength + 1;
            } else {
                prePieces[i][0] = year;
                prePieceLengths[i][0] = 1;
            }

            if (year - bandwidth <= minYearDecPiece) {
                prePieces[i][1] = Math.min(year, minYearDecPiece);
                prePieceLengths[i][1] = decPieceLength + 1;
            } else {
                prePieces[i][1] = year;
                prePieceLengths[i][1] = 1;
            }
        }

        ArrayList<Integer> pieceIndices = new ArrayList<>();
        pieceIndices.add(0);
        for (int i = 1; i < input.length; i++) {
            int incPieceLength = prePieceLengths[i][0];
            int decPieceLength = prePieceLengths[i][1];
            int previousIncLength = prePieceLengths[i - 1][0];
            int previousDecLength = prePieceLengths[i - 1][1];

            if (incPieceLength == 1 && previousIncLength > previousDecLength) {
                insert(pieceIndices, i);
                insert(pieceIndices, i - previousIncLength);
            }

            if (decPieceLength == 1 && previousDecLength > previousIncLength) {
                insert(pieceIndices, i);
                insert(pieceIndices, i - previousDecLength);
            }
        }

        insert(pieceIndices, input.length);

        return pieceIndices;
    }

    // insert value into sorted list; if value exists, ignore
    private void insert(ArrayList<Integer> list, int value) {
        int index = Collections.binarySearch(list, value, Comparator.comparing(Integer::intValue));

        if (index < 0) {
            index = -index - 1;
            list.add(index, value);
        }
    }
}
