package org.music.block;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;

public class SlidingWindow extends AbstractBlock {

	public static Block block;
	public static ArrayList entities;
	public static ArrayList list;

	public SlidingWindow(ArrayList records, HashMap<Integer, Block> blocks,
						 HashMap<Integer, ArrayList> invertedList, String blockKey, double slidingSize) {
		// TODO Auto-generated constructor stub

		//int slidingSize = 7;

		System.out.println("blocking method: \t slidingwindow \t" + blockKey + "\t" + slidingSize);

		for (int i = 0; i < records.size() - slidingSize + 1; i++) {
			ArtistData artist = (ArtistData) records.get(i);
			entities = new ArrayList();
			entities.add(i);
			updateInvertedList(i, i, invertedList);
			for (int j = i + 1; j < i + slidingSize; j++) {
				entities.add(j);
				updateInvertedList(j, i, invertedList);
			}
			block = new Block(i, entities);
			blocks.put(i, block);

		}
		blockStatistics(blocks);
		invertedListStatistics(invertedList);
		System.out.println("----------- sliding window blocking quality -------------");
		Evaluation evaluation = new Evaluation();
		evaluation.Evaluation(records, invertedList);
		evaluation.OverlapIntEvaluation(records, invertedList, blocks);

	}

	private void updateInvertedList(int id, int block_id,
									HashMap<Integer, ArrayList> invertedList) {
		// TODO Auto-generated method stub
		if (!invertedList.containsKey(id)) {
			list = new ArrayList();
			list.add(block_id);
			invertedList.put(id, list);
		} else {
			list = invertedList.get(id);
			list.add(block_id);
			invertedList.put(id, list);
		}
	}

}
