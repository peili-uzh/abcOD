package org.music.temporalblocking;

import java.util.ArrayList;


public class TemporalBlockTest {

    private static String SQL = /*"select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, l.id as cluster_id " +
			"from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac, discogs_release_label rl, discogs_label l " +
			"where ra.release_id=r.id and ra.name = ac.artist and date <>'' and r.id=rl.release_id and rl.label_name = l.name and l.id < 5  order by date;";
								/*"select r.id, r.title as release, r.genres, r.styles, r.country, date, ra.name, ac.cluster_id "
								+"from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' "
								+"and ac.cluster_id<50 order by date limit 50";*/
            "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id, r.id " +
                    "from discogs_release r, discogs_release_artist ra, discogs_release_label rl, discogs_label l, discogs_release_format rf, discogs_release_extraartist re " +
                    "where ra.release_id=r.id  and date <>'' and rf.release_id = r.id and re.release_id = r.id " +
                    "and r.id=rl.release_id and rl.label_name = l.name and (  l.name = 'Cisco Music' or r.id in (1073607, 371291, 1499506) ) and date<>'?'  order by date, catno";


    public static void main(String[] args) throws Exception {
		
		/*
		ArrayList artists = new ArrayList();
		
		BlockProcessor blockprocessor = new BlockProcessor(SQL);
		artists = blockprocessor.getArtists();
		
		TimelessBlocking timelessblock = new TimelessBlocking();
		blockprocessor.process(timelessblock);
		
		int t = 3;
		TemporalBlocking temporalblock = new TemporalBlocking(t);
		//blockprocessor.process(temporalblock);
		 * 
		 */
        String sql = "select discogs_id from inter_music_release_full_new limit 10";
        // BlockProcessor blockprocessor = new BlockProcessor(SQL, "label");
        //BlockProcessor blockprocessor = new BlockProcessor(SQL, "releaselabel");

        TimelessBlocking timelessblock = new TimelessBlocking();
        //blockprocessor.processEntity(timelessblock, "label");
        //blockprocessor.processEntity(timelessblock, "releaselabel");


        DataAnalysis analysis = new DataAnalysis(SQL, "releaselabel");
        ArrayList dataset = analysis.getRelabels();
        //analysis.checkDistribution("releaselabel", dataset);
        analysis.checkSubDistribution("releaselabel", dataset);
    }


}
