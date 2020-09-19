package org.music.temporalclustering;

import org.music.evaluation.Evaluation;
import org.music.temporalblocking.BlockProcessor;
import org.music.temporalblocking.TimelessBlocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LabelClusterTest {

	private static String SQL = "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, l.id as cluster_id " +
			"from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac, discogs_release_label rl, discogs_label l " +
			"where ra.release_id=r.id and ra.name = ac.artist and date <>'' and r.id=rl.release_id and rl.label_name = l.name and (" +
			"  l.name like '13th Floor Records%')  order by date;";
	private static String SQL1 = //"select name, cluster_id from dr_evil";
			"select name, id from discogs_label where name like 'New Music%'";


	public static void main(String[] args) throws Exception {

		ArrayList labels = new ArrayList();


		BlockProcessor blockprocessor = new BlockProcessor(SQL, "label");
		LinkedHashMap<Integer, ArrayList> blocks = new LinkedHashMap<Integer, ArrayList>(); // block_id - record ids (blocks are sorted)
		HashMap<Integer, ArrayList> blockinvertedlist = new HashMap<Integer, ArrayList>(); // record id - block ids (blocks are sorted)

		ClusterProcessor clusterprocessor = new ClusterProcessor(SQL1, "label");


		TimelessBlocking timelessblock = new TimelessBlocking();
		blockprocessor.processEntity(timelessblock, "label");

		labels = blockprocessor.getLabels();
		blocks = timelessblock.getBlocks();
		blockinvertedlist = timelessblock.getBlockinvertedlist();
		//System.out.println(blockinvertedlist.get(907));
		// time = infinity

		clusterprocessor.processEntity("label", labels, blocks, blockinvertedlist);

		HashMap<Integer, Integer> clusterlist0 = new HashMap();
		clusterlist0 = clusterprocessor.getClusterlist();
		Evaluation evaluation = new Evaluation();
		//evaluation.ClusterEvaluation(clusterprocessor.getArtists(), clusterlist0);
		//clusterprocessor.pairwiseEntityComparison("label", labels, blocks, blockinvertedlist);
		
		/*
		for(int t=5; t<6; t++){
			System.out.println("t="+t);
			TemporalBlocking temporalblock = new TemporalBlocking(t);
			blockprocessor.process(temporalblock);
			
			blocks = temporalblock.getBlocks();
			blockinvertedlist = temporalblock.getBlockinvertedlist();
			//System.out.println(blockinvertedlist.get(907));
			
			//Cluster cluster = new Cluster();
			
			//clusterprocessor.process(artists, blocks, blockinvertedlist);
			
			HashMap<Integer, Integer> clusterlist1 = new HashMap();
			clusterlist1 = clusterprocessor.getClusterlist();
			
			
			//evaluation.ClusterComparision(clusterlist0, clusterlist1);
			//clusterprocessor.pairwiseComparison(artists, blocks, blockinvertedlist);
		}*/


		//clusterprocessor.printMap(clusterprocessor.getClusters());
		
		/*
		// t=4
		TemporalBlocking temporalblock2 = new TemporalBlocking(4);
		blockprocessor.process(temporalblock2);
		
		blocks = temporalblock2.getBlocks();
		blockinvertedlist = temporalblock2.getBlockinvertedlist();
		
		
		//Cluster cluster = new Cluster();
		
		clusterprocessor.process(artists, blocks, blockinvertedlist);
		HashMap<Integer, Integer> clusterlist2 = new HashMap();
		clusterlist2 = clusterprocessor.getClusterlist();
		
		Evaluation evaluation = new Evaluation();
		evaluation.ClusterComparision(clusterlist2, clusterlist1);*/


	}

}
