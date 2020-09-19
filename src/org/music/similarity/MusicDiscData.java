package org.music.similarity;

import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.evaluation.Evaluation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MusicDiscData {

	public static ArtistData artist;
	public static String MUSICBRAINZ_TRACK = "select distinct tn.name from artist_credit_name acn, track t, artist_credit ac, " +
			"track_name tn where acn.artist=?  and t.artist_credit = ac.id and ac.id=acn.artist_credit and tn.id=t.name";
	//"select r.id, r.name from recording_sample r, l_artist_recording l where r.id = l.entity1 and l.entity0 = ?";
	public static String DISCOGS_TRACK = "select distinct t.title from discogs_track t, discogs_track_artist a where a.artist_id=? and a.track_id=t.id";

	public static void main(String[] args) throws Exception {
		ArrayList musicArtists = new ArrayList();
		ArrayList discoArtists = new ArrayList();
		HashMap<Integer, HashSet> results = new HashMap<Integer, HashSet>();
		double th = 0.8;
		//String value = args[0];
		String sql = "select a.id, a.sort_name from artist_sample a, artist_link l where a.id = l.mid and mid<40140";//"select id, sort_name from artist_sample where sort_name like 'John %'";;////"select id, sort_name from artist_sample where id=4";
		String sql2 = "select a.id, a.name from discogs_artist a, artist_link l where a.id=l.gid and mid<40140";//"select a.id, a.name from discogs_artist a where name like 'John %'";//
		String db1 = "musicbrainz";
		String db2 = "discogs";

		getMusicData(sql, db1, musicArtists);
		getMusicData(sql2, db2, discoArtists);

		//getTracks(db1, musicArtists);
		//getTracks(db2, discoArtists);

		//System.out.println("musicartist contains id 275909? \t"+musicArtists.contains(o));
		
		/*
		 * ArtistData a = (ArtistData) musicArtists.get(0);
		System.out.println(a.getTracks().size());
		
		
		a= (ArtistData) discoArtists.get(0);
		System.out.println(a.getTracks().size());
		*/

		for (int i = 0; i < musicArtists.size(); i++) {
			ArtistData musicart = (ArtistData) musicArtists.get(i);
			for (int j = 0; j < discoArtists.size(); j++) {
				ArtistData discoart = (ArtistData) discoArtists.get(j);
				System.out.println("!!! compare records \t" + musicart.getId() + "\t" + discoart.getId());
				double score = 0;//RecordSimilarity.recordSimilarity(musicart, discoart);
				if (score >= th) {
					if (!results.containsKey(musicart.getId())) {
						HashSet set = new HashSet();
						set.add(discoart.getId());
						results.put(musicart.getId(), set);
					} else {
						HashSet set = results.get(musicart.getId());
						set.add(discoart.getId());
						results.put(musicart.getId(), set);
					}
				}
			}
		}

		System.out.println("# match pairs \t" + results.size());
		HashMap<Integer, Integer> truth = new HashMap<Integer, Integer>();
		Evaluation.getGroundTruth(truth);
		double f1 = Evaluation.FMeasure(truth, results, musicArtists, discoArtists);

	}

	public static void getMusicData(String sql, String db, ArrayList artists) throws Exception {

		ArrayList list = new ArrayList();


		Connection con = ConnectionPool.getConnection();
		if (!con.isClosed()) {

			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				artist = new ArtistData();
				int id = result.getInt(1);
				String name = result.getString(2).toLowerCase().trim();
				artist.setId(id);
				artist.setName(name);
				ArrayList tracks = new ArrayList();
				artist.setTracks(tracks);

				artists.add(artists.size(), artist);

			}

			st.close();


			ConnectionPool.putConnection(con);

		}
	}

	public static void getTracks(String db, ArrayList artists) throws Exception {
		Connection con = ConnectionPool.getConnection();
		if (!con.isClosed()) {

			String sql = "";
			if (db.equals("musicbrainz")) {
				sql = MUSICBRAINZ_TRACK;
			} else if (db.equals("discogs"))
				sql = DISCOGS_TRACK;

			// insert label
			PreparedStatement prest = null;
			//con.setAutoCommit(false);
			prest = con.prepareStatement(sql);


			System.out.println(artists.size());

			for (int i = 0; i < artists.size(); i++) {
				ArtistData art = (ArtistData) artists.get(i);
				prest.setInt(1, art.getId());
				System.out.println(i + "\t" + art.getId() + "\t" + art.getName());
				ResultSet tracks = prest.executeQuery();
				ArrayList list = new ArrayList();
				while (tracks.next()) {
					//int track_id = tracks.getInt(1);
					String track = tracks.getString(1);
					list.add(track);
					System.out.println("\t |" + track + "|");
				}


				if (list.isEmpty()) {
					artists.remove(i);
					i--;
					//System.out.println("remove artist \t"+art.getId()+"\t"+artists.size());
				} else {
					art.setTracks(list);
				}

			}

			if (prest != null) {
				prest.close();
			}

		}
		ConnectionPool.putConnection(con);
	}

}
