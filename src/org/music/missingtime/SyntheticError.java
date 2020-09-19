package org.music.missingtime;

import java.util.*;

public class SyntheticError extends SyntheticData {

	private double mean;
	private double deviation;
	private Random random = new Random();

	public SyntheticError(int range,
						  LinkedHashMap<Integer, Integer> timelist, double percent, int deltat, int theta)
			throws Exception {
		super(range, timelist, percent, deltat, theta);

		//System.out.println("start errorneous sample");
		mean = 2000;
		deviation = 50;
		if (!errorSequence.isEmpty()) {
			mean = getMean();
			//mean = getDistanceMean();
			deviation = getDeviation();
			//deviation = getDistanceDeviation();
		}
		// System.out.println("error mean \t deviation \t"+mean+"\t"+deviation);
	}

	private double getDistanceDeviation() {
		deviation = 0;
		Iterator it = distanceSequence.values().iterator();
		while (it.hasNext()) {
			int distance = (Integer) it.next();
			deviation += Math.pow((distance - mean), 2);
		}
		deviation = Math.sqrt(deviation / distanceSequence.size());
		return deviation;
	}

	private double getDistanceMean() {
		mean = 0;
		Iterator it = distanceSequence.values().iterator();
		while (it.hasNext()) {
			int distance = (Integer) it.next();
			mean += distance;
		}
		mean = mean / distanceSequence.size();
		return mean;
	}

	private double getGaussian() {
		// System.out.println("mean \t random \t deviation");
		double value = random.nextGaussian();
		// System.out.println(mean + "\t" + value + "\t" + Math.max(10,
		// deviation));
		// return mean + value * Math.max(10, deviation);
		// return value * deviation;
		return random.nextInt(50);
	}

	/**
	 * Sample without replacement on elements with correct values.
	 *
	 * @param sequence
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<Integer> sampleWithoutReplacement() {

		ArrayList<Integer> tempSequence = new ArrayList<Integer>();
		tempSequence.addAll(correctSequence);

		ArrayList<Integer> newSequence = new ArrayList<Integer>();
		repairs = new HashMap<Integer, Triple>();
		truths = new HashMap<Integer, Integer>();
		newSequence.addAll(sequence);

		int n = (int) (tempSequence.size() * percent);//Math.max(0, tempSequence.size() * percent);
		n = (int) Math.min(n, tempSequence.size() - 1);

		// System.out.println("# possible errors: \t" + n + "\t" +
		// tempSequence.size() + "\t" + percent);

		for (int i = 0; i < n; i++) {
			int index = random.nextInt(tempSequence.size());
			int key = tempSequence.get(index);
			int nearestKey = index - 1;
			nearestKey = Math.max(0, nearestKey);
			int nearestYear = sequence.get(nearestKey);
			// System.out.println("nearest key / year \t" + nearestKey + "\t" +
			// nearestYear);
			// int error = sequence.get(key) - (int) getGaussian();

			int gaussian = (int) getGaussian();
			// System.out.println("delatat/gaussian :" + deltat + "\t" +
			// gaussian);
			// int added = (0 + 4) * deltat;
			// System.out.println("\t added:" + added);
			int error = nearestYear + gaussian;

			if (Math.abs(error - sequence.get(key)) <= deltat) {
				// System.out.println("\t" + key + "\t" + sequence.get(key) +
				// "\t" + error);
				if (error > sequence.get(key))
					error += 6;
				else
					error -= 6;
			}

			newSequence.set(key, error);
			// System.out.println("\t" + key + "\t" + sequence.get(key) + "\t" +
			// error);

			Triple triple = new Triple();
			triple.setGround(sequence.get(key));
			triple.setError(error);
			repairs.put(key, triple);
			truths.put(key, sequence.get(key));

			tempSequence.remove(index);
		}

		return newSequence;

	}

	private double getDeviation() {
		deviation = 0;
		for (int i = 0; i < errorSequence.size(); i++) {
			int index = errorSequence.get(i);
			deviation += Math.pow(Double.valueOf(sequence.get(index) - mean), 2);
		}
		deviation = Math.sqrt(deviation / errorSequence.size());
		return deviation;
	}

	/**
	 * Compute average value of outliers.
	 *
	 * @return
	 */
	private double getMean() {
		mean = 0;
		for (int i = 0; i < errorSequence.size(); i++) {
			int index = errorSequence.get(i);
			mean += sequence.get(index);
		}
		mean = mean / Double.valueOf(errorSequence.size());

		return mean;
	}

}
