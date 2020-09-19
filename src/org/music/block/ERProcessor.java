package org.music.block;

import java.util.ArrayList;

public class ERProcessor extends AbstractBlock {


    public ERProcessor(String sql, String blockKey, double th1) throws Exception {
        this.records = new ArrayList();
        queryRecords(records, sql);
        this.blockKey = blockKey;
        this.th1 = th1;


    }

    public void process(BlockProcessor blockProcessor) {
        double min1 = 0.1;
        double min2 = min1;
        double step = 0.1;
        th2 = 1;
        if (th1 > 1) {
            min1 = 1;
            step = 1;
            th2 = this.th1;
        }

        System.out.println(min1 + "\t" + min2 + "\t" + step + "\t" + th1 + "\t" + th2);

        while (min1 <= th1) {
            while (min2 <= th2) {

                System.out.println("\r\n th1=" + "\t" + min1 + "\t th2=" + min2);
                blockProcessor.process(records, blockKey, min1, min2);

                min2 += step;

                //System.out.println(min1+"\t"+min2+"\t"+step+"\t"+th1+"\t"+th2);
            }
            min1 += step;
            min2 = min1;
        }

    }

}
