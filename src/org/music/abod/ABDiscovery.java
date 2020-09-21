package org.music.abod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ABDiscovery {

    public double[] computeLMB(double[] input, double bandWidth) {
        double[] lmb;

        double infinityValue = Double.POSITIVE_INFINITY;
        double minValue = 0.0;

        double[] bestValues = new double[input.length + 1];
        ArrayList<List<Integer>> maxLengths = new ArrayList<>();

        // initialise best values
        bestValues[0] = minValue;
//        Set<Integer> emptySet = Collections.emptySet();
        maxLengths.add(0, new ArrayList<Integer>());
        for (int i = 1; i < input.length; i++) {
            bestValues[i] = infinityValue;
            int size = (int) bandWidth;
            maxLengths.add(i, new ArrayList<Integer>(size));
        }

        bestValues[input.length] = infinityValue;
        int bandLength = 1;

        System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

        for (int i = 1; i < input.length + 1; i++) {
            double value = input[i - 1];
            int bestPosition1 = binarySearchLeftMostPosition(value, bestValues);
            int bestPosition2 = binarySearchLeftMostPosition(value + bandWidth, bestValues);
            bandLength = Math.max(bandLength, bestPosition2);
            for (int k = bestPosition2; k >= bestPosition1; k--) {
                double bestValue = minValue;
                if (k > 1)
                    bestValue = bestValues[k - 1];
                bestValues[k] = Math.max(bestValue, value);
                List<Integer> lengths = maxLengths.get(i - 1);
                lengths.add(k);
                //lengths.add(lengths.size(), k);
//                System.gc();
            }
        }

        lmb = new double[bandLength];
        int length = bandLength;
        for (int i = input.length - 1; i >= 0; i--) {
            List<Integer> currentLengths = maxLengths.get(i);
            if (currentLengths.contains(length)) {
                lmb[length - 1] = input[i];
                length = length - 1;
            }
        }

        return lmb;
    }

    // search the left
    protected int binarySearchLeftMostPosition(double value, double[] bestValues) {
        return binarySearch(value, bestValues, 0, bestValues.length - 1);
    }

    private int binarySearch(double value, double[] bestValues, int low, int high) {
        if (high < low)
            return -1;
        int rightPosition = (low + high) / 2;
        int leftPosition = Math.max(low, rightPosition - 1);
        if (low == high - 1) {
            rightPosition = high;
            leftPosition = low;
        }

        if (leftPosition < 0) {
            return rightPosition + 1;
        }

        if (leftPosition == rightPosition) {
            return leftPosition;
        }
        if (value >= bestValues[leftPosition] && value < bestValues[rightPosition])
            return rightPosition;
        if (value >= bestValues[rightPosition]) {
            return binarySearch(value, bestValues, rightPosition + 1, high);
        }
        return binarySearch(value, bestValues, low, leftPosition);
    }

    public double[] computeBaselineLMB(double[] input, double bandWidth) {
        double[] lmb;
        HashMap<Double, Integer>[] bestValueHashArray = new HashMap[input.length];

        // initialise bandLengths and bestValues
        for (int i = 0; i < input.length; i++) {
            HashMap<Double, Integer> value = new HashMap();
            value.put(input[i], 1);
            bestValueHashArray[i] = value;
        }

        int maxLength = 1;

        for (int i = 1; i < input.length; i++) {
            for (int j = 0; j < i; j++) {
                HashMap<Double, Integer> previousBestValueHash = bestValueHashArray[j];
                for (Map.Entry<Double, Integer> previousEntry : previousBestValueHash.entrySet()) {
                    double previousBestValue = previousEntry.getKey();

                    if (input[i] + bandWidth >= previousBestValue) {
                        maxLength = updateBestValues(input, previousEntry, bestValueHashArray, i, maxLength);
                    }
                }
            }
        }

        lmb = new double[maxLength];
        int length = maxLength;
        for (int i = input.length - 1; i >= 0; i--) {
            HashMap<Double, Integer> bestValueWithLength = bestValueHashArray[i];
            if (bestValueWithLength.containsValue(length)) {
                lmb[length - 1] = input[i];
                length = length - 1;
            }
        }

        return lmb;
    }

    private int updateBestValues(
            double[] input,
            Map.Entry<Double, Integer> previousEntry,
            HashMap<Double, Integer>[] bestValueHashArray,
            int i, // index
            int maxLength
    ) {
        double previousBestValue = previousEntry.getKey();
        int previousLength = previousEntry.getValue();
        double newBestValue = Math.max(input[i], previousBestValue);

        if (bestValueHashArray[i].containsKey(newBestValue)) {
            if (bestValueHashArray[i].get(newBestValue) < previousLength + 1) {
                bestValueHashArray[i].computeIfPresent(newBestValue, (key, value) -> previousLength + 1);
                maxLength = Math.max(previousLength + 1, maxLength);
            }
        } else {
            bestValueHashArray[i].put(newBestValue, previousLength + 1);
            maxLength = Math.max(previousLength + 1, maxLength);
        }

        return maxLength;
    }
}
