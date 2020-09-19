package org.music.temporalclustering;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cluster {

	public Cluster() {
	}

	;

	public ArrayList artists;
	public ArrayList labels;
	public ArrayList relabels;

	public LinkedHashMap<Integer, ArrayList> blocks; // block_id - record ids (blocks are sorted)
	public HashMap<Integer, ArrayList> blockinvertedlist; // record id - block ids (blocks are sorted)
	public HashMap<String, Integer> blocklist; //  block id - block key
	public HashMap<Integer, String> timelist; // record id - year

	public LinkedHashMap<Integer, ArrayList> clusters; // clster_id - record ids
	public HashMap<Integer, Integer> clusterlist; // record_id - cluster_id
	public HashMap<Integer, ArrayList> clusterinvertedlist; // cluster_id - block ids 
	public HashMap<Integer, ArrayList> clustersign; // cluster_id - signature id (start - end records)

	public RecordSimilarity recordSim;


	public ArrayList getRelabels() {
		return relabels;
	}

	public void setRelabels(ArrayList relabels) {
		this.relabels = relabels;
	}

	public ArrayList getLabels() {
		return labels;
	}

	public void setLabels(ArrayList labels) {
		this.labels = labels;
	}

	public HashMap<Integer, ArrayList> getBlockinvertedlist() {
		return blockinvertedlist;
	}

	public void setBlockinvertedlist(HashMap<Integer, ArrayList> blockinvertedlist) {
		this.blockinvertedlist = blockinvertedlist;
	}

	public HashMap<Integer, ArrayList> getClustersign() {
		return clustersign;
	}

	public void setClustersign(HashMap<Integer, ArrayList> clustersign) {
		this.clustersign = clustersign;
	}

	public double th;

	public void setCluster(HashMap<Integer, Integer> recordlist) {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.artists.size(); i++) {
			ArtistData a = (ArtistData) artists.get(i);
			int cid = recordlist.get(i);
			System.out.println(i + "\t" + a.getName() + "\t" + a.getRelease() + "\t" + a.getGenres() + "\t" + a.getStyle() + "\t" + a.getCountry() + "\t" + a.getDate() + "\t" + a.getCluster_id() + "\t" + cid);

		}

	}

	public void setClusterType(HashMap<Integer, Integer> recordlist, String entityType) {
		// TODO Auto-generated method stub
		if (entityType.equalsIgnoreCase("label")) {
			for (int i = 0; i < this.labels.size(); i++) {
				LabelData a = (LabelData) labels.get(i);
				int cid = recordlist.get(i);
				System.out.println(i + "\t" + a.getName() + "\t" + a.getRelease() + "\t" + a.getArtist() + "\t" + a.getGenres() + "\t" + a.getStyle() + "\t" + a.getCountry() + "\t" + a.getDate() + "\t" + a.getCluster_id() + "\t" + cid);

			}
		}


	}

	public ArrayList getArtists() {
		return artists;
	}

	public void setArtists(ArrayList artists) {
		this.artists = artists;
	}

	public LinkedHashMap<Integer, ArrayList> getBlocks() {
		return blocks;
	}

	public void setBlocks(LinkedHashMap<Integer, ArrayList> blocks) {
		this.blocks = blocks;
	}

	public HashMap<String, Integer> getBlocklist() {
		return blocklist;
	}

	public void setBlocklist(HashMap<String, Integer> blocklist) {
		this.blocklist = blocklist;
	}

	public HashMap<Integer, String> getTimelist() {
		return timelist;
	}

	public void setTimelist(HashMap<Integer, String> timelist) {
		this.timelist = timelist;
	}

	public LinkedHashMap<Integer, ArrayList> getClusters() {
		return clusters;
	}

	public void setClusters(LinkedHashMap<Integer, ArrayList> clusters) {
		this.clusters = (LinkedHashMap<Integer, ArrayList>) clusters;
	}

	public HashMap<Integer, Integer> getClusterlist() {
		return clusterlist;
	}

	public void setClusterlist(HashMap<Integer, Integer> clusterlist) {
		this.clusterlist = clusterlist;
	}

	public HashMap<Integer, ArrayList> getClusterinvertedlist() {
		return clusterinvertedlist;
	}

	public void setClusterinvertedlist(
			HashMap<Integer, ArrayList> clusterinvertedlist) {
		this.clusterinvertedlist = clusterinvertedlist;
	}

	public void printMap(HashMap<Integer, ArrayList> map) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, ArrayList> e : map.entrySet()) {
			int bid = e.getKey();

			ArrayList list = e.getValue();
			System.out.println(bid + "\t" + list);
			for (int i = 0; i < list.size(); i++) {
				int id = (Integer) list.get(i);
				//System.out.println("\t"+id+"\t"+timelist.get(id));
			}

		}
	}

	public void updateClusters(int id, int clusterid) {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		if (clusters.containsKey(clusterid))
			list = clusters.get(clusterid);

		list.add(list.size(), id);
		clusters.put(clusterid, list);
	}

	public void updateClusterInvertedList(int id, int clusterid) {
		// TODO Auto-generated method stub
		ArrayList blocks = blockinvertedlist.get(id);
		//System.out.println("\t"+id+"\t in # blocks \t"+blocks.size()+"\t"+blocks);

		ArrayList list = new ArrayList();
		if (clusterinvertedlist.containsKey(clusterid))
			list = clusterinvertedlist.get(clusterid);

		list.addAll(blocks);
		clusterinvertedlist.put(clusterid, list);
		//System.out.println("\t"+clusterid+"\t"+list);
		
		/*
		for(int i = 0; i< blocks.size(); i++){
			int blockid = (Integer) blocks.get(i);
			ArrayList list = new ArrayList();
			if(clusterinvertedlist.containsKey(clusterid))
				list = clusterinvertedlist.get(clusterid);
			
			list.add(list.size(), blockid);
			clusterinvertedlist.put(clusterid, list);
			System.out.println("\t"+clusterid+"\t"+list);
		}*/
	}

	public boolean checkOverlap(ArrayList blocks1, ArrayList blocks2) {
		// TODO Auto-generated method stub
		//System.out.println(blocks1+"\t"+blocks2);

		if (blocks1.isEmpty() || blocks1 == null) {
			System.out.println("block1 is empty");
		}

		if (blocks2.isEmpty()) {
			System.out.println("block2 is empty");
		}

		ArrayList temp = new ArrayList();
		temp.addAll(blocks1);
		boolean overlap = true;
		temp.retainAll(blocks2);
		if (temp.isEmpty())
			overlap = false;
		//System.out.println("\t"+"\t"+temp);
		return overlap;
	}

}
