package org.music.abod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ABDiscovery {

    public double[] computeLMB(double[] input, double bandWidth) {
        double[] lmb;

        double infinityValue = Double.POSITIVE_INFINITY;
        double minValue = 0.0;

        double[] bestValues = new double[input.length + 1];
        ArrayList<Set<Integer>> maxLengths;

        bestValues[0] = minValue;
        for (int i = 1; i < input.length; i++) {
            bestValues[i] = infinityValue;
        }

        int lmbLength = 0;
        lmb = new double[lmbLength];

        return lmb;
    }

    // search the left
    private int binarySearchLeftMostPosition(double value, double[] bestValues) {
        return binarySearch(value, bestValues, 0, bestValues.length);
    }

    private int binarySearch(double value, double[] bestValues, int low, int high) {
        if (high < low)
            return -1;
        int rightPosition = (low + high) / 2;
        int leftPosition = rightPosition - 1;
        if (value > bestValues[leftPosition] && value <= bestValues[rightPosition])
            return rightPosition;
        if (value > bestValues[rightPosition])
            return binarySearch(value, bestValues, rightPosition, high);
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
