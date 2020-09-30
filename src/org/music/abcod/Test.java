package org.music.abcod;

import org.music.segmentation.baseline.ScaleProcessor;

import java.util.ArrayList;

public class Test {
	/**
	 * Discogs data
	 */

	private static String SQL =
			"select id, date, title as release_1, catno, label as label_name from music.music_release " +
					"where (date NOT IN (' ', ' ', ' ', ' ') AND date IS NOT NULL) " +
					"order by label, catno, date limit 1000000";

	public static void main(String[] args) throws Exception {

		/*
		 * partiton records by catalog values, s.t. each partition holds order
		 * dependency between catalog and year; the result is stored in 3
		 * arraylist: 1. catalists: contains ordered catalog values of each
		 * partition 2. timelists: contains ordered years of each partition 3.
		 * releaselists: contains releaseed sorted in the same order as catalog
		 * of each partition
		 */

		// simple series
		String data11 = "24980793";
		String data2 = "9347917,";
		String data4 = "37392920,";

		// single trends:
		String data5 = "5025263, 26863565, 32431978, 4890393,";
		String data6 = "35399002, 28880045, 35808588, 31528587,";

		String data8 = "8382842,  19361636, 12467687, 32431645, 10447172, 10447172, 25696741, ";
		String data9 = "11493981, 15510312, 28991128, 16004902,";

		String data14 = "66051, 187104, 618377, 5034652";

		// multiple series
		String data31 = "20940940,"; // 20940940 5802602
		String data3 = "2883936, 35789577, 5802602, 20940940, 37938757,";

		String data10 = "22499825, 14436114, 27310286,";
		String data12 = "5436403, 8302350, 2495038, 3101087,";

		String data7 = "26590540, 14857043, 11813126, 34768053, 31645200, 38008530";

		String sql = "select * from music_series where partition_id in (" + data31

				+ data11 + data2 + data4 + data5 + data6 + data8 + data9 + data14
				+ data3 + data10 + data12 + data7
				+ ") order by cluster_id, partition_id, catno";

		String sqlwithGroundTruth = "select * from series_with_ground_truth order by cluster_id, partition_id, date";

		/**
		 * Analyse series in sample data
		 */
		// analyzeSeries(sql);
		/**
		 * Analyse series in music full data
		 */
		// analyzeSeries(SQL);

		/***
		 * test scalability
		 *
		 */
		testScalability(SQL);

	}

	public static void testScalability(String sql) throws Exception {
		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		int totalSize = dataset.size();
		int partition = 1;
		int partitionSize = totalSize / partition;

		// System.out.println("\t \t Gap \t LMB \t LMS \t MonoScale");
		for (int d = 3; d <= 3; d++) {
			int deltat = d;
			int theta = 4;
			for (int i = 0; i < partition; i++) {
				int subSize = partitionSize * (i + 1);
				ArrayList subDataSet = new ArrayList();
				for (int j = 0; j < subSize; j++) {
					subDataSet.add(subDataSet.size(), dataset.get(j));
				}
				// baseline: gap
				double start = System.currentTimeMillis();
				// System.out.println("gap:");
				parProcessor.partitionWithGap("releaselabel", subDataSet);
				double endGAP = System.currentTimeMillis();
				double gapTime = endGAP - start;
				System.out.println("Gap Runtime: \t" + subSize + "\t" + gapTime);

				// blocking
				// System.out.println("Block:");
				parProcessor.block("releaselabel", subDataSet);

//				System.out.println("subDataSet size \t" + subDataSet.size());
				// System.out.println("LMB:");
				double startLMB = System.currentTimeMillis();
//				parProcessor.series(deltat, theta);
				double endLMB = System.currentTimeMillis();
				double lmbTime = endLMB - startLMB;

//				System.out.println("LMB Runtime: \t" + lmbTime);

				// System.out.println("LMS:");
				// parProcessor.series(0, theta);
				double endLMS = System.currentTimeMillis();
				double lmsTime = endLMS - endLMB;
				// System.out.println("LMS Runtime: \t" + lmsTime);

				// System.out.println("MonoScale:");
				// ScaleProcessor scaleProcessor = new
				// ScaleProcessor(parProcessor.finaltimelists,parProcessor.finalreleaselists,
				// deltat);
				// scaleProcessor.process();
				// double endScale = System.currentTimeMillis();
				// double scaleTime = endScale - endLMS;
			}
		}
	}

	public static void main1(String[] args) throws Exception {

		/*
		 * partiton records by catalog values, s.t. each partition holds order
		 * dependency between catalog and year; the result is stored in 3
		 * arraylist: 1. catalists: contains ordered catalog values of each
		 * partition 2. timelists: contains ordered years of each partition 3.
		 * releaselists: contains releaseed sorted in the same order as catalog
		 * of each partition
		 */

		// simple series
		String data11 = "24980793";
		String data2 = "9347917,";
		String data4 = "37392920";

		// single trends:
		String data5 = "5025263, 26863565, 32431978, 4890393,";
		String data6 = "35399002, 28880045, 35808588, 31528587,";

		String data8 = "8382842,  19361636, 12467687, 32431645, 10447172, 10447172, 25696741, ";
		String data9 = "11493981, 15510312, 28991128, 16004902,";

		String data14 = "66051, 187104, 618377, 5034652";

		// multiple series
		String data31 = "5802602, 20940940";
		// String data3 = "2883936, 35789577, 5802602, 20940940, 37938757,";

		String data10 = "22499825, 14436114, 27310286,";
		String data12 = "5436403, 8302350, 2495038, 3101087,";

		String data7 = "26590540, 14857043, 11813126, 34768053, 31645200, 38008530";

		String sql = "select * from music.series where partition_id in (" + data31

				// data11 + data2 + data4 + data5 + data6 + data8 + data9 + data14

				// + data3 + data10 + data12 + data7
				+ ") order by cluster_id, partition_id, date";

		String sqlwithGroundTruth = "select * from series_with_ground_truth order by cluster_id, partition_id, date";

		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		int totalSize = dataset.size();
		int partition = 1;
		int partitionSize = totalSize / partition;

		// System.out.println("\t \t Gap \t LMB \t LMS \t MonoScale");
		for (int d = 3; d <= 20; d++) {
			int deltat = d;
			int theta = 4;
			for (int i = 0; i < partition; i++) {
				int subSize = partitionSize * (i + 1);
				ArrayList subDataSet = new ArrayList();
				for (int j = 0; j < subSize; j++) {
					subDataSet.add(subDataSet.size(), dataset.get(j));
				}
				// baseline: gap
				double start = System.currentTimeMillis();
				// System.out.println("gap:");
				// parProcessor.partitionWithGap("releaselabel", subDataSet);
				double endGAP = System.currentTimeMillis();
				double gapTime = endGAP - start;

				// blocking
				// System.out.println("Block:");
				parProcessor.block("releaselabel", subDataSet);

				// System.out.println(subDataSet.size());
				// System.out.println("LMB:");
				double startLMB = System.currentTimeMillis();
				parProcessor.series(deltat, theta);
				double endLMB = System.currentTimeMillis();
				double lmbTime = endLMB - startLMB;

				//
				// System.out.println("LMB Runtime: \t" + lmbTime);

				// System.out.println("LMS:");
				// parProcessor.series(0, theta);
				double endLMS = System.currentTimeMillis();
				double lmsTime = endLMS - endLMB;

				// System.out.println("MonoScale:");
				ScaleProcessor scaleProcessor = new ScaleProcessor(parProcessor.finaltimelists,
						parProcessor.finalreleaselists, deltat);
				scaleProcessor.process();
				// double endScale = System.currentTimeMillis();
				// double scaleTime = endScale - endLMS;
			}
		}
	}

	// Analyse LMB-based series, i.e., to count the number of increasing series,
	// and # of decreasing series
	public static void analyzeSeries(String sql) throws Exception {
		int deltat = 3;
		int theta = 4;
		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();

		parProcessor.block("releaselabel", dataset);

		double startLMB = System.currentTimeMillis();
		parProcessor.series(deltat, theta);
		double endLMB = System.currentTimeMillis();
		double lmbTime = endLMB - startLMB;

		System.out.println("LMB Runtime: \t" + lmbTime);
	}

	public void repairReadData(String sql) throws Exception {
		int deltat = 3;
		int theta = 4;
		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();

		/**
		 * Repair w. Gap
		 */

		parProcessor.gapRepair(deltat, theta);

		// baseline 1
		ScaleProcessor scaleProcessor = new ScaleProcessor(parProcessor.finaltimelists, parProcessor.finalreleaselists,
				deltat);
		scaleProcessor.process();

		System.out.println("MonoScale"); //
		System.err.println("series missing"); //
		parProcessor.scaleRepairMissing(deltat, theta);
		// repair errornous values
		System.out.println("series error"); //
		parProcessor.scaleRepairError(deltat, theta);
		// repair mixtural values
		System.out.println("series mixture"); //
		parProcessor.scaleRepairMissingError(deltat, theta);


		/**
		 * Process real-world data
		 */

		parProcessor.BaselineDynamicProcessWithGroundTruth(deltat, theta);

	}

	public void processRealData(String sql) throws Exception {
		int deltat = 3;
		int theta = 4;
		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();

		parProcessor.DynamicProcessWithGroundTruth(deltat, theta);

		System.out.println("LMS");
		parProcessor.errorMissingProcess(0, theta);
		parProcessor.missingErrorProcess(0, theta);
		parProcessor.DynamicProcessWithGroundTruth(0, theta);

	}

	public void evaluateAccuracy(String sql) throws Exception {
		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		// Accuracy Test

		System.out.println("dataset size: " + dataset.size());
		System.out.println();
		parProcessor.partitionWithGap("releaselabel", dataset);
		System.out.println();
		System.out.println("block:");
		parProcessor.block("releaselabel", dataset);

		int deltat = 0;
		int theta = 4;
		double start = System.currentTimeMillis();
		System.out.println();
		System.out.println("LMB:");
		parProcessor.series(deltat, theta);
		double end = System.currentTimeMillis();
		System.out.println("runtime\t" + (end - start));
		parProcessor.errorMissingProcess(deltat, theta);
		parProcessor.missingErrorProcess(deltat, theta);
		parProcessor.seriesDiscoveryWithMissingErroneousValues(deltat, theta);

	}

	public void evaluateMultipleDelta(String sql) throws Exception {

		PartitionProcessor parProcessor = new PartitionProcessor(sql, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		int totalSize = dataset.size();
		int partition = 1;
		int partitionSize = totalSize / partition;

		for (int d = 0; d <= 20; d++) {
			int deltat = d;
			int theta = 4;
			for (int i = 0; i < partition; i++) {
				int subSize = partitionSize * (i + 1);
				ArrayList subDataSet = new ArrayList();
				for (int j = 0; j < subSize; j++) {
					subDataSet.add(subDataSet.size(), dataset.get(j));
				}
				parProcessor.block("releaselabel", subDataSet);

				double startLMB = System.currentTimeMillis();
				parProcessor.series(deltat, theta);
				double endLMB = System.currentTimeMillis();
				double lmbTime = endLMB - startLMB;

				System.out.println("LMB Runtime: \t" + lmbTime);

			}
		}
	}
}
