package org.music.evaluation;

import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;
import org.music.similarity.RecordSimilarity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Evaluation {

	protected static ArrayList records;
	protected static HashMap<Integer, ArrayList> invertedList;
	protected static HashMap<Integer, String> disjointList;
	protected static String blockKey;
	protected static HashMap<String, Block> strblocks;
	protected static HashMap<Integer, Block> intblocks;
	protected static int blockDuplicate;// # duplicate pairs compared in at least one block
	protected static int inputDuplicate;//# duplicate pairs in the input
	protected static int aggreCardinality; // # comparisons all blocks contains
	protected static double pairCompleteness;
	protected static double pairQuality;
	protected static RecordSimilarity sim;
	private static double para = 0.0;


	public static double getPairCompleteness() {
		return pairCompleteness;
	}


	public static void setPairCompleteness() {
		double pairCompleteness = Double.valueOf(blockDuplicate) / Double.valueOf(inputDuplicate);
		//System.out.println("pair completeness \t"+pairCompleteness);
		Evaluation.pairCompleteness = pairCompleteness;
	}


	public static double getPairQuality() {
		return pairQuality;
	}


	public static void setPairQuality() {
		double pairQuality = Double.valueOf(blockDuplicate) / Double.valueOf(aggreCardinality);
		//System.out.println("pair quality: \t"+pairQuality);
		Evaluation.pairQuality = pairQuality;
	}


	public static void setBlockDuplicate(int blockDuplicate) {
		Evaluation.blockDuplicate = blockDuplicate;
	}


	public static void setInputDuplicate(int inputDuplicate) {
		Evaluation.inputDuplicate = inputDuplicate;
	}


	public static void setAggreCardinality(int aggreCardinality) {
		Evaluation.aggreCardinality = aggreCardinality;
	}


	public static int getBlockDuplicate() {
		return blockDuplicate;
	}


	public static HashMap<String, Block> getStrblocks() {
		return strblocks;
	}


	public static void setStrblocks(HashMap<String, Block> strblocks) {
		Evaluation.strblocks = strblocks;
	}


	public static HashMap<Integer, Block> getIntblocks() {
		return intblocks;
	}


	public static void setIntblocks(HashMap<Integer, Block> intblocks) {
		Evaluation.intblocks = intblocks;
	}


	public static void setBlockDuplicate(ArrayList records, HashMap<Integer, ArrayList> invertedList) {
		int blockDuplicate = 0;
		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			int cluster_id1 = artist1.getCluster_id();
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				int cluster_id2 = artist2.getCluster_id();
				if (cluster_id1 == cluster_id2) {
					ArrayList list1 = invertedList.get(i);
					ArrayList list2 = invertedList.get(j);
					boolean overlap = checkOverlap(list1, list2);
					if (overlap)
						blockDuplicate += 1;
				}

			}
		}
		//System.out.println("# duplicate pairs in blocks \t"+blockDuplicate);
		Evaluation.blockDuplicate = blockDuplicate;
	}


	public static int getInputDuplicate() {
		return inputDuplicate;
	}


	public static void setInputDuplicate(ArrayList records) {
		int inputDuplicate = 0;
		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			int cluster_id1 = artist1.getCluster_id();
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				int cluster_id2 = artist2.getCluster_id();
				if (cluster_id1 == cluster_id2)
					inputDuplicate += 1;
			}
		}

		//System.out.println("# duplicate pairs in input \t"+inputDuplicate);
		Evaluation.inputDuplicate = inputDuplicate;
	}


	public static int getAggreCardinality() {
		return aggreCardinality;
	}


	public static void setAggreCardinality(HashMap<String, Block> blocks) {
		int aggreCardinality = 0;
		for (Map.Entry<String, Block> e : blocks.entrySet()) {
			String key = e.getKey();
			Block block = e.getValue();
			ArrayList nodelist = block.getEntities();
			HashSet set = new HashSet(nodelist);
			aggreCardinality += set.size() * (set.size() - 1) / 2;
			//if(set.size()>1)
			//System.out.println(key+"\t"+nodelist+"\t"+set.size()+"\t"+blockDuplicate);

		}
		//System.out.println("# of totoal comparisons: \t"+aggreCardinality);
		Evaluation.aggreCardinality = aggreCardinality;
	}


	public static String getBlockKey() {
		return blockKey;
	}


	public static void setBlockKey(String blockKey) {
		Evaluation.blockKey = blockKey;
	}


	public static ArrayList getRecords() {
		return records;
	}


	public static void setRecords(ArrayList records) {
		Evaluation.records = records;
	}


	public static HashMap<Integer, ArrayList> getInvertedList() {
		return invertedList;
	}


	public static void setInvertedList(HashMap<Integer, ArrayList> invertedList) {
		Evaluation.invertedList = invertedList;
	}


	public static HashMap<Integer, String> getDisjointList() {
		return disjointList;
	}


	public static void setDisjointList(HashMap<Integer, String> disjointList) {
		Evaluation.disjointList = disjointList;
	}


	public Evaluation() {

	}


	public static void getGroundTruth(HashMap<Integer, Integer> truth) throws Exception {


		String sql = "select mid, gid from artist_link";

		Connection con = ConnectionPool.getConnection();
		if (!con.isClosed()) {

			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				int mid = result.getInt(1);
				int gid = result.getInt(2);

				truth.put(mid, gid);
			}

			st.close();


			ConnectionPool.putConnection(con);

		}
	}


	/*
	 * evaluate overlapping blocks
	 */
	public static void OverlapEvaluation(ArrayList records, HashMap<Integer, ArrayList> invertedList, HashMap<String, Block> blocks) {

		Evaluation.setAggreCardinality(blocks);
		Evaluation.setBlockDuplicate(records, invertedList);
		Evaluation.setInputDuplicate(records);
		Evaluation.setPairCompleteness();
		Evaluation.setPairQuality();

		System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		System.out.println(pairCompleteness + "\t" + pairQuality + "\t" + blockDuplicate + "\t" + inputDuplicate + "\t" + aggreCardinality);


	}

	/*
	 * evaluate non-overlapping blocks
	 */

	public static void Evaluation(ArrayList records, HashMap<Integer, ArrayList> invertedList) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			//int id1 = artist1.getId();
			int cluster_id1 = artist1.getCluster_id();
			ArrayList block1 = new ArrayList();
			if (invertedList.containsKey(i))
				block1 = invertedList.get(i);

			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				//int id2 = artist2.getId();
				int cluster_id2 = artist2.getCluster_id();
				ArrayList block2 = new ArrayList();
				if (invertedList.containsKey(j))
					block2 = invertedList.get(j);

				boolean overlap = checkOverlap(block1, block2);
				double score = sim.recordSimilarity(artist1, artist2);


				// true positive & false negative
				if (cluster_id1 == cluster_id2) {
					if (overlap && score >= para) {
						tp++;
					} else {
						fn += 1;
						//System.out.println("false negative \t"+artist1.getId()+"\t"+i+"\t"+artist2.getId()+"\t"+j);
						//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
					}

				} else if (overlap && score >= para) {
					fp++;
					//double score = AttributeSimilarity.StrSim(artist1.getName(), artist2.getName());
					//System.out.println("false positive \t"+artist1.getId()+"\t"+i+"\t"+artist2.getId()+"\t"+j+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name()+"\t"+score);
					//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
				}
			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);
	}


	private static boolean checkOverlap(ArrayList block1, ArrayList block2) {
		// TODO Auto-generated method stub
		boolean overlap = false;
		ArrayList blocktemp = new ArrayList();
		if (block1 != null) {
			//System.out.println("empty block1");
			//System.out.println("+++ block1 size \t"+block1.size());
			blocktemp.addAll(block1);
			blocktemp.retainAll(block2);

			if (!blocktemp.isEmpty())
				overlap = true;
		}
		/*
		if(overlap){
			System.out.println("\t"+"\t"+block1+"\t"+blocktemp);
			System.out.println("\t"+"\t"+block2);
			System.out.println(overlap);
		}*/

		return overlap;
	}


	public Evaluation(ArrayList records, HashMap<Integer, String> invertedList) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			//int id1 = artist1.getId();
			int cluster_id1 = artist1.getCluster_id();
			String block1 = "";
			if (invertedList.containsKey(i))
				block1 = invertedList.get(i);
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				//int id2 = artist2.getId();
				int cluster_id2 = artist2.getCluster_id();
				String block2 = "";
				if (invertedList.containsKey(j))
					block2 = invertedList.get(j);
				double score = sim.recordSimilarity(artist1, artist2);
				// true positive & false negative
				if (cluster_id1 == cluster_id2) {
					if (block1.equals(block2) && !block1.equals("") && score >= para) {
						tp++;
						//System.out.println(i+"\t"+j);
					} else {
						fn += 1;
						//System.out.println(i+"\t"+j);
						//System.out.println("false negative \t"+id1+"\t"+id2+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name());
						//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
					}

				} else if (block1.equals(block2) && !block1.equals("") && score >= para) {
					fp++;
					//System.out.println(i+"\t"+j);
					//System.out.println("false positive \t"+id1+"\t"+id2+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name());
					//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
				}
			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);

		double blockDuplicate = tp;
		double inputDuplicate = tp + fn;
		double aggreCardinality = tp + fp;
		double pairCompleteness = blockDuplicate / inputDuplicate;
		double pairQuality = blockDuplicate / aggreCardinality;

		System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		System.out.println(pairCompleteness + "\t" + pairQuality + "\t" + blockDuplicate + "\t" + inputDuplicate + "\t" + aggreCardinality);
	}

	public void ClusterComparision(HashMap<Integer, Integer> clusterlist1, HashMap<Integer, Integer> clusterlist2) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		// use clusterlist1 as ground truth
		for (Map.Entry<Integer, Integer> e1 : clusterlist1.entrySet()) {
			int id1 = e1.getKey();
			int cluster_id1 = e1.getValue();
			int cluster1 = -1;
			if (clusterlist2.containsKey(id1))
				cluster1 = clusterlist2.get(id1);
			//System.out.println("evaluate \t"+id1+"\t"+cluster_id1+"\t"+cluster1);
			for (Map.Entry<Integer, Integer> e2 : clusterlist1.entrySet()) {
				int id2 = e2.getKey();
				int cluster_id2 = e2.getValue();
				int cluster2 = -1;
				if (clusterlist2.containsKey(id2))
					cluster2 = clusterlist2.get(id2);

				if (id2 > id1) {


					// true positive & false negative
					if (cluster_id1 == cluster_id2) {
						if (cluster1 == cluster2 && cluster1 != -1) {
							tp++;
						} else {
							fn += 1;
							//System.out.println("false negative \t"+id1+"\t"+id2+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name());
							//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
						}

					} else if (cluster1 == cluster2 && cluster1 != -1) {
						fp++;
						//System.out.println("false positive \t"+artist1.getId()+"\t"+artist1.getName()+" vs "+artist2.getId()+"\t"+artist2.getName());
						//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
					}
				}

			}

		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);

	}

	public static void EntityEvaluation(String entityType, ArrayList records, HashMap<Integer, Integer> clusterinvertedList) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < records.size(); i++) {

			int cluster_id1 = -1;
			int cluster1 = -1;

			if (entityType.equalsIgnoreCase("label")) {
				LabelData label1 = (LabelData) records.get(i);
				//int id1 = artist1.getId();
				cluster_id1 = label1.getCluster_id();
				cluster1 = -1;
			} else if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel1 = (ReleaseLabel) records.get(i);
				cluster_id1 = relabel1.getCluster_id();
			}

			if (clusterinvertedList.containsKey(i))
				cluster1 = clusterinvertedList.get(i);
			//System.out.println("evaluate \t"+i+"\t"+cluster_id1+"\t"+cluster1);
			for (int j = i + 1; j < records.size(); j++) {
				int cluster_id2 = -1;
				int cluster2 = -1;

				if (entityType.equalsIgnoreCase("label")) {
					LabelData label2 = (LabelData) records.get(j);
					cluster_id2 = label2.getCluster_id();
				} else if (entityType.equalsIgnoreCase("releaselabel")) {
					ReleaseLabel relabel2 = (ReleaseLabel) records.get(j);
					cluster_id2 = relabel2.getCluster_id();
				}

				if (clusterinvertedList.containsKey(j))
					cluster2 = clusterinvertedList.get(j);

				// true positive & false negative
				if (cluster_id1 == cluster_id2) {
					if (cluster1 == cluster2 && cluster1 != -1) {
						tp++;
					} else {
						fn += 1;
						//System.out.println("false negative \t"+id1+"\t"+id2+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name());
						//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
					}

				} else if (cluster1 == cluster2 && cluster1 != -1) {
					fp++;
					//System.out.println("false positive \t"+artist1.getId()+"\t"+artist1.getName()+" vs "+artist2.getId()+"\t"+artist2.getName());
					//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
				}
			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);

		double blockDuplicate = tp;
		double inputDuplicate = tp + fn;
		double aggreCardinality = tp + fp;
		double pairCompleteness = blockDuplicate / inputDuplicate;
		double pairQuality = blockDuplicate / aggreCardinality;

		//System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		//System.out.println(pairCompleteness+"\t"+pairQuality+"\t"+blockDuplicate+"\t"+inputDuplicate+"\t"+aggreCardinality);
	}

	public static void ClusterEvaluation(ArrayList records, HashMap<Integer, Integer> clusterinvertedList) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			//int id1 = artist1.getId();
			int cluster_id1 = artist1.getCluster_id();
			int cluster1 = -1;
			if (clusterinvertedList.containsKey(i))
				cluster1 = clusterinvertedList.get(i);
			//System.out.println("evaluate \t"+i+"\t"+cluster_id1+"\t"+cluster1);
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				//int id2 = artist2.getId();
				int cluster_id2 = artist2.getCluster_id();
				int cluster2 = -1;
				if (clusterinvertedList.containsKey(j))
					cluster2 = clusterinvertedList.get(j);

				// true positive & false negative
				if (cluster_id1 == cluster_id2) {
					if (cluster1 == cluster2 && cluster1 != -1) {
						tp++;
					} else {
						fn += 1;
						//System.out.println("false negative \t"+id1+"\t"+id2+"\t"+artist1.getReal_name()+" vs "+artist2.getReal_name());
						//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
					}

				} else if (cluster1 == cluster2 && cluster1 != -1) {
					fp++;
					//System.out.println("false positive \t"+artist1.getId()+"\t"+artist1.getName()+" vs "+artist2.getId()+"\t"+artist2.getName());
					//System.out.println("\t"+artist1.getName()+" vs "+artist2.getName());
				}
			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);

		double blockDuplicate = tp;
		double inputDuplicate = tp + fn;
		double aggreCardinality = tp + fp;
		double pairCompleteness = blockDuplicate / inputDuplicate;
		double pairQuality = blockDuplicate / aggreCardinality;

		//System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		//System.out.println(pairCompleteness+"\t"+pairQuality+"\t"+blockDuplicate+"\t"+inputDuplicate+"\t"+aggreCardinality);
	}


	public static double FMeasure(HashMap<Integer, Integer> truth, HashMap<Integer, HashSet> results, ArrayList musicArtists, ArrayList discoArtists) {
		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < musicArtists.size(); i++) {
			ArtistData musicart = (ArtistData) musicArtists.get(i);
			//if(!musicart.getTracks().isEmpty()){
			int mid = musicart.getId();
			int tid = mid;
			if (truth.containsKey(mid))
				tid = truth.get(mid);
			HashSet set = null;
			if (results.containsKey(mid)) {
				set = results.get(mid);
			}

			for (int j = 0; j < discoArtists.size(); j++) {
				ArtistData discoart = (ArtistData) discoArtists.get(j);
				//if(!discoart.getTracks().isEmpty()){
				int gid = discoart.getId();
				// true positive & false negative
				if (gid == tid) {
					if (set != null && set.contains(gid)) {
						tp += 1;
					} else {
						fn += 1;
						System.out.println("false negative \t" + mid + "\t" + gid);
					}
				} else if (set != null && set.contains(gid)) {// false positive
					fp += 1;
					System.out.println("false positive \t" + mid + "\t" + gid);
				}
				//}

			}
			//}

		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);
		System.out.println("music \t disco size \t" + musicArtists.size() + "\t" + discoArtists.size());
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);
		return f1;
	}


	public void OverlapIntEvaluation(ArrayList records,
									 HashMap<Integer, ArrayList> invertedList,
									 HashMap<Integer, Block> blocks) {
		// TODO Auto-generated method stub
		/*
		 * aggregate cardinality
		 */
		int aggreCardinality = 0;
		for (Map.Entry<Integer, Block> e : blocks.entrySet()) {
			int key = e.getKey();
			Block block = e.getValue();
			ArrayList nodelist = block.getEntities();
			HashSet set = new HashSet(nodelist);
			aggreCardinality += set.size() * (set.size() - 1) / 2;
			//if(set.size()>1)
			//System.out.println(key+"\t"+nodelist+"\t"+set.size()+"\t"+blockDuplicate);
		}

		/*
		 * blockDuplicate & inputDuplicate
		 */

		int blockDuplicate = 0;
		int inputDuplicate = 0;
		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			int cluster_id1 = artist1.getCluster_id();
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				int cluster_id2 = artist2.getCluster_id();
				if (cluster_id1 == cluster_id2) {
					inputDuplicate += 1;
					ArrayList list1 = invertedList.get(i);
					ArrayList list2 = invertedList.get(j);
					boolean overlap = checkOverlap(list1, list2);
					if (overlap)
						blockDuplicate += 1;
				}

			}
		}
		double pairCompleteness = Double.valueOf(blockDuplicate) / Double.valueOf(inputDuplicate);
		double pairQuality = Double.valueOf(blockDuplicate) / Double.valueOf(aggreCardinality);

		System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		System.out.println(pairCompleteness + "\t" + pairQuality + "\t" + blockDuplicate + "\t" + inputDuplicate + "\t" + aggreCardinality);
	}

	public static void BaseEvaluate(ArrayList records) {


		/*
		 * blockDuplicate & inputDuplicate
		 */
		int size = records.size();
		int blockDuplicate = 0;
		int inputDuplicate = 0;
		int aggreCardinality = size * (size - 1) / 2;

		double f1 = 0;
		double precision = 0;
		double recall = 0;

		double tp = 0;
		double fp = 0;
		double fn = 0;

		for (int i = 0; i < records.size(); i++) {
			ArtistData artist1 = (ArtistData) records.get(i);
			int cluster_id1 = artist1.getCluster_id();
			for (int j = i + 1; j < records.size(); j++) {
				ArtistData artist2 = (ArtistData) records.get(j);
				int cluster_id2 = artist2.getCluster_id();
				if (cluster_id1 == cluster_id2) {
					inputDuplicate += 1;
					blockDuplicate += 1;
					tp += 1;
				} else {
					fp += 1;
				}

			}
		}

		precision = tp / (tp + fp);
		recall = tp / (tp + fn);
		f1 = 2 * precision * recall / (precision + recall);

		System.out.println("baseline evaluation:");
		System.out.println("f1 \t precision \t recall \t fp \t fn \t tp");
		System.out.println(f1 + "\t" + precision + "\t" + recall + "\t" + fp + "\t" + fn + "\t" + tp);

		double pairCompleteness = Double.valueOf(blockDuplicate) / Double.valueOf(inputDuplicate);
		double pairQuality = Double.valueOf(blockDuplicate) / Double.valueOf(aggreCardinality);

		System.out.println("pairCompleteness \t pairQuality \t # duplicates in blocks \t # duplicates in records \t # total comparisons");
		System.out.println(pairCompleteness + "\t" + pairQuality + "\t" + blockDuplicate + "\t" + inputDuplicate + "\t" + aggreCardinality);
	}
}
