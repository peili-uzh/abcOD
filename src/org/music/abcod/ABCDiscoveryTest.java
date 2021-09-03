package org.music.abcod;

import org.music.abod.ABDiscovery;
import org.music.abod.Outlier;
import org.music.connection.DataLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ABCDiscoveryTest {
    public static void main(String[] args) throws Exception {
        int dataSize = 1000000;
        double bandWidth = 0;
        String SQL = new ExperimentInput().musicABCForScalability;//sfoFlightABCForMultipleAttributes;//sfoFlightABCForScalability;//usFlightForScalability
//        computeBandWidth(SQL, dataSize);                                                 +
        testScalability(SQL, dataSize, bandWidth);
//        testMultiAttributeScalability(SQL, dataSize, bandWidth);
//        analyseSeries(SQL, dataSize, bandWidth);

    }


    public static void computeBandWidth(String sql, int dataSize) throws Exception {
        String queryWithDataSize = sql + " limit " + dataSize + ";";
        HashMap<String, ArrayList<Double>> groupedYearSequence = new DataLoader().loadDataForABCDiscovery(queryWithDataSize, dataSize);

        double maxBandWidth = 6.0;
        double step = 0.1;

        for (Map.Entry<String, ArrayList<Double>> indexYearSequence : groupedYearSequence.entrySet()) {
            String groupId = indexYearSequence.getKey();
            ArrayList<Double> years = indexYearSequence.getValue();
            double[] yearSequence = new double[years.size()];
            for (int i = 0; i < years.size(); i++) {
                yearSequence[i] = years.get(i);
                System.out.println(i + "\t" + years.get(i));
//                System.out.println(years.size() + "\t" + groupId + "\t" + i + "\t" + years.get(i));
            }

//            HashMap<Double, Double> distancePerBandWidth = new HashMap<Double, Double>();
            ArrayList<double[]> distancePerBandWidth = new ArrayList<>();

            for (double bandWidth = 0; bandWidth <= maxBandWidth; bandWidth += step) {
//                System.out.println("bandWidth\t" + bandWidth);
                int[] lmb = new ABDiscovery().computeLMB(yearSequence, bandWidth, new Outlier());

                double distances = 0; // the sum of distance between each outlier and its repair = 0;

                for (int j = 0; j < lmb.length; j++) {
                    int currentLMBIndex = lmb[j];
                    int nextLMBIndex = lmb[Math.min(j + 1, lmb.length - 1)];
                    if (j == 0 && currentLMBIndex > 0) {
                        for (int outlierIndex = 0; outlierIndex < currentLMBIndex; outlierIndex++) {
                            distances += accumulateDistance(yearSequence, outlierIndex, yearSequence[currentLMBIndex]);
                        }
                    }
                    if ((nextLMBIndex - currentLMBIndex) > 1) {
                        double repair = (yearSequence[currentLMBIndex] + yearSequence[nextLMBIndex]) / 2;
                        for (int outlierIndex = currentLMBIndex + 1; outlierIndex < nextLMBIndex; outlierIndex++) {
                            distances += accumulateDistance(yearSequence, outlierIndex, repair);
                        }
                    }
                    if (j == lmb.length - 1 && currentLMBIndex < yearSequence.length - 1) {
                        for (int outlierIndex = currentLMBIndex + 1; outlierIndex < yearSequence.length; outlierIndex++) {
                            distances += accumulateDistance(yearSequence, outlierIndex, yearSequence[currentLMBIndex]);
                        }
                    }
                }

                double[] bandWidthWithDistance = new double[2];
                bandWidthWithDistance[0] = bandWidth;
                bandWidthWithDistance[1] = distances;
                if (lmb.length < yearSequence.length) {
                    bandWidthWithDistance[1] = distances / (yearSequence.length - lmb.length);
                }
                System.out.println("bandWidth\t" + bandWidth + "\t" + bandWidthWithDistance[1]);
                distancePerBandWidth.add(bandWidthWithDistance);
            }

            ArrayList<double[]> distanceDegrees = new ArrayList<>();
            for (int i = 0; i < distancePerBandWidth.size(); i++) {
                double degree = 0;
                double bandWidth = distancePerBandWidth.get(i)[0];
                if (bandWidth > 0) {
                    double currentDistance = distancePerBandWidth.get(i)[1];
                    double previousDistance = distancePerBandWidth.get(i - 1)[1];
                    degree = currentDistance - previousDistance;
                    if (currentDistance != previousDistance) {
                        degree = (currentDistance - previousDistance) / (currentDistance);
                    }
                }
                double[] bandWithWithDegree = new double[2];
                bandWithWithDegree[0] = bandWidth;
                bandWithWithDegree[1] = degree;
                distanceDegrees.add(bandWithWithDegree);
            }

            System.out.println(distanceDegrees.size());
            double maxDegree = 0;
            double optimalBandWidth = 0;
            for (double[] distanceDegree : distanceDegrees) {
                double currentBandWidth = distanceDegree[0];
                double currentDegree = distanceDegree[1];
                System.out.println("bandWidth\t" + distanceDegree[0] + "\t" + distanceDegree[1]);

                if (maxDegree < currentDegree) {
                    maxDegree = currentDegree;
                    optimalBandWidth = currentBandWidth;
                }
            }

            System.out.println("optimalBandWidth\t" + optimalBandWidth + "\t(" + maxDegree + ")");
        }
    }

    private static double accumulateDistance(double[] yearSequence, int outlierIndex, double repair) {
        double outlier = yearSequence[outlierIndex];
        double distance = Math.abs(outlier - repair);
//        System.out.println("\t" + outlier + "\t" + repair + "\t" + distance);

        return distance;
    }

    public static void analyseSeries(String sql, int dataSize, double bandWidth) throws Exception {
        String queryWithDataSize = sql + " limit " + dataSize + ";";
        HashMap<String, ArrayList<Double>> groupedYearSequence = new DataLoader().loadDataForABCDiscovery(queryWithDataSize, dataSize);
        double numberSeries = 0;
        int maxSeries = 0;
        int minSeries = dataSize;
        double outlierCount = 0;
        double incSeriesCount = 0;
        double decSeriesCount = 0;
        double tupleCount = 0;

        for (Map.Entry<String, ArrayList<Double>> indexYearSequence : groupedYearSequence.entrySet()) {
            String groupId = indexYearSequence.getKey();
            ArrayList<Double> years = indexYearSequence.getValue();
            tupleCount += years.size();
            double[] yearSequence = new double[years.size()];
            for (int i = 0; i < years.size(); i++) {
                yearSequence[i] = years.get(i);
//                System.out.println(i+"\t"+years.get(i));
//                System.out.println(years.size()+"\t"+groupId+"\t"+i+"\t"+years.get(i));
            }

            ABCDiscovery abcDiscovery = new ABCDiscovery();

            int errorThreshold = 100;
            ArrayList<Integer> series = abcDiscovery.computeSeries(yearSequence, bandWidth, errorThreshold);
//                System.out.println("series.size\t"+series.size());
            numberSeries += series.size();

            for (int i = series.size() - 1; i >= 0; i--) {
                int startIndex = series.get(i);
                int endIndex = yearSequence.length;
                if (i > 0) {
                    endIndex = series.get(i - 1);
                }
                int seriesLength = endIndex - startIndex;
                maxSeries = Math.max(maxSeries, seriesLength);
                minSeries = Math.min(minSeries, seriesLength);
                /*for(int j = startIndex; j< endIndex; j++){
                    String tab = "";
                    int numTab = series.size()-i-1;
                    int count = 0;
                    while(count < numTab){
                        tab += "\t";
                        count +=1;
                    }
                    System.out.println(j+"\t"+tab+yearSequence[j]);
                }*/

//                    System.out.println("\t"+i+"\t"+startIndex+"\t"+endIndex+"\t"+seriesLength);
//                    System.out.println("\t"+i+"\t series: "+yearSequence[startIndex] +"("+startIndex+") - "+yearSequence[endIndex-1]+"("+(endIndex-1)+")");
                double[] subSequence = new double[seriesLength];
                System.arraycopy(yearSequence, startIndex, subSequence, 0, seriesLength);
                int[] lmb = new ABDiscovery().computeLMB(subSequence, bandWidth, new Outlier());
                outlierCount += subSequence.length - lmb.length;

                if ((yearSequence[endIndex - 1] >= (yearSequence[startIndex] + bandWidth)) || (yearSequence[endIndex - 1] >= yearSequence[startIndex])) {
                    incSeriesCount += 1;
                } else {
                    decSeriesCount += 1;
                }
            }
        }
        System.out.println("numberSeries\t" + numberSeries);
        System.out.println("maxSeries\t" + maxSeries);
        System.out.println("minSeries\t" + minSeries);
        System.out.println("avgSeries\t" + dataSize / numberSeries);
        System.out.println("incSeriesCount\t" + incSeriesCount / numberSeries);
        System.out.println("decSeriesCount\t" + decSeriesCount / numberSeries);
        System.out.println("outlierCount\t" + outlierCount / tupleCount);
    }

    public static void testScalability(String sql, int dataSize, double bandWidth) throws Exception {
        int totalSize = dataSize;
        int partition = 10;
        int partitionSize = totalSize / partition;

        for (int p = 2; p < partition; p++) {
            int subSize = partitionSize * (p + 1);
            String queryWithDataSize = sql + " limit " + subSize + ";";
            HashMap<String, ArrayList<Double>> groupedYearSequence = new DataLoader().loadDataForABCDiscovery(queryWithDataSize, subSize);

            double startSeriesTime = System.currentTimeMillis();
            for (Map.Entry<String, ArrayList<Double>> indexYearSequence : groupedYearSequence.entrySet()) {
                String groupId = indexYearSequence.getKey();
                ArrayList<Double> years = indexYearSequence.getValue();
                double[] yearSequence = new double[years.size()];
                for (int i = 0; i < years.size(); i++) {
                    yearSequence[i] = years.get(i);
//                    System.out.println(years.size()+"\t"+groupId+"\t"+i+"\t"+years.get(i));
                }

                ABCDiscovery abcDiscovery = new ABCDiscovery();

                int errorThreshold = 4;
                ArrayList<Integer> series = abcDiscovery.computeSeriesWithoutPiece(yearSequence, bandWidth, errorThreshold);
//                ArrayList<Integer> series = abcDiscovery.computeSeries(yearSequence, bandWidth, errorThreshold);

//                System.out.println(series.size());
            }

            double endSeriesTime = System.currentTimeMillis();
            double seriesTime = endSeriesTime - startSeriesTime;
            System.out.println("Series Runtime: \t" + (p + 1) + "\t" + seriesTime);
        }
    }

    public static void testMultiAttributeScalability(String sql, int dataSize, double bandWidth) throws Exception {
        int totalSize = dataSize;
        int partition = 1;
        int partitionSize = totalSize / partition;

        for (int p = 0; p < partition; p++) {
            int subSize = partitionSize * (p + 1);
            String queryWithDataSize = sql + " limit " + subSize + ";";
            double startSeriesTime = System.currentTimeMillis();
            HashMap<String, ArrayList<Double>> groupedYearSequence = new DataLoader().loadMultiAttributeDataForABCDiscovery(queryWithDataSize, subSize);

            for (Map.Entry<String, ArrayList<Double>> indexYearSequence : groupedYearSequence.entrySet()) {
                String groupId = indexYearSequence.getKey();
                ArrayList<Double> years = indexYearSequence.getValue();
                double[] yearSequence = new double[years.size()];
                for (int i = 0; i < years.size(); i++) {
                    yearSequence[i] = years.get(i);
//                    System.out.println(years.size()+"\t"+groupId+"\t"+i+"\t"+years.get(i));
                }

                ABCDiscovery abcDiscovery = new ABCDiscovery();

                int errorThreshold = 4;
                ArrayList<Integer> series = abcDiscovery.computeSeries(yearSequence, bandWidth, errorThreshold);
//                System.out.println(series.size());
            }

            double endSeriesTime = System.currentTimeMillis();
            double seriesTime = endSeriesTime - startSeriesTime;
            System.out.println("Series Runtime: \t" + (p + 1) + "\t" + seriesTime);
        }
    }
}
