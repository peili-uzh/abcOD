package org.music.block;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BigramIndexing extends AbstractBlock {

    protected static double th = 0.8;
    protected static ArrayList records;
    private static HashMap<String, Block> tempblocks;
    protected static HashMap<Integer, Block> blocks;
    protected static HashMap<Integer, ArrayList> invertedList;
    protected static String blockKey;
    private static ArrayList<String> templist;
    private static int seq = 0;

    public BigramIndexing() {
    }

    ;

    public BigramIndexing(ArrayList records, String blockKey) {
        //this.th = th;
        this.records = records;
        this.blockKey = blockKey;
    }

    public BigramIndexing(ArrayList records, String blockKey, double th) {
        this.th = th;
        this.records = records;
        this.blockKey = blockKey;
    }

    public void process() {

        /*
         * initialize
         */
        Token tokenizer = new Token();
        tempblocks = new HashMap();
        invertedList = new HashMap();
        blocks = new HashMap();

        System.out.println("blocking method: \t bigramindexing \t" + blockKey + "\t" + th);

        for (int i = 0; i < records.size(); i++) {
            ArtistData artist = (ArtistData) records.get(i);
            String value = getValueByBlockKey(artist, blockKey);//.replace("john ", "");
            //String value0 = value.split(" ")[0];
            String value0 = value.split(" ")[value.split(" ").length - 1];//value.replace("john ", "");//
            //System.out.println(i+"\t |"+value+"| \t |"+value0+"|");
            value = value0;
            templist = new ArrayList();
            if (value.length() > 1) {
                String[] bigrams = tokenizer.getBigram(value);
                //System.out.println(i+"\t"+value);
                //System.out.println(" \t"+bigrams.size()+"\t"+bigrams);

                int length = (int) Math.round(bigrams.length * th);
                if (length == 0)
                    length = 1;
                //System.out.println("\t"+bigrams.length+"\t"+length);
				
				/*
				System.out.println("\t string length: "+bigrams.length);
				for(int j=0; j<bigrams.length;j++){
					System.out.println("\t\t"+bigrams[j]);
				}*/

                //ArrayList<String> keylist
                templist = getBigramCombinations(bigrams, length);
            } else if (!value.equals("")) {
                //templist = new ArrayList<String>();
                templist.add(value);
            }
            if (!templist.isEmpty() || templist != null) {
                //update blocks
                updateBlocks(i);
                //System.out.println("# blocks \t"+blocks.size());

                //update inverted index
                //invertedList.put(i, templist);
            }
        }

        convertBlock();

        blockStatistics(blocks);
        invertedListStatistics(invertedList);
        System.out.println("----------- bigram blocking quality -------------");
        Evaluation evaluation = new Evaluation();
        evaluation.Evaluation(records, invertedList);
        evaluation.OverlapIntEvaluation(records, invertedList, blocks);
    }

	/*
	private static void updateInvertedList(int i) {
		// TODO Auto-generated method stub
		for(String key:templist){
			
		}
	}*/

    private static void convertBlock() {
        // TODO Auto-generated method stub
        for (Map.Entry<String, Block> e : tempblocks.entrySet()) {
            Block block = e.getValue();
            int bid = blocks.size();
            blocks.put(bid, block);
            ArrayList entities = block.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                int id = (Integer) entities.get(i);
                updateInvertedList(id, bid);
            }

        }
    }

    private static void updateInvertedList(int id, int bid) {
        // TODO Auto-generated method stub
        if (!invertedList.containsKey(id)) {
            ArrayList list = new ArrayList();
            list.add(bid);
            invertedList.put(id, list);
        } else {
            ArrayList list = invertedList.get(id);
            list.add(bid);
            invertedList.put(id, list);
        }
    }

    private static void updateBlocks(int i) {
        // TODO Auto-generated method stub
        for (String key : templist) {
            if (!tempblocks.containsKey(key)) {
                int index = tempblocks.size();
                ArrayList nodelist = new ArrayList();
                nodelist.add(i);
                Block block = new Block(index, nodelist);
                tempblocks.put(key, block);
            } else {
                Block block = tempblocks.get(key);
                ArrayList entities = block.getEntities();
                entities.add(i);
                block.setEntities(entities);
                tempblocks.put(key, block);
            }
        }
    }

    private static ArrayList<String> getBigramCombinations(String[] bigrams, int length) {
        templist = new ArrayList();
        // TODO Auto-generated method stub
        //ArrayList<String> keylist = new ArrayList();
        String[] data = new String[length];
        int n = bigrams.length;

        combinationUtil(bigrams, data, 0, n - 1, 0, length);
        //System.out.println("# combinations \t"+templist.size());
		
		/*
		for(int i=0; i<templist.size();i++){
			String[] temp = templist.get(i);
			String key = Arrays.toString(temp);
			System.out.println("\t"+key);
			keylist.add(key);
		}
		*/

        return templist;
    }

    private static void combinationUtil(String[] bigrams, String[] data, int start,
                                        int end, int index, int length) {
        /*
         *  bigrams --> input array
         *  data --> temporay arry to store current combination
         *  start & end --> staring and ending indexes in bigrams
         *  length --> size of each combination
         */
        // TODO Auto-generated method stub
        if (index == length) {
			/*
			for(int j= 0; j< length; j++){
				String temp = data[j];
				System.out.println("current combination \t"+j+"\t"+temp);
			}*/
            String currentkey = Arrays.toString(data);
            //System.out.println("\t"+currentkey);
            templist.add(templist.size(), currentkey);
			/*
			for(int j=0; j<templist.size();j++){
				String temp = templist.get(j);
				//String key = Arrays.toString(temp);
				System.out.println("\t"+j+"\t"+temp);
			}*/
            return;
        } else {
            for (int i = start; i <= end && end - i + 1 >= length - index; i++) {
                data[index] = bigrams[i];
                //System.out.println(i+"\t"+index+"\t"+bigrams[i]);
                combinationUtil(bigrams, data, i + 1, end, index + 1, length);
            }
        }

        /*
         * replace index w. all possible elements.
         */

    }

    public static double getTh() {
        return th;
    }

    public static void setTh(double th) {
        BigramIndexing.th = th;
    }


    public HashMap<Integer, ArrayList> getInvertedList() {
        return invertedList;
    }

    public void setInvertedList(HashMap<Integer, ArrayList> invertedList) {
        this.invertedList = invertedList;
    }

    public static String getBlockKey() {
        return blockKey;
    }

    public static void setBlockKey(String blockKey) {
        BigramIndexing.blockKey = blockKey;
    }

    public HashMap<Integer, Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(HashMap<Integer, Block> blocks) {
        this.blocks = blocks;
    }


}
