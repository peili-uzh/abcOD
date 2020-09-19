package org.music.temporalclustering;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;
import org.music.evaluation.Evaluation;
import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DPhase extends Cluster {

	public HashMap<Integer, Integer> recordlist;


	public HashMap<Integer, Integer> getRecordlist() {
		return recordlist;
	}

	public void setRecordlist(HashMap<Integer, Integer> recordlist) {
		this.recordlist = recordlist;
	}

	public DPhase() {

	}

	public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> clusters, HashMap<Integer, Integer> clusterlist, HashMap<Integer, ArrayList> clustersign, HashMap<Integer, ArrayList> clusterinvertedlist, RecordSimilarity recordSim) {

		this.clusters = new LinkedHashMap<Integer, ArrayList>();
		this.clusterlist = new HashMap<Integer, Integer>();
		this.clusterinvertedlist = new HashMap<Integer, ArrayList>();
		this.blockinvertedlist = clusterinvertedlist;
		this.blocks = clusters;
		this.artists = artists;
		this.clustersign = clustersign;
		this.recordSim = recordSim;
		this.th = 0.8;

		recordlist = new HashMap<Integer, Integer>(); // record-cluster list

		System.out.println("____ proceed DPhase");

		for (Map.Entry<Integer, ArrayList> e : clusters.entrySet()) {
			int cid = e.getKey();
			ArrayList list = e.getValue();
			ArrayList sign = new ArrayList();
			if (clustersign.containsKey(cid))
				sign = clustersign.get(cid);
			ArrayList blocks1 = blockinvertedlist.get(cid);

			ArtistData artist1 = (ArtistData) artists.get((Integer) sign.get(0));
			//System.out.println(cid+"\t"+artist1.getDate()+"\t"+artist1.getCluster_id());
			//System.out.println("\t in blocks \t"+blocks1);
			//printSign(sign);


			if (this.clusters.isEmpty()) {
				updateClusters(cid, cid);
				this.clusterlist.put(cid, cid);
				updateClusterInvertedList(cid, cid);
				//System.out.println("create new cluster \t"+cid+"\t for sub_cluster \t"+cid);
			} else {
				boolean unmatched = true;
				double maxscore = 0;
				int maxcluster = 0;

				for (Map.Entry<Integer, ArrayList> m : this.clusters.entrySet()) {
					ArrayList subclusters = m.getValue();
					int clusterid = m.getKey();
					ArrayList blocks2 = this.clusterinvertedlist.get(clusterid);
					//System.out.println("\t" +clusterid+ "\t in blocks \t"+blocks2);

					/*
					 * check if share any blockid
					 */
					boolean overlap = checkOverlap(blocks1, blocks2);
					//System.out.println("\t compare w cluster \t"+clusterid+"\t");
					if (overlap) {
						//printCluster(subclusters);
						double score = recordClusterSimilarity("artist", sign, subclusters);
						//System.out.println("  compare w cluster \t"+clusterid+"\t"+score);

						//count += records.size();

						if (score > maxscore) {
							maxscore = score;
							maxcluster = clusterid;
						}

						//if(score < th)
						//invalidcount += records.size();
					}
				}

				/*
				 * add record int maxcluster if maxscore > th
				 */
				if (maxscore >= th) {
					unmatched = false;
					//System.out.println("\t add subcluster"+cid+" into cluster "+maxcluster);
					updateClusters(cid, maxcluster);
					updateClusterInvertedList(cid, maxcluster);
					this.clusterlist.put(cid, maxcluster);
				}

				if (unmatched) {
					updateClusters(cid, cid);
					this.clusterlist.put(cid, cid);
					updateClusterInvertedList(cid, cid);
					//System.out.println("\t create new cluster \t"+cid+"\t for subcluster \t"+cid);
				}
			}
		}

		recordlist = updateRecordList(clusterlist);

		//setCluster(recordlist);

		Evaluation evaluation = new Evaluation();
		evaluation.ClusterEvaluation(artists, recordlist);
	}

	public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> clusters, HashMap<Integer, Integer> clusterlist, HashMap<Integer, ArrayList> clustersign, HashMap<Integer, ArrayList> clusterinvertedlist, RecordSimilarity recordSim) {

		if (entityType.equalsIgnoreCase("label"))
			this.labels = dataset;
		else if (entityType.equalsIgnoreCase("releaselabel"))
			this.relabels = dataset;
		this.clusters = new LinkedHashMap<Integer, ArrayList>();
		this.clusterlist = new HashMap<Integer, Integer>();
		this.clusterinvertedlist = new HashMap<Integer, ArrayList>();
		this.blockinvertedlist = clusterinvertedlist;
		this.blocks = clusters;
		this.clustersign = clustersign;
		this.recordSim = recordSim;
		this.th = 0.6;

		recordlist = new HashMap<Integer, Integer>(); // record-cluster list

		System.out.println("____ proceed DPhase");

		for (Map.Entry<Integer, ArrayList> e : clusters.entrySet()) {
			int cid = e.getKey();
			ArrayList list = e.getValue();
			ArrayList sign = new ArrayList();
			if (clustersign.containsKey(cid))
				sign = clustersign.get(cid);
			ArrayList blocks1 = blockinvertedlist.get(cid);

			//ArtistData artist1 = (ArtistData) artists.get((Integer) sign.get(0));
			//System.out.println(cid+"\t"+artist1.getDate()+"\t"+artist1.getCluster_id());
			//System.out.println("\t in blocks \t"+blocks1);
			printSign(sign);


			if (this.clusters.isEmpty()) {
				updateClusters(cid, cid);
				this.clusterlist.put(cid, cid);
				updateClusterInvertedList(cid, cid);
				//System.out.println("create new cluster \t"+cid+"\t for sub_cluster \t"+cid);
			} else {
				boolean unmatched = true;
				double maxscore = 0;
				int maxcluster = 0;

				for (Map.Entry<Integer, ArrayList> m : this.clusters.entrySet()) {
					ArrayList subclusters = m.getValue();
					int clusterid = m.getKey();
					ArrayList blocks2 = this.clusterinvertedlist.get(clusterid);
					//System.out.println("\t" +clusterid+ "\t in blocks \t"+blocks2);

					/*
					 * check if share any blockid
					 */
					boolean overlap = checkOverlap(blocks1, blocks2);
					//System.out.println("\t compare w cluster \t"+clusterid+"\t");
					if (overlap) {
						//printCluster(subclusters);
						double score = recordClusterSimilarity(entityType, sign, subclusters);
						System.out.println("  compare w cluster \t" + clusterid + "\t" + score);

						//count += records.size();

						if (score > maxscore) {
							maxscore = score;
							maxcluster = clusterid;
						}

						//if(score < th)
						//invalidcount += records.size();
					}
				}

				/*
				 * add record int maxcluster if maxscore > th
				 */
				if (maxscore >= th) {
					unmatched = false;
					//System.out.println("\t add subcluster"+cid+" into cluster "+maxcluster);
					updateClusters(cid, maxcluster);
					updateClusterInvertedList(cid, maxcluster);
					this.clusterlist.put(cid, maxcluster);
				}

				if (unmatched) {
					updateClusters(cid, cid);
					this.clusterlist.put(cid, cid);
					updateClusterInvertedList(cid, cid);
					//System.out.println("\t create new cluster \t"+cid+"\t for subcluster \t"+cid);
				}
			}
		}

		recordlist = updateRecordList(clusterlist);

		setClusterType(recordlist, entityType);

		Evaluation evaluation = new Evaluation();
		evaluation.EntityEvaluation(entityType, dataset, recordlist);

	}


	private HashMap<Integer, Integer> updateRecordList(HashMap<Integer, Integer> clusterlist) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> recordlist = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> e : clusterlist.entrySet()) {
			int rid = e.getKey();
			int subid = e.getValue();
			int cid = this.clusterlist.get(subid);
			//System.out.println(rid+"\t"+subid+"\t"+cid);
			recordlist.put(rid, cid);
		}
		return recordlist;

	}

	private void printCluster(ArrayList subclusters) {
		// TODO Auto-generated method stub
		//System.out.println("\t"+subclusters);
		for (int i = 0; i < subclusters.size(); i++) {
			int cid = (Integer) subclusters.get(i);
			//System.out.println("\t"+"subcluster \t"+cid);
			ArrayList sign = clustersign.get(cid);
			printSign(sign);
		}
	}

	private void printSign(ArrayList sign) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sign.size(); i++) {
			int id = (Integer) sign.get(i);
			//ArtistData a = (ArtistData) artists.get(id);
			//LabelData a = (LabelData) labels.get(id);
			ReleaseLabel a = (ReleaseLabel) relabels.get(id);

			System.out.println("\t" + (id + 1) + "\t" + a.getLabel() + "\t" + a.getRelease() + "\t" + a.getGenreslist() + "\t" + a.getStrartist() + "\t" + a.getCountry() + "\t" + a.getDate() + "\t" + a.getCluster_id());
		}
	}

	private double recordClusterSimilarity(String entityType, ArrayList sign, ArrayList subclusters) {
		// TODO Auto-generated method stub
		// compare sign w. signs of subclusters
		double avgscore = 0;
		double count = 0;
		//System.out.println("\t"+sign);
		for (int n = 0; n < sign.size(); n++) {
			int id1 = (Integer) sign.get(n);

			if (entityType.equalsIgnoreCase("artist")) {
				ArtistData a1 = (ArtistData) artists.get(id1);
				for (int i = 0; i < subclusters.size(); i++) {
					int clusterid = (Integer) subclusters.get(i);
					ArrayList sign2 = new ArrayList();
					if (clustersign.containsKey(clusterid))
						sign2 = clustersign.get(clusterid);
					//System.out.println("\t\t"+clusterid+"\t"+sign2);
					for (int m = 0; m < sign2.size(); m++) {
						int id2 = (Integer) sign2.get(m);
						ArtistData a2 = (ArtistData) artists.get(id2);
						//System.out.println("\t\t compare "+id1+"\t"+a1.getName()+"\t"+id2+"\t"+a2.getName());
						double score = recordSim.decaySimilarity(a1, a2);
						if (score >= th) {
							count += 1;
							avgscore += score;
						}
						//System.out.println("\t\t\t"+id1+"\t"+id2+"\t"+score+"\t"+a1.getCluster_id()+"\t"+a2.getCluster_id());			
					}

				}
			} else if (entityType.equalsIgnoreCase("label")) {
				LabelData label1 = (LabelData) labels.get(id1);
				for (int i = 0; i < subclusters.size(); i++) {
					int clusterid = (Integer) subclusters.get(i);
					ArrayList sign2 = new ArrayList();
					if (clustersign.containsKey(clusterid))
						sign2 = clustersign.get(clusterid);
					//System.out.println("\t\t"+clusterid+"\t"+sign2);
					for (int m = 0; m < sign2.size(); m++) {
						int id2 = (Integer) sign2.get(m);
						LabelData label2 = (LabelData) labels.get(id2);
						//System.out.println("\t\t compare "+id1+"\t"+a1.getName()+"\t"+id2+"\t"+a2.getName());
						double score = recordSim.labelDecaySimilarity(label1, label2);
						System.out.println("\t\t" + label1.getId() + "\t" + label2.getId() + "\t" + score);
						//score = recordSim.labelTimelessSimilarity(label1, label2);
						if (score >= th) {
							count += 1;
							avgscore += score;
						}
						//System.out.println("\t\t\t"+id1+"\t"+id2+"\t"+score+"\t"+a1.getCluster_id()+"\t"+a2.getCluster_id());			
					}

				}
			} else if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel1 = (ReleaseLabel) relabels.get(id1);
				for (int i = 0; i < subclusters.size(); i++) {
					int clusterid = (Integer) subclusters.get(i);
					ArrayList sign2 = new ArrayList();
					if (clustersign.containsKey(clusterid))
						sign2 = clustersign.get(clusterid);
					for (int m = 0; m < sign2.size(); m++) {
						int id2 = (Integer) sign2.get(m);
						ReleaseLabel relabel2 = (ReleaseLabel) relabels.get(id2);
						double score = recordSim.relabelDecaySimilarity(relabel1, relabel2);
						System.out.println("\t\t" + relabel1.getId() + "\t" + relabel2.getId() + "\t" + score);
						if (score >= th) {
							count += 1;
							avgscore += score;
						}
					}
				}
			}


		}
		if (count > 0)
			avgscore = avgscore / count;

		return avgscore;
	}
	
	/*
	public void updateClusterInvertedList(int id, int clusterid, HashMap<Integer, ArrayList> clusterinvertedlist) {
		// TODO Auto-generated method stub
		ArrayList blocks = clusterinvertedlist.get(id);
		//System.out.println("\t"+id+"\t in # blocks \t"+blocks.size()+"\t"+blocks);
		for(int i = 0; i< blocks.size(); i++){
			int blockid = (Integer) blocks.get(i);
			ArrayList list = new ArrayList();
			if(this.clusterinvertedlist.containsKey(clusterid))
				list = this.clusterinvertedlist.get(clusterid);
			
			list.add(list.size(), blockid);
			this.clusterinvertedlist.put(clusterid, list);
			//System.out.println("\t"+clusterid+"\t"+list);
		}
	}*/

}
