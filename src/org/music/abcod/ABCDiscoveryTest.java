package org.music.abcod;

import org.music.connection.DataLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ABCDiscoveryTest {
    public static void main(String[] args) throws Exception {
        int dataSize = 1000000;
        String SQL =
//                "select dep_time_in_hr as time from music.nationwide_2018_flight order by origin_airport_id, fl_date, crs_dep_time_in_hr, op_carrier_fl_num ";
                // sfo flight
//                "select time_in_hr as time from music.sfo_flight " +

//                        "order by airline, flight_number, transaction, date";
//         music scalability test:
                //
                "select concat(label, regexp_replace(catno, '[0-9]*', '', 'g'))  as group_id, date as time from music.music_release " +
                        "where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +//AND label = 'A&M Records'
                        "order by group_id, catno, date";
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
                if (yearSequence.length > 1000) {
//                    System.out.println(groupId);
                }

                ABCDiscovery abcDiscovery = new ABCDiscovery();

                double bandWidth = 3.0;
                int errorThreshold = 4;
                ArrayList<Integer> series = abcDiscovery.computeSeries(yearSequence, bandWidth, errorThreshold);
//                System.out.println(series.size());
            }

            double endSeriesTime = System.currentTimeMillis();
            double seriesTime = endSeriesTime - startSeriesTime;
            System.out.println("Series Runtime: \t" + subSize + "\t" + seriesTime);
        }
    }
}
