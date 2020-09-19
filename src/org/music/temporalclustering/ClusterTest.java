package org.music.temporalclustering;

import org.music.evaluation.Evaluation;
import org.music.temporalblocking.BlockProcessor;
import org.music.temporalblocking.TemporalBlocking;
import org.music.temporalblocking.TimelessBlocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ClusterTest {

	private static String SQL = "select r.id, r.title as release, r.genres, r.styles, r.country, date, ra.name, ac.cluster_id "
			+ "from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' and r.title<>'' and r.genres<>'' and r.styles<>'' and r.country<>'' and ra.name<>'' "
			+ "and ac.cluster_id>0 and ac.cluster_id<100 order by date";
	private static String SQL1 = //"select name, cluster_id from dr_evil";
			"select ra.name, ac.cluster_id from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' and ac.cluster_id<50 order by ac.cluster_id";


	public static void main(String[] args) throws Exception {

		ArrayList artists = new ArrayList();

		String sql = "select id, release, genres, track, country, year, name, cluster_id from dr_evil  order by id";

		BlockProcessor blockprocessor = new BlockProcessor(SQL);
		artists = blockprocessor.getArtists();
		LinkedHashMap<Integer, ArrayList> blocks = new LinkedHashMap<Integer, ArrayList>(); // block_id - record ids (blocks are sorted)
		HashMap<Integer, ArrayList> blockinvertedlist = new HashMap<Integer, ArrayList>(); // record id - block ids (blocks are sorted)
		ClusterProcessor clusterprocessor = new ClusterProcessor(SQL1);


		TimelessBlocking timelessblock = new TimelessBlocking();
		blockprocessor.process(timelessblock);

		blocks = timelessblock.getBlocks();
		blockinvertedlist = timelessblock.getBlockinvertedlist();
		//System.out.println(blockinvertedlist.get(907));
		// time = infinity

		//clusterprocessor.process(artists, blocks, blockinvertedlist);

		HashMap<Integer, Integer> clusterlist0 = new HashMap();
		clusterlist0 = clusterprocessor.getClusterlist();
		Evaluation evaluation = new Evaluation();
		//evaluation.ClusterEvaluation(clusterprocessor.getArtists(), clusterlist0);
		//clusterprocessor.pairwiseComparison(artists, blocks, blockinvertedlist);


		for (int t = 5; t < 6; t++) {
			System.out.println("t=" + t);
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
		}


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
