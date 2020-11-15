package org.music.abod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ABDiscovery {

    public int[] computeLMB(double[] input, double bandWidth, Outlier outlier) {
        double infinityValue = Double.POSITIVE_INFINITY;
        double minValue = 0.0;

        double[] bestIncValues = new double[input.length + 1];
        ArrayList<int[]> maxIncLengths = new ArrayList<>();
        double[] bestDecValues = new double[input.length + 1];
        ArrayList<int[]> maxDecLengths = new ArrayList<>();

        // initialise best values
        bestIncValues[0] = minValue;
        bestDecValues[0] = infinityValue;


        for (int i = 1; i < input.length; i++) {
            bestIncValues[i] = infinityValue;
            bestDecValues[i] = minValue;
        }

        bestIncValues[input.length] = infinityValue;
        bestDecValues[input.length] = minValue;
        int incBandLength = 1;
        int decBandLength = 1;

        for (int i = 1; i < input.length + 1; i++) {
            double value = input[i - 1];
            int bestPosition1 = binarySearchLeftMostPosition(value, bestIncValues, true);
            int bestPosition2 = binarySearchLeftMostPosition(value + bandWidth, bestIncValues, true);
            incBandLength = Math.max(incBandLength, bestPosition2);

            int bestPosition3 = binarySearchLeftMostPosition(value, bestDecValues, false);
            int bestPosition4 = binarySearchLeftMostPosition(value + bandWidth, bestDecValues, false);
            decBandLength = Math.max(decBandLength, bestPosition4);

            int[] incLengths = new int[2];
            incLengths[0] = bestPosition1;
            incLengths[1] = bestPosition2;
            maxIncLengths.add(i - 1, incLengths);

            int[] decLengths = new int[2];
            decLengths[0] = bestPosition3;
            decLengths[1] = bestPosition4;
            maxDecLengths.add(i - 1, decLengths);

            for (int k = bestPosition2; k >= bestPosition1; k--) {
                double bestValue = minValue;
                if (k > 1) {
                    bestValue = bestIncValues[k - 1];
                }
                bestIncValues[k] = Math.max(bestValue, value);
            }

            for (int k = bestPosition4; k >= bestPosition3; k--) {
                double bestValue = infinityValue;
                if (k > 1) {
                    bestValue = bestDecValues[k - 1];
                }
                bestDecValues[k] = Math.min(bestValue, value);
            }
        }

        int[] lmb;
        if (incBandLength > decBandLength) {
            lmb = retrieveLMB(input, incBandLength, maxIncLengths, outlier);
        } else {
            lmb = retrieveLMB(input, decBandLength, maxDecLengths, outlier);
        }

        return lmb;
    }

    /*
       lmb is array of indices in input, e.g, lmb[0]=1 means input[1] is the first element in the lmb
     */
    private int[] retrieveLMB(double[] input, int bandLength, ArrayList<int[]> maxDecLengths, Outlier outlier) {
        int[] lmb = new int[bandLength];
        int maxOutlierCount = 0;
        int currentOutlierCount;
        int previousIndex = input.length;
        int length = bandLength;
        for (int i = input.length - 1; i >= 0; i--) {
            int[] currentLengths = maxDecLengths.get(i);
            int minLength = currentLengths[0];
            int maxLength = currentLengths[1];
            if (minLength <= length && length <= maxLength) {
//                lmb[length - 1] = input[i];
                lmb[length - 1] = i;
                length = length - 1;
                currentOutlierCount = previousIndex - i - 1;
                maxOutlierCount = Math.max(maxOutlierCount, currentOutlierCount);
                previousIndex = i;
            }
        }

        maxOutlierCount = Math.max(maxOutlierCount, previousIndex);
        outlier.setMaxOutlierCount(maxOutlierCount);
        return lmb;
    }

    // search the left
    protected int binarySearchLeftMostPosition(double value, double[] bestValues, boolean isIncreasing) {
        return binarySearch(value, bestValues, 0, bestValues.length - 1, isIncreasing);
    }

    private int binarySearch(double value, double[] bestValues, int low, int high, boolean isIncreasing) {
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
        if (isIncreasing) {
            if (value >= bestValues[leftPosition] && value < bestValues[rightPosition])
                return rightPosition;
            if (value >= bestValues[rightPosition]) {
                return binarySearch(value, bestValues, rightPosition + 1, high, true);
            }
        } else {
            if (value <= bestValues[leftPosition] && value > bestValues[rightPosition]) {
                return rightPosition;
            }
            if (value <= bestValues[rightPosition]) {
                return binarySearch(value, bestValues, rightPosition + 1, high, false);
            }
        }

        return binarySearch(value, bestValues, low, leftPosition, isIncreasing);
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
