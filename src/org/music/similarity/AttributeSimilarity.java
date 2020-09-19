package org.music.similarity;

import com.wcohen.secondstring.Jaccard;

import java.util.ArrayList;

public class AttributeSimilarity {

	public AttributeSimilarity() {
	}

	;

	public double StrSim(String s1, String s2) {
		double score = 0;
		//JaroWinkler jaro = new JaroWinkler();
		//score = jaro.score(s1, s2);

		Jaccard jaccard = new Jaccard();
		score = jaccard.score(s1, s2);
		//if(s1==null||s2.==null)
		//score = 0;
		//System.out.println("str sim:"+s1+"\t"+s2+"\t"+score);
		return score;
	}

	public double SetSim(ArrayList<String> l1, ArrayList<String> l2) {
		double score = 0;
		double sum = 0;
		double th = 1;//
		int inter = 0;


		if (l1.size() < l2.size()) {
			for (String s1 : l1) {
				double maxscore = 0;
				for (String s2 : l2) {
					double localscore = StrSim(s1, s2);
					if (localscore > maxscore) {
						maxscore = localscore;
					}
				}
				if (maxscore >= th) {
					sum += maxscore;
					inter += 1;
				}
			}
		} else {
			for (String s2 : l2) {
				double maxscore = 0;
				for (String s1 : l1) {
					double localscore = StrSim(s1, s2);
					if (localscore > maxscore) {
						maxscore = localscore;
					}
				}
				if (maxscore >= th) {
					sum += maxscore;
					inter += 1;
				}
			}
		}
		if (l1.size() == 0 || l2.size() == 0)
			score = 0;
		else
			score = sum / Double.valueOf(l1.size() + l2.size() - inter);
		//score = sum/Math.min(l1.size(), l2.size());
		//System.out.println(l1+"\t vs \t"+l2+"\t :"+sum+"\t"+Math.min(l1.size(), l2.size())+"\t"+score);
		return score;
	}

}
