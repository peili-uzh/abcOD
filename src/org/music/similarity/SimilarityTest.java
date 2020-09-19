package org.music.similarity;

import org.music.data.ReleaseLabel;

import java.util.ArrayList;

public class SimilarityTest {

	private static String SQL = "select name, id from discogs_label order by id limit 9";
	//"select name, cluster_id from dr_evil";
	//"select ra.name, ac.cluster_id from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' and ac.cluster_id<50 order by ac.cluster_id";
			/*
			"select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, l.id as cluster_id " +
			"from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac, discogs_release_label rl, discogs_label l " +
			"where ra.release_id=r.id and ra.name = ac.artist and date <>'' and r.id=rl.release_id and rl.label_name = l.name and l.id < 10  order by l.id, date;";*/

	public static void main(String[] args) throws Exception {
		/*
		String s1 ="Charlie Ventura";
		String s2 = "Charlie Barnet";
		
		//double score = AttributeSimilarity.StrSim(s1, s2);
		
		ArrayList l1 = new ArrayList();
		l1.add("More Punanny");
		l1.add("More Punanny");
		l1.add("More Punanny");
		ArrayList l2 = new ArrayList();
		l2.add("No STD");
		l2.add("More Punanny");
		//l2.add("Cool Fusion Rhythm");
		
		//AttributeSimilarity.SetSim(l1, l2);
		*/

		//ArtistData a1 = new ArtistData();
		//ArtistData a2 = new ArtistData();

		//LabelData a1 = new LabelData();
		//LabelData a2 = new LabelData();
		ReleaseLabel a1 = new ReleaseLabel();
		ReleaseLabel a2 = new ReleaseLabel();

		a1.setLabel("1");
		a1.setRelease("1");
		ArrayList g1 = new ArrayList();
		g1.add("1");
		a1.setGenreslist(g1);
		ArrayList s1 = new ArrayList();
		s1.add("1");
		a1.setStyleslist(s1);
		a1.setCountry("1");
		a1.setDate(0);
		a1.setStrartist("1");
		a1.setStrextrartist("1");
		a1.setCatno("1");
		a1.setFormat("1");
		a1.setQty("2");
		a1.setFormat_description("d");


		a2.setLabel("1");
		a2.setRelease("2");
		ArrayList g2 = new ArrayList();
		g2.add("2");
		a2.setGenreslist(g2);
		ArrayList s2 = new ArrayList();
		s2.add("1");
		a2.setStyleslist(s2);
		a2.setCountry("1");
		a2.setStrextrartist("1");
		a2.setStrartist("2");
		a2.setCatno("1");
		a2.setFormat("1");
		a2.setQty("2");
		a2.setFormat_description("d");


		//RecordSimilarity sim = new RecordSimilarity(SQL);
		//RecordSimilarity sim = new RecordSimilarity(SQL, "label");
		RecordSimilarity sim = new RecordSimilarity(SQL, "releaselabel");

		for (int i = 0; i < 1; i++) {
			String t = String.valueOf(i);
			//System.out.println("--------- t = "+t);
			a2.setDate(Integer.valueOf(t));
			//double score = sim.decaySimilarity(a1, a2);
			//double score = sim.labelDecaySimilarity(a1, a2);
			double score = sim.relabelDecaySimilarity(a1, a2);
			System.out.println(t + "\t" + score);
		}


	}

}
