package org.music.block;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;

public class Baseline extends AbstractBlock {

	public Baseline(ArrayList records) {

		this.records = records;
		blocks = new HashMap();
		this.invertedList = new HashMap();
		this.evaluation = new Evaluation();

	}

	public void process() {

		ArrayList entities = new ArrayList();
		int blockid = 0;

		ArrayList blocklist = new ArrayList();
		blocklist.add(blockid);

		for (int i = 0; i < records.size(); i++) {
			entities.add(i);
			invertedList.put(i, blocklist);
		}

		Block block = new Block();
		block.setBlock_id(blockid);
		block.setEntities(entities);
		blocks.put(blockid, block);

		System.out.println("----------- baseline blocking quality -------------");

		evaluation.Evaluation(records, invertedList);
		evaluation.OverlapIntEvaluation(records, invertedList, blocks);

	}

}
