package org.music.decay;

import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;

public class Decay {

	public double th = 0.6;
	public AttributeSimilarity attrsim;

	public Decay() {

		attrsim = new AttributeSimilarity();
	}

	;

	public void CompareBetweenEntity(ArrayList entities, HashMap<Integer, ArrayList> invertedlist, ArrayList valuelist, ArrayList durationlist, HashMap<Integer, Integer> decaylist) {

	}

	public void updateDecay(int delta, HashMap<Integer, Integer> decaylist) {
		// TODO Auto-generated method stub
		if (!decaylist.containsKey(delta)) {
			int count = 1;
			decaylist.put(delta, count);
			//System.out.println("add to agr decay \t"+delta+"\t"+count);
		} else {
			int count = decaylist.get(delta);
			count += 1;
			decaylist.put(delta, count);
			//System.out.println("add to agr decay \t"+delta+"\t"+count);
		}
	}

}
