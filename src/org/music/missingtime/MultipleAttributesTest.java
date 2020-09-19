package org.music.missingtime;

import org.music.segmentation.baseline.ScaleProcessor;

import java.util.ArrayList;

public class MultipleAttributesTest {
	/**
	 * Discogs data
	 */
	// full data
	private static String SQL = "select * from music.series where partition_id > 0 order by cluster_id, partition_id, date limit 1000";

	private static String multiple = "select * from series where partition_id > 0 order by cluster_id, partition_id, date";

	private static String multiple1 = "select * from series where partition_id in (2883936, 35789577, 19614270, 5802602, 20940940"
			+ ", 37938757) order by cluster_id, partition_id, date";

	private static String simple1 = "select * from series where partition_id in (24980793) "
			+ "order by cluster_id, partition_id, date";

	private static String simple2 = "select * from series where partition_id in (24980793) "
			+ "order by cluster_id, partition_id, date";

	private static String single1 = "select * from series where partition_id in (8382842, 19361636, 12467687, 26578866, 12551479, "
			+ "32431645, 11658103, 10447172, 10447172, 25696741, 19577415) order by cluster_id, partition_id, date";

	private static String single2 = "select * from series where partition_id in (24980793, 8382842, "
			+ "19361636, 12467687, 26578866, 12551479, 32431645, 11658103, 10447172, 10447172, 25696741, 19577415) "
			+ "order by cluster_id, partition_id, date";

	private static String random1 = "select * from series order by cluster_id, date limit 10000";

	private static String random = "select * from series where partition_id != 0 order by cluster_id";

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
		String data11 = "24980793,";
		String data2 = "9347917,";
		String data4 = "37392920";

		// single trends:
		String data5 = "5025263, 26863565, 32431978, 4890393,"; //
		String data6 = "35399002, 28880045, 35808588, 31528587,";

		String data8 = "8382842,  19361636, 12467687, 32431645, 10447172, 10447172, 25696741, ";
		String data9 = "11493981, 15510312, 28991128, 16004902,";

		String data14 = "66051, 187104, 618377, 5034652";

		// multiple series
		String data3 = "2883936, 35789577, 5802602, 20940940, 37938757,";

		String data10 = "22499825, 14436114, 27310286,";
		String data12 = "5436403, 8302350, 2495038, 3101087,";

		String data7 = "26590540, 14857043, 11813126, 34768053, 31645200, 38008530";

		String sql = "select * from music.series where partition_id in ("
				// + data11 + data2 + data4
				// + data5 + data6 + data8 + data9 + data14
				+ data3 + data10 + data12 + data7

				+ ") order by cluster_id, partition_id, date";


		String sqlwithGroundTruth = "select * from series_with_ground_truth order by cluster_id, partition_id, date";

		bandAccuracyTest(sql);

	}

	private void baselineAccuracyTest() throws Exception {
		PartitionProcessor parProcessor = new PartitionProcessor(SQL, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		int totalSize = dataset.size();
		int partition = 10;
		int partitionSize = totalSize / partition;
		/**
		 * Repair w. Gap
		 */
		/*
		 * // parProcessor.scaleRepair(deltat, theta, //
		 * parProcessor.gapReleaseLists); // gapRepair(deltat, theta);
		 *
		 * // baseline 1 // ArrayList dataset = dataProcessor.getRelabels();
		 *
		 * // ScaleProcessor scaleProcessor = new //
		 * ScaleProcessor(parProcessor.finaltimelists, //
		 * parProcessor.finalreleaselists, // deltat); //
		 * scaleProcessor.process();
		 *
		 * System.out.println("MonoScale"); //
		 * System.err.println("series missing"); //
		 * parProcessor.scaleRepairMissing(deltat, theta); // repair errornous
		 * values System.out.println("series error"); //
		 * parProcessor.scaleRepairError(deltat, theta); // repair mixtural
		 * values System.out.println("series mixture"); //
		 * parProcessor.scaleRepairMissingError(deltat, theta);
		 */

		/**
		 * Process real-world data
		 */
		int deltat = 3;
		int theta = 4;

		parProcessor.BaselineDynamicProcessWithGroundTruth(deltat, theta);

	}

	private static void bandAccuracyTest(String query) throws Exception {
		PartitionProcessor parProcessor = new PartitionProcessor(query, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();

		System.out.println("dataset size: " + dataset.size());
		System.out.println();
		// System.out.println("gap:"); //
		// parProcessor.partitionWithGap("releaselabel", dataset);
		System.out.println();
		// System.out.println("block:");
		parProcessor.block("releaselabel", dataset);

		double deltat = 3;
		int theta = 4;

		for (deltat = 3; deltat <= 3; deltat++) {
			double start = System.currentTimeMillis();
			System.out.println("deltat = " + deltat + "; theta = " + theta);
			// System.out.println("LMB:");
			parProcessor.series(deltat, theta);
			// parProcessor.seriesWithMultipleAttributes(deltat, theta);

			double end = System.currentTimeMillis();

			System.out.println("runtime\t" + (end - start)); //
		}

		/*
		 * parProcessor.dynamicProcess(deltat, theta); //
		 * parProcessor.errorMissingProcess(deltat, theta); //
		 * parProcessor.missingErrorProcess(deltat, theta); //
		 * parProcessor.dynamicSeries(deltat, theta); //
		 * parProcessor.seriesDiscoveryWithMissingErroneousValues(deltat,
		 * theta);
		 */

		/*
		 * Process real-world data
		 */
		// parProcessor.DynamicProcessWithGroundTruth(deltat, theta);

		// System.out.println("LMS"); // parProcessor.DynamicProcess(0, theta);
		// parProcessor.errorMissingProcess(0, theta); //
		// parProcessor.missingErrorProcess(0, theta); //
		// parProcessor.DynamicProcessWithGroundTruth(0, theta);
	}

	private void performanceTest() throws Exception {
		PartitionProcessor parProcessor = new PartitionProcessor(SQL, "releaselabel");
		ArrayList dataset = parProcessor.getRelabels();
		int totalSize = dataset.size();
		int partition = 10;
		int partitionSize = totalSize / partition;

		/**
		 * Performance test, with \Delta t varying
		 */
		for (int d = 3; d <= 3; d++) {
			int deltat = d;
			int theta = 4;
			for (int i = 0; i < partition; i++) {
				int subSize = partitionSize * (i + 1);
				ArrayList subDataSet = new ArrayList();
				for (int j = 0; j < subSize; j++) {
					subDataSet.add(subDataSet.size(), dataset.get(j));
				}
				double start = System.currentTimeMillis();
				// System.out.println("gap:");
				// parProcessor.partitionWithGap("releaselabel", subDataSet);
				double endGAP = System.currentTimeMillis();
				double gapTime = endGAP - start;

				// System.out.println("Block:");
				parProcessor.block("releaselabel", subDataSet);
				// System.out.println("LMB:");
				double startLMB = System.currentTimeMillis();
				// parProcessor.series(deltat, theta);
				double endLMB = System.currentTimeMillis();
				double lmbTime = endLMB - startLMB;

				// System.out.println("LMS:");
				// parProcessor.series(0, theta);
				double endLMS = System.currentTimeMillis();
				double lmsTime = endLMS - endLMB;

				// System.out.println("MonoScale:");
				ScaleProcessor scaleProcessor = new ScaleProcessor(parProcessor.finaltimelists,
						parProcessor.finalreleaselists, deltat);
				scaleProcessor.process();
				double endScale = System.currentTimeMillis();
				double scaleTime = endScale - endLMS;

				// System.out.println(d + "\t" + lmbTime + "\t" + lmsTime);
				System.out.println("\t" + i + "\t" + gapTime + "\t" + lmbTime + "\t" + lmsTime + "\t");

				/**
				 * Repair w. Gap
				 */
				// parProcessor.DynamicProcessWithGroundTruth(deltat, theta);
				// parProcessor.BaselineDynamicProcessWithGroundTruth(deltat,
				// theta);
			}
		}
	}

}
