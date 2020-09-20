package org.music.abod;

import org.music.abcod.PartitionProcessor;
import org.music.data.ReleaseLabel;

import java.util.ArrayList;

public class ABDiscoveryTest {
    public static void main(String[] args) throws Exception {
        String SQL =
                "select id, date, title as release_1, catno, label as label_name from music.music_release " +
                        "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
                        "order by label, catno limit 100000";
        testScalability(SQL);

//        PartitionProcessor parProcessor = new PartitionProcessor(SQL, "releaselabel");
//        ArrayList dataset = parProcessor.getRelabels();
//        double[] yearSequence = new double[dataset.size()];
//        for (int i = 0; i < dataset.size(); i++) {
//                ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);
//
//                int year = relabel.getDate();
//                yearSequence[i] = year;
//        }
//
//        ABDiscovery discovery = new ABDiscovery();
//
//        double bandWidth = 3.0;
//        System.out.println(yearSequence.length);
////        for(int i = 0; i < yearSequence.length; i++){
////            System.out.println(yearSequence[i]);
////        }
//
//        double startLMB = System.currentTimeMillis();
//        double[] lmb = discovery.computeBaselineLMB(yearSequence, bandWidth);
//        double endLMB = System.currentTimeMillis();
//        double lmbTime = endLMB - startLMB;
//
//        System.out.println("LMB Runtime: \t" + lmbTime);
//        System.out.println("LMB size \t" + lmb.length);
    }

    public static void testScalability(String sql) throws Exception {
        PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
        ArrayList dataset = parProcessor.getRelabels();
        int totalSize = dataset.size();
        int partition = 10;
        int partitionSize = totalSize / partition;

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
//                System.out.println("dataset size: \t" + yearSequence.length);

            ABDiscovery discovery = new ABDiscovery();

            double bandWidth = 3.0;
            double startLMB = System.currentTimeMillis();
            double[] lmb = discovery.computeBaselineLMB(yearSequence, bandWidth);
            double endLMB = System.currentTimeMillis();
            double lmbTime = endLMB - startLMB;

            System.out.println("LMB Runtime: \t" + yearSequence.length + "\t" + lmbTime + "\t" + lmb.length);
//                System.out.println("LMB size \t" + lmb.length);
        }
    }
}
