package org.music.abod;

import org.music.connection.DataLoader;

public class ABDiscoveryTest {
    public static void main(String[] args) throws Exception {
        int dataSize = 700000;
        String SQL = new ExperimentInput().USForScalability;
        testScalability(SQL, dataSize);
    }

    public static void testScalability(String sql, int dataSize) throws Exception {
        int totalSize = dataSize;
        int partition = 10;
        int partitionSize = totalSize / partition;

        for (int p = 0; p < partition; p++) {
            int subSize = partitionSize * (p + 1);

            String queryWithDataSize = sql + " limit " + subSize + ";";

            double[] yearSequence = new DataLoader().loadDataForABDiscovery(queryWithDataSize, subSize);
//            for(int m = 0; m < yearSequence.length; m++){
//                System.out.println(yearSequence[m]);
//            }
            ABDiscovery discovery = new ABDiscovery();

            double bandWidth = 1.2;
            double startLMB = System.currentTimeMillis();
//            int[] lmb = discovery.computeLMB(yearSequence, bandWidth, new Outlier());
            double endLMB = System.currentTimeMillis();
            double lmbTime = endLMB - startLMB;
//            System.out.println("LMB Runtime: \t" + yearSequence.length + "\t" + lmbTime + "\t" + lmb.length);


//            System.out.println("lmb: "+"\t"+lmb.length);
//            for(int m = 0; m < lmb.length; m++){
//                System.out.println(lmb[m]);
//            }
//            System.out.println("baselineLMB: "+"\t"+baselineLMB.length);
//            for(int m = 0; m < baselineLMB.length; m++){
//                System.out.println(baselineLMB[m]);
//            }

            double[] baselineLMB = discovery.computeBaselineLMB(yearSequence, bandWidth);
            double endBaselineLMB = System.currentTimeMillis();
            double baselineLMBTime = endBaselineLMB - endLMB;
            System.out.println("LMB Runtime: \t" + yearSequence.length + "\t" + baselineLMBTime + "\t" + baselineLMB.length);
        }
    }
}
