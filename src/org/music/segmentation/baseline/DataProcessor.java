package org.music.segmentation.baseline;

import org.music.missingtime.PartitionProcessor;

import java.util.ArrayList;

/**
 * This processor is responsible for reading input sequence from DB.
 *
 * @author peili
 */
public class DataProcessor extends PartitionProcessor {

    /**
     * The constructor reads data from DB
     *
     * @param sql
     * @param entity
     * @throws Exception
     */
    public DataProcessor(String sql, String entity) throws Exception {
        super(sql, entity);

        @SuppressWarnings("rawtypes")
        ArrayList dataset = getRelabels();
        // partitionWithGap(entity, dataset);
        block(entity, dataset);
    }


}
