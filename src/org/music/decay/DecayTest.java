package org.music.decay;

public class DecayTest {

	private static String SQL1 = "select name, id from discogs_label where name like 'New%'";
	//"select name, cluster_id from dr_evil";
	//"select   ra.name, ac.cluster_id from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' order by ra.name limit 200";

	public static void main(String[] args) throws Exception {

		//DecayProcessor decayprocessor = new DecayProcessor(SQL1);

		//DecayProcessor decayprocessor = new DecayProcessor(SQL1, "label");
		DecayProcessor decayprocessor = new DecayProcessor(SQL1, "releaselabel");
		AgreementDecay agrdecay = new AgreementDecay();
		DisagreementDecay disdecay = new DisagreementDecay();

		String atr = "";

		atr = "release";
		atr = "genres";
		atr = "artist";
		atr = "styles";
		atr = "extrartist";
		atr = "label";
		atr = "catno";
		atr = "format";


		atr = "country";
		System.out.println("+++ " + atr + " agreement decay");
		decayprocessor.process(atr, agrdecay);
		System.out.println("+++ " + atr + " disagreement decay");
		//decayprocessor.process(atr, disdecay);


	}

}
