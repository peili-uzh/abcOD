package org.music.block;

import com.wcohen.secondstring.JaroWinkler;
import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;
import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Canopy extends BlockProcessor {


    public Canopy() {
        jaro = new JaroWinkler();
    }

    private HashMap<String, HashSet> tokenlist;
    private JaroWinkler jaro;
    private HashSet remove;
    private ArrayList<Integer> nodelist;
    private HashSet temp;
    private ArrayList tokenset;
    private String token;
    private ArrayList recordlist;
    private AttributeSimilarity similarity;
    private Block block;


    public void Process(ArrayList records, String blockKey, double th1, double th2) {
        /*
         * initialize
         */
        remove = new HashSet();
        blocks = new HashMap();
        invertedList = new HashMap();
        similarity = new AttributeSimilarity();
        System.out.println("blocking method: \t canopy \t" + blockKey + "\t parameters: \t" + th1 + "\t" + th2);


        /*
         * tokenize records
         */
        Token tokenizer = new Token(records, blockKey);
        HashMap<String, ArrayList> tokenlist = new HashMap();
        tokenlist = tokenizer.getTokenInvertedList();



        /*
         * process records
         */
        for (int i = 0; i < records.size(); i++) {
            ArtistData artist = (ArtistData) records.get(i);
            String value = getValueByBlockKey(artist, blockKey);
            //int id = artist.getId();
            if (!remove.contains(i)) {
                // create new canopy with id as center
                nodelist = new ArrayList();
                nodelist.add(i);

                temp = new HashSet();
                tokenset = tokenizer.tokenizeRecord(artist);
                for (int index = 0; index < tokenset.size(); index++) {
                    token = (String) tokenset.get(index);
                    recordlist = tokenlist.get(token);
                    for (int j = 0; j < recordlist.size(); j++) {
                        int candidateId = (Integer) recordlist.get(j);
                        if (candidateId != i && !temp.contains(candidateId)) {
                            temp.add(candidateId);

                            if (blocks.containsKey(candidateId)) {
                                if (invertedList.containsKey(i)) {
                                    ArrayList blist = invertedList.get(i);
                                    if (blist.contains(candidateId)) {
                                        nodelist.add(candidateId);
                                    }
                                }
                            } else {
                                ArtistData candiateArtist = (ArtistData) records.get(candidateId);
                                String candidateValue = getValueByBlockKey(candiateArtist, blockKey);
                                double score = similarity.StrSim(value, candidateValue);
                                //double score = similarity.StrSim(value.split(" ")[value.split(" ").length-1], candidateValue.split(" ")[candidateValue.split(" ").length-1]);
                                if (score >= th1) {
                                    nodelist.add(candidateId);
                                    if (score >= th2) {
                                        remove.add(candidateId);
                                    }
                                }

                            }

                        }
                    }
                }
                // add to blocks and invertedList
                block = new Block(i, nodelist);
                blocks.put(i, block);
                updateHashMap(i);

            }

        }
        blockStatistics(blocks);
        invertedListStatistics(invertedList);
        System.out.println("----------- canopy blocking quality -------------");
        Evaluation evaluation = new Evaluation();
        evaluation.Evaluation(records, invertedList);
        evaluation.OverlapIntEvaluation(records, invertedList, blocks);
    }


    private void updateHashMap(int i) {
        // TODO Auto-generated method stub
        for (Integer node : nodelist) {
            if (!invertedList.containsKey(node)) {
                ArrayList blist = new ArrayList();
                blist.add(i);
                invertedList.put(node, blist);
            } else {
                ArrayList blist = invertedList.get(node);
                blist.add(i);
                invertedList.put(node, blist);
            }
        }
    }

    public ArrayList getRecords() {
        return records;
    }

    public void setRecords(ArrayList records) {
        this.records = records;
    }


    public String getBlockKey() {
        return blockKey;
    }

    public void setBlockKey(String blockKey) {
        this.blockKey = blockKey;
    }


}
