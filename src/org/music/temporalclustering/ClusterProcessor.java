package org.music.temporalclustering;

import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClusterProcessor extends Cluster {

	public RecordSimilarity recordSim;

	public ClusterProcessor(String sql, String entityType) throws Exception {

		recordSim = new RecordSimilarity(sql, entityType);

	}

	;

	public ClusterProcessor(String sql) throws Exception {

		recordSim = new RecordSimilarity(sql);

	}

	;

	public void pairwiseComparison(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist) {

		this.artists = artists;

		PairComparison pairCompare = new PairComparison();

		pairCompare.process(artists, blocks, blockinvertedlist, recordSim);
		
		
		
		/*
		// DPhase
		DPhase dphase = new DPhase();
		dphase.process(artists, sphase.clusters, sphase.clusterlist, clustersign, sphase.clusterinvertedlist, recordSim);
		
		this.clusterlist = dphase.recordlist;*/
	}

	public void pairwiseEntityComparison(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist) {

		if (entityType.equalsIgnoreCase("label"))
			this.labels = dataset;

		PairComparison pairCompare = new PairComparison();

		pairCompare.processEntity(entityType, dataset, blocks, blockinvertedlist, recordSim);
		
		
		/*
		// DPhase
		DPhase dphase = new DPhase();
		dphase.process(artists, sphase.clusters, sphase.clusterlist, clustersign, sphase.clusterinvertedlist, recordSim);
		
		this.clusterlist = dphase.recordlist;*/
	}

	public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist) {

		this.artists = artists;

		SPhase sphase = new SPhase();

		sphase.process(artists, blocks, blockinvertedlist, recordSim);

		clustersign = new HashMap<Integer, ArrayList>();

		generateClusterSignature(sphase.getClusters());


		// DPhase
		DPhase dphase = new DPhase();
		dphase.process(artists, sphase.clusters, sphase.clusterlist, clustersign, sphase.clusterinvertedlist, recordSim);

		this.clusterlist = dphase.recordlist;
	}

	public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist) {


		SPhase sphase = new SPhase();

		sphase.processEntity(entityType, dataset, blocks, blockinvertedlist, recordSim);

		clustersign = new HashMap<Integer, ArrayList>();

		generateClusterSignature(sphase.getClusters());


		// DPhase
		DPhase dphase = new DPhase();
		dphase.processEntity(entityType, dataset, sphase.clusters, sphase.clusterlist, clustersign, sphase.clusterinvertedlist, recordSim);

		this.clusterlist = dphase.recordlist;
	}

	private void generateClusterSignature(LinkedHashMap<Integer, ArrayList> clusters) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, ArrayList> e : clusters.entrySet()) {
			int cid = e.getKey();
			ArrayList list = e.getValue();
			//System.out.println(cid+"\t"+list.size());
			ArrayList sign = new ArrayList();


			sign.add(0, list.get(0));
			sign.add(1, list.get(list.size() - 1));
			
			/*
			for(int i = 0; i< list.size(); i++){
				int rid = (Integer) list.get(i);
				ArtistData artist = (ArtistData) artists.get(rid);
				//sign.add(sign.size(), list.get(i));
				//System.out.println("\t\t"+rid+"\t"+artist.getDate()+"\t"+artist.getCluster_id());
				
			}*/
			//System.out.println("\t signature \t"+sign);
			clustersign.put(cid, sign);
		}
	}


}
