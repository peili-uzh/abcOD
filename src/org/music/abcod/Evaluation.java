package org.music.abcod;

import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Evaluation {

	public Evaluation() {
	}

	;

	public void evaluationPartition(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> releaselists) {

		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;
		// System.out.println(releaselists.size());

		for (int i = 0; i < releaselists.size(); i++) { // for each partition
			LinkedHashMap<Integer, ReleaseLabel> list1 = releaselists.get(i);
			// if (list1.get(0).getId() == 33166737) {
			// System.out.println("\t series size \t" + list1.size());
			for (int j = 0; j < list1.size(); j++) { // for each record in a
				// partition
				ReleaseLabel r1 = list1.get(j);
				int gid1 = r1.getPartition_id();
				int pid1 = i;
				int rid1 = r1.getId();
				// System.out.println("\t\t" + j + "\t" + rid1 + "\t" + pid1 +
				// "\t" + gid1 + "\t" + r1.getCatno() + "\t"
				// + r1.getDate());
				for (int n = 0; n < releaselists.size(); n++) { // loop for
					// each
					// partition
					LinkedHashMap<Integer, ReleaseLabel> list2 = releaselists.get(n);

					for (int m = 0; m < list2.size(); m++) {
						ReleaseLabel r2 = list2.get(m);
						int gid2 = r2.getPartition_id();
						int pid2 = n;
						int rid2 = r2.getId();
						// System.out.println(rid1 + "\t" + rid2 + "\t" + gid1 +
						// "\t" + gid2 + "\t" + pid1 + "\t" + pid2);

						if (rid1 != rid2 && gid1 != 0 && gid2 != 0) {
							// true positive & false negative
							if (gid1 == gid2) {
								if (pid1 == pid2)
									tp++;
								else
									fn++;
							} else if (pid1 == pid2)
								fp++;
						}
					}
				}
			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		// System.out.println("F1 \t Precision \t recall \t FP \t FN \t TP");
		// System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp
		// + "\t" + fn + "\t" + tp);

		System.out.println(f1 + "\t" + precision + "\t" + recall);
	}

	public void analyseRepairs(HashMap<Integer, Triple> repairs, int deltat) {
		// System.out.println("\t id \t truth \t lower \t upper \t timegap");
		HashMap<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> fineHistogram = new HashMap<Integer, Integer>();

		for (Map.Entry<Integer, Triple> e : repairs.entrySet()) {
			Triple triple = e.getValue();

			if (triple.getGround() > 0) {

				/**
				 * only used for LMB
				 */
				if (triple.getStart() == triple.getEnd()) {
					triple.setStart(triple.getStart() - deltat);
					triple.setEnd(triple.getEnd() + deltat);
				}

				int timeGap = Math.abs(triple.getEnd() - triple.getStart()); // //
				// triple.getEnd()
				// -
				// triple.getStart();

				System.out.println("\t" + e.getKey() + "\t" + triple.getGround() + "\t" + triple.getStart() + "\t"
						+ triple.getEnd() + "\t" + timeGap);

				if (timeGap <= deltat) {
					updateHistogram(histogram, deltat);
				} else {
					if (timeGap <= 2 * deltat) {
						updateHistogram(histogram, 2 * deltat);
					} else {
						if (timeGap <= 3 * deltat) {
							updateHistogram(histogram, 3 * deltat);
						} else {
							if (timeGap <= 4 * deltat) {
								updateHistogram(histogram, 4 * deltat);
							} else {
								if (timeGap <= 5 * deltat) {
									updateHistogram(histogram, 5 * deltat);
								} else {
									updateHistogram(histogram, 10000000);
								}
							}
						}
					}
				}

				updateHistogram(fineHistogram, timeGap);
			}
		}

		// System.out.println("histogram");
		// System.out.println("gap \t count");
		for (Map.Entry<Integer, Integer> e : histogram.entrySet()) {
			// System.out.println(e.getKey() + "\t" + e.getValue());
		}

		System.out.println("fine histogram");
		System.out.println("gap \t count");

		int totalCount = 0;
		for (Map.Entry<Integer, Integer> e : fineHistogram.entrySet()) {
			// System.out.println(e.getKey() + "\t" + e.getValue());
			totalCount += e.getValue();
		}

		int count = 0;
		for (int i = 0; i <= 30; i++) {
			int value = 0;
			if (fineHistogram.containsKey(i))
				value = fineHistogram.get(i);
			// System.out.println(i + "\t" + value);
			System.out.println(value);
			count += value;
		}
		// System.out.println("others \t" + (totalCount - count));
		System.out.println((totalCount - count));
	}

	private void updateHistogram(HashMap<Integer, Integer> histogram, int key) {
		int count = 0;
		if (histogram.containsKey(key)) {
			count = histogram.get(key);
		}

		histogram.put(key, count + 1);
	}

	public ArrayList<Integer> evaluateRepairs(HashMap<Integer, Triple> repairs) {
		// System.out.println("evalute repair");
		int correct = 0;
		int validCount = 0;
		// System.out.println("# of sampled values: \t" + repairs.size());
		// System.out.println("\t id \t truth \t lower \t upper");
		for (Map.Entry<Integer, Triple> e : repairs.entrySet()) {
			Triple triple = e.getValue();

			if (triple.getGround() > 0) {
				/**
				 * only used for LMB
				 */
				// if (triple.getStart() == triple.getEnd()) {
				// triple.setStart(triple.getStart() - deltat);
				// triple.setEnd(triple.getEnd() + deltat);
				// }

				// System.out.println("\t" + e.getKey() + "\t" +
				// triple.getGround() + "\t" + triple.getStart() + "\t"
				// + triple.getEnd());
				if (triple.getStart() <= triple.getEnd()) {
					if (triple.getGround() <= triple.getEnd() && triple.getGround() >= triple.getStart())
						correct++;
					// else
					// System.out.println("");//
				} else {
					if (triple.getGround() <= triple.getEnd() || triple.getGround() >= triple.getStart())
						correct++;
					// else
					// System.out.println("\t" + e.getKey() + "\t" +
					// triple.getGround() + "\t" + triple.getStart() + "\t"
					// + triple.getEnd());// System.out.println("");//
				}

				validCount++;
			}
		}

		ArrayList<Integer> precision = new ArrayList<Integer>();
		precision.add(0, correct);
		precision.add(1, validCount);
		// System.out.println("\t" + correct + "\t" + repairs.size() + "\t"
		// + (Double.valueOf(correct) / Double.valueOf(repairs.size())));
		// Double.valueOf(correct) / Double.valueOf(repairs.size());

		return precision;
	}
}
