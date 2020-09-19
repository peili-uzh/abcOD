package org.music.decay;

import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AgreementDecay extends Decay {

	public HashMap<Integer, Integer> decaylist;

	public AgreementDecay() {
		attrsim = new AttributeSimilarity();
	}

	;

	public void CompareBetweenEntity(ArrayList entities, HashMap<Integer, ArrayList> invertedlist, ArrayList valuelist, ArrayList durationlist, HashMap<Integer, Integer> decaylist) {

		//System.out.println("entity size \t"+entities.size());
		for (int j = 0; j < entities.size(); j++) {
			int cluster1 = (Integer) entities.get(j);
			ArrayList list1 = invertedlist.get(cluster1);
			//System.out.println(j+"\t"+cluster1+"\t"+list1.size());
			for (int m = j + 1; m < entities.size(); m++) {
				int cluster2 = (Integer) entities.get(m);
				ArrayList list2 = invertedlist.get(cluster2);
				//System.out.println("\t compare with \t"+m+"\t"+cluster2+"\t"+list2.size());

				compareValuesForAgreement(list1, list2, valuelist, durationlist, decaylist);

			}
		}

		this.decaylist = decaylist;

		// compute decay
		for (int c = 0; c < 20; c++) {
			double agr = computeAgr(c);
			System.out.println(c + "\t" + agr);
		}

	}

	public double computeAgr(int c) {
		// TODO Auto-generated method stub
		double agr = 0;
		int count = 0;
		int totalcount = 0;
		for (Map.Entry<Integer, Integer> e : decaylist.entrySet()) {
			int t = e.getKey();
			int s = e.getValue();
			totalcount += s;
			if (t <= c) {
				count += s;
			}
		}

		agr = Double.valueOf(count) / Double.valueOf(totalcount);

		//if(agr==1)
		//agr = 0.99;

		return agr;
	}

	private void compareValuesForAgreement(ArrayList list1, ArrayList list2, ArrayList valuelist, ArrayList durationlist, HashMap<Integer, Integer> decaylist) {
		// TODO Auto-generated method stub
		boolean match = false;
		for (int i = 0; i < list1.size(); i++) {
			int id1 = (Integer) list1.get(i);
			String str1 = (String) valuelist.get(id1);
			ArrayList duration1 = (ArrayList) durationlist.get(id1);
			for (int j = 0; j < list2.size(); j++) {
				int id2 = (Integer) list2.get(j);
				String str2 = (String) valuelist.get(id2);
				ArrayList duration2 = (ArrayList) durationlist.get(id2);
				double score = attrsim.StrSim(str1, str2);
				//System.out.println("\t\t"+str1+"\t"+str2+"\t"+score);
				if (score >= th) {
					match = true;
					int delta = getDelta(duration1, duration2);
					//System.out.println("\t\t"+str1+"\t"+str2+"\t"+score+"\t"+delta);
					updateDecay(delta, decaylist);
				}

			}
		}
		if (!match) {
			int delta = 10000;
			updateDecay(delta, decaylist);

		}
	}


	private int getDelta(ArrayList duration1, ArrayList duration2) {
		// TODO Auto-generated method stub
		int delta = 0;
		String d10 = (String) duration1.get(0);
		String d11 = (String) duration1.get(1);
		//System.out.println(d10+"\t"+d11);

		String d20 = (String) duration2.get(0);
		String d21 = (String) duration2.get(1);
		//System.out.println(d20+"\t"+d21);

		if (Integer.valueOf(d10) < Integer.valueOf(d20)) {
			delta = Math.max(Integer.valueOf(d20) - Integer.valueOf(d11) + 1, 0);
		} else {
			delta = Math.max(Integer.valueOf(d10) - Integer.valueOf(d21) + 1, 0);
		}
		return delta;
	}

}
