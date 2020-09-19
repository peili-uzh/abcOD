package org.music.block;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;

public class StandardBlocking extends AbstractBlock {

    private Block block;
    private ArrayList entities;
    private HashMap<String, Integer> keymap;

    public StandardBlocking(ArrayList records, HashMap<Integer, Block> blocks, HashMap<Integer, String> invertedList, String blockKey, double para) {

        /*
         *
         */
        int count = (int) para;
        int seq = 0;
        keymap = new HashMap();

        System.out.println("blocking method: \t standarBlocking on: \t" + blockKey + "\t paramter: \t" + para);

        for (int i = 0; i < records.size(); i++) {
            ArtistData artist = (ArtistData) records.get(i);
            //int id = artist.getId();
            String value = "";
            if (blockKey.equals("name")) {
                value = artist.getName();
            } else if (blockKey.equals("real_name")) {
                value = artist.getReal_name();
            }


            if (!value.equals("")) {

                String key = value;
                if (value.length() > para) {
                    //key = value.substring(0, para).trim();
                    key = value.substring(value.length() - count, value.length());

                }
                //key = value.split(" ")[0];
                //String key = value.split(" ")[value.split(" ").length-1];
                //key = value;
                //key = "baseline";
                //System.out.println(i+"\t"+value+": \t"+key);

                if (!key.equals("")) {
                    // update or add blocks
                    if (!keymap.containsKey(key)) {
                        entities = new ArrayList();
                        entities.add(i);
                        block = new Block(seq, entities);
                        blocks.put(seq, block);
                        keymap.put(key, seq);
                        //System.out.println(seq+"\t"+key+": \t"+entities);
                        seq += 1;

                    } else {
                        int blockid = keymap.get(key);
                        block = blocks.get(blockid);
                        entities = block.getEntities();
                        entities.add(i);
                        block.setEntities(entities);
                        blocks.put(blockid, block);
                        //System.out.println(blockid+"\t"+key+": \t"+entities);
                    }
                    // add new inverted index
                    invertedList.put(i, key);
                }
            }
        }
        blockStatistics(blocks);
        System.out.println("----------- standard blocking quality -------------");
        Evaluation evaluation = new Evaluation(records, invertedList);
    }

}
