package org.music.decay;

import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisagreementDecay extends Decay {

	public HashMap<Integer, Integer> fullist;
	public HashMap<Integer, Integer> partialist;

	public DisagreementDecay() {
		attrsim = new AttributeSimilarity();
	}

	public void CompareBetweenEntity(ArrayList entities, HashMap<Integer, ArrayList> invertedlist, ArrayList valuelist, ArrayList durationlist, HashMap<Integer, Integer> decaylist) {

		fullist = new HashMap<Integer, Integer>();
		partialist = new HashMap<Integer, Integer>();

		for (int i = 0; i < entities.size(); i++) {
			int cluster_id = (Integer) entities.get(i);
			ArrayList list = invertedlist.get(cluster_id);
			//System.out.println(i+"\t"+cluster_id+"\t"+list.size());
			for (int j = 0; j < list.size(); j++) {
				int id1 = (Integer) list.get(j);
				ArrayList duration1 = (ArrayList) durationlist.get(id1);
				String value1 = (String) valuelist.get(id1);
				//System.out.println("\t"+value1+"\t from \t"+duration1.get(0)+"\t"+duration1.get(1));
				if (j < list.size() - 1) {
					int id2 = (Integer) list.get(j + 1);
					ArrayList duration2 = (ArrayList) durationlist.get(id2);
					int delta = Integer.valueOf((String) duration2.get(0)) - Integer.valueOf((String) duration1.get(0));
					//System.out.println("\t delta t=\t"+delta);
					updateDecay(delta, fullist);
				} else {
					int delta = Integer.valueOf((String) duration1.get(1)) - Integer.valueOf((String) duration1.get(0)) + 1;
					//System.out.println("\t delta t=\t"+delta);
					updateDecay(delta, partialist);
				}
			}
		}

		// compute decay
		for (int c = 0; c < 20; c++) {
			double agr = computeDis(c);
			System.out.println(c + "\t" + agr);
		}

	}

	public double computeDis(int c) {
		// TODO Auto-generated method stub
		double decay = 0;
		int full = 0;
		int fulltotal = 0;
		int partial = 0;

		for (Map.Entry<Integer, Integer> e : fullist.entrySet()) {
			int t = e.getKey();
			int s = e.getValue();
			if (t <= c) {
				full += s;
			}
			fulltotal += s;
		}
		for (Map.Entry<Integer, Integer> e : partialist.entrySet()) {
			int t = e.getKey();
			int s = e.getValue();
			if (t >= c) {
				partial += s;
			}
		}

		decay = Double.valueOf(full) / Double.valueOf(fulltotal + partial);
		return decay;
	}

}
