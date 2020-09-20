package org.music.car;

import org.music.abcod.PartitionProcessor;

import java.util.ArrayList;

public class SeriesTest {
    public static void main(String[] args) throws Exception {
        String sql = "select year, * from car.car_with_vin where block_id >0 order by block_id, order_id";
        PartitionProcessor parProcessor = new PartitionProcessor(sql, "cars");
        ArrayList dataset = parProcessor.getRelabels();


        // double deltat = 0.1;
        int deltat = 0;
        int theta = 4;
        while (deltat <= 20) {

            /**
             * series experiments
             *
             */
            // parProcessor.partitionWithGap("releaselabel", dataset);

            parProcessor.block("cars", dataset);
            double start = System.currentTimeMillis();

            parProcessor.series(deltat, theta);
            // parProcessor.seriesWithMultipleAttributes(deltat, theta);
            double end = System.currentTimeMillis();
            // System.out.println("runtime\t" + (end - start));

            // ScaleProcessor scaleProcessor = new
            // ScaleProcessor(parProcessor.finaltimelists,
            // parProcessor.finalreleaselists,deltat);
            // scaleProcessor.process();

            /**
             * time stamp repair
             *
             */
            // parProcessor.DynamicProcessWithGroundTruth(deltat, theta);
            // parProcessor.scaleRepairError(deltat, theta);
            // parProcessor.errorMissingProcess(deltat, theta); //
            // parProcessor.scaleRepairMissingError(deltat, theta);

            // parProcessor.BaselineDynamicProcessWithGroundTruth(deltat,
            // theta);
            deltat = deltat + 1;
        }

    }
}
