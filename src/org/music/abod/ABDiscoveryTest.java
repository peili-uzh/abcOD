package org.music.abod;

import org.music.connection.DataLoader;

public class ABDiscoveryTest {
    public static void main(String[] args) throws Exception {
        int dataSize = 1000;
        String SQL =
//                "select dep_time_in_hr as time from music.nationwide_2018_flight order by origin_airport_id, fl_date, crs_dep_time_in_hr, op_carrier_fl_num ";
                // sfo flight
//                "select time_in_hr as time from music.sfo_flight " +
//                        "where time_in_hr IS NOT NULL and date IS NOT NULL " +
//                        "order by airline, flight_number, transaction, date";
//         music scalability test:
                "select date as time from music.music_release " +
                        "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
                        "order by label, catno, date";
        //// music accuracy test:
//                "select id, date, title as release_1, catno, label as label_name from music.music_release " +
//                        "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
//                        "order by label, catno, date";
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

            double bandWidth = 1.0;
            double startLMB = System.currentTimeMillis();
            int[] lmb = discovery.computeLMB(yearSequence, bandWidth, new Outlier());
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
