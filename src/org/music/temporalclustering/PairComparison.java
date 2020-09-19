package org.music.temporalclustering;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.evaluation.Evaluation;
import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PairComparison extends Cluster {

	public PairComparison() {
	}

	;

	public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist, RecordSimilarity recordSim) {

		if (entityType.equalsIgnoreCase("label"))
			this.labels = dataset;

		this.blocks = blocks;
		this.blockinvertedlist = blockinvertedlist;
		this.recordSim = recordSim;
		this.th = 0.5;

		clusters = new LinkedHashMap<Integer, ArrayList>();
		clusterlist = new HashMap<Integer, Integer>();
		clusterinvertedlist = new HashMap<Integer, ArrayList>();
		clustersign = new HashMap<Integer, ArrayList>();


		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("label")) {
				LabelData label1 = (LabelData) dataset.get(i);
				//int id1 = artist1.getId();
				String name1 = label1.getName();
				String yr1 = label1.getDate();
				int clusterid1 = label1.getCluster_id();
				ArrayList blocks1 = blockinvertedlist.get(i);
				//System.out.println("****\t"+i+"\t"+name1+"\t"+yr1+"\t"+clusterid1);
				System.out.println("*****" + label1.getId() + "\t" + name1 + "\t" + label1.getArtist() + "\t" + label1.getRelease() + "\t" + label1.getGenres() + "\t" + label1.getStyle() + "\t" + label1.getCountry() + "\t" + label1.getDate() + "\t" + label1.getCluster_id());


				for (int j = i + 1; j < dataset.size(); j++) {
					LabelData label2 = (LabelData) dataset.get(j);
					//int id1 = artist1.getId();
					String name2 = label2.getName();
					String yr2 = label2.getDate();
					int clusterid2 = label2.getCluster_id();
					ArrayList blocks2 = blockinvertedlist.get(j);

					boolean overlap = checkOverlap(blocks1, blocks2);
					if (overlap) {
						//double score = recordSim.agreeSimilarity(artist1, artist2);
						double score = recordSim.labelDecaySimilarity(label1, label2);
						double timelessscore = recordSim.labelTimelessSimilarity(label1, label2);

						int t = Integer.parseInt(label2.getDate()) - Integer.parseInt(label1.getDate());
						System.out.println("\t r" + label1.getId() + ", r" + label2.getId() + "\t" + score + "\t" + timelessscore + "\t" + t + "\t" + label1.getCluster_id() + "\t" + label2.getCluster_id());
					}

				}
			}

		}


		Evaluation evaluation = new Evaluation();
		//evaluation.ClusterEvaluation(artists, clusterlist);
	}

	public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist, RecordSimilarity recordSim) {

		this.artists = artists;
		this.blocks = blocks;
		this.blockinvertedlist = blockinvertedlist;
		this.recordSim = recordSim;
		this.th = 0.5;

		clusters = new LinkedHashMap<Integer, ArrayList>();
		clusterlist = new HashMap<Integer, Integer>();
		clusterinvertedlist = new HashMap<Integer, ArrayList>();
		clustersign = new HashMap<Integer, ArrayList>();

		int count = 0;
		int validcount = 0;

		System.out.println("S-Phase:");


		for (int i = 0; i < artists.size(); i++) {
			ArtistData artist1 = (ArtistData) artists.get(i);
			//int id1 = artist1.getId();
			String name1 = artist1.getName();
			String yr1 = artist1.getDate();
			int clusterid1 = artist1.getCluster_id();
			ArrayList blocks1 = blockinvertedlist.get(i);
			//System.out.println("****\t"+i+"\t"+name1+"\t"+yr1+"\t"+clusterid1);
			System.out.println("*****" + artist1.getId() + "\t" + name1 + "\t" + artist1.getRelease() + "\t" + artist1.getGenres() + "\t" + artist1.getStyle() + "\t" + artist1.getCountry() + "\t" + artist1.getDate() + "\t" + artist1.getCluster_id());


			for (int j = i + 1; j < artists.size(); j++) {
				ArtistData artist2 = (ArtistData) artists.get(j);
				//int id1 = artist1.getId();
				String name2 = artist2.getName();
				String yr2 = artist2.getDate();
				int clusterid2 = artist2.getCluster_id();
				ArrayList blocks2 = blockinvertedlist.get(j);

				boolean overlap = checkOverlap(blocks1, blocks2);
				if (overlap) {
					//double score = recordSim.agreeSimilarity(artist1, artist2);
					double score = recordSim.decaySimilarity(artist1, artist2);
					if (score >= th)
						validcount += 1;
					int t = Integer.parseInt(artist2.getDate()) - Integer.parseInt(artist1.getDate());
					System.out.println("\t r" + artist1.getId() + ", r" + artist2.getId() + "\t" + score + "\t" + t);
				}

			}


		}

		System.out.println("++++ # necessary comparisons \t" + validcount);

		Evaluation evaluation = new Evaluation();
		//evaluation.ClusterEvaluation(artists, clusterlist);
	}

	private double recordClusterSimilarity(int id1, ArrayList records) {
		// TODO Auto-generated method stub
		double sumscore = 0;
		double avgscore = 0;
		ArtistData artist1 = (ArtistData) artists.get(id1);
		for (int i = 0; i < records.size(); i++) {
			int id2 = (Integer) records.get(i);
			ArtistData artist2 = (ArtistData) artists.get(id2);
			//System.out.println("\t\t compare "+id1+" with "+id2);
			double score = recordSim.agreeSimilarity(artist1, artist2);
			//System.out.println("\t\t record sim \t"+id1+"\t"+id2+"\t"+score+"\t"+artist1.getCluster_id()+"\t"+artist2.getCluster_id());
			sumscore += score;
		}

		avgscore = sumscore / Double.valueOf(records.size());

		return avgscore;
	}


}
