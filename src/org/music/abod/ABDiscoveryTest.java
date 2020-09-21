package org.music.abod;

import org.music.abcod.PartitionProcessor;
import org.music.data.ReleaseLabel;

import java.util.ArrayList;

public class ABDiscoveryTest {
    public static void main(String[] args) throws Exception {
        String SQL =
                "select id, date, title as release_1, catno, label as label_name from music.music_release " +
                        "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
                        "order by label, catno, date limit 600000";
        System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
        testScalability(SQL);

//        double[] input = {1990, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
//        ABDiscovery discovery = new ABDiscovery();
//        double value = 2000;
//        int position = discovery.binarySearchLeftMostPosition(value, input);
//        System.out.println(value +"\t"+position+"\t"+input[position]);
    }

    public static void testScalability(String sql) throws Exception {
        PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
        ArrayList dataset = parProcessor.getRelabels();
        int totalSize = dataset.size();
        int partition = 10;
        int partitionSize = totalSize / partition;
        System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

        for (int p = 0; p < partition; p++) {
            int subSize = partitionSize * (p + 1);
            ArrayList subDataSet = new ArrayList();
            for (int j = 0; j < subSize; j++) {
                subDataSet.add(subDataSet.size(), dataset.get(j));
            }

            double[] yearSequence = new double[subDataSet.size()];
            for (int i = 0; i < subDataSet.size(); i++) {
                ReleaseLabel relabel = (ReleaseLabel) subDataSet.get(i);

                int year = relabel.getDate();
                yearSequence[i] = year;
            }

//            for(int m = 0; m < yearSequence.length; m++){
//                System.out.println(yearSequence[m]);
//            }
            System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

            ABDiscovery discovery = new ABDiscovery();

            double bandWidth = 3.0;
            double startLMB = System.currentTimeMillis();
            double[] lmb = discovery.computeLMB(yearSequence, bandWidth);
            double endLMB = System.currentTimeMillis();
            double lmbTime = endLMB - startLMB;
            System.out.println("LMB Runtime: \t" + yearSequence.length + "\t" + lmbTime + "\t" + lmb.length);


//            System.out.println("lmb: "+"\t"+lmb.length);
//            for(int m = 0; m < lmb.length; m++){
//                System.out.println(lmb[m]);
//            }
//            System.out.println("baselineLMB: "+"\t"+baselineLMB.length);
//            for(int m = 0; m < baselineLMB.length; m++){
//                System.out.println(baselineLMB[m]);
//            }

//            double[] baselineLMB = discovery.computeBaselineLMB(yearSequence, bandWidth);
            double endBaselineLMB = System.currentTimeMillis();
            double baselineLMBTime = endBaselineLMB - endLMB;
//            System.out.println("LMB Runtime: \t" + yearSequence.length + "\t" + baselineLMBTime + "\t" + baselineLMB.length);
        }
    }
}
