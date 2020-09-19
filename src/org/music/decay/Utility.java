package org.music.decay;

import org.music.block.AbstractBlock;
import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Utility {


	public static String getClusters(String sql) throws Exception {
		// TODO Auto-generated method stub
		String clusters = "";

		Connection con = ConnectionPool.getConnection();
		HashSet set = new HashSet();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			//System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release, genres, style, country, date, name, cluster_id 
			while (result.next()) {
				i++;
				String name = result.getString(1);
				int cluster_id = result.getInt(2);
				set.add(cluster_id);

				//System.out.println(i+"\t"+name+"\t"+cluster_id+"\t"+clusters);

			}

			System.out.println("decay entity size \t" + set.size());
			Iterator it = set.iterator();
			if (it.hasNext()) {
				clusters += String.valueOf((Integer) it.next());
			}
			while (it.hasNext()) {
				int id = (Integer) it.next();
				clusters += ", " + String.valueOf(id);

			}


			st.close();
		}

		ConnectionPool.putConnection(con);

		return clusters;
	}

	public ArrayList getData(String clusters, String entity) throws Exception {

		String sql = "";

		if (entity.equals("label"))
			sql = "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, l.id as cluster_id " +
					"from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac, discogs_release_label rl, discogs_label l " +
					"where ra.release_id=r.id and ra.name = ac.artist and date <>'' and r.id=rl.release_id and rl.label_name = l.name " +
					"and l.id in (" + clusters + ") order by l.id, date";
		else if (entity.equalsIgnoreCase("releaselabel"))
			sql = "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id " +
					"from discogs_release r, discogs_release_artist ra, discogs_release_label rl, discogs_label l, discogs_release_format rf, discogs_release_extraartist re " +
					"where ra.release_id=r.id  and date <>'' and rf.release_id = r.id and re.release_id = r.id and r.id=rl.release_id and rl.label_name = l.name and l.id in (" + clusters + ") and date<>'?'  order by l.id, date, catno";


		//sql = "select id, release, genres, track, country, year, name, cluster_id from dr_evil where id<>11 order by cluster_id, year";

		ArrayList data = new ArrayList();
		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			if (entity.equals("label")) {
				//select id, release, genres, style, country, date, artist, label_name, cluster_id
				while (result.next()) {
					i++;
					int id = result.getInt(1);
					String release = result.getString(2);
					release = AbstractBlock.cleanValue(release);
					String genres = result.getString(3);
					String style = result.getString(4);
					String country = result.getString(5);
					country = AbstractBlock.cleanValue(country);
					String date = result.getString(6);
					String artist = result.getString(7);
					artist = AbstractBlock.cleanValue(artist);
					String name = result.getString(8);
					name = AbstractBlock.cleanValue(name);

					int cluster_id = result.getInt(9);
					//System.out.println(i+"\t"+id+"\t"+name+"\t"+release+"\t"+genres+"\t"+style+"\t"+country+"\t"+date+"\t"+cluster_id);
					ArrayList genreslist = new ArrayList();
					genreslist = convert2List(genres, genreslist, "\\|");
					ArrayList stylelist = new ArrayList();
					stylelist = convert2List(style, stylelist, "\\|");
					String year = "";
					year = convert2Year(date, year, "-");

					LabelData label = new LabelData();
					label.setId(id);
					label.setRelease(release);
					label.setGenres(genreslist);
					label.setArtist(artist);
					label.setStyle(stylelist);
					label.setCountry(country);
					label.setDate(year);
					label.setName(name);
					label.setCluster_id(cluster_id);

					data.add(data.size(), label);

				}
			} else if (entity.equals("releaselabel")) {
				//select id, release_name, genres, styles, country, date, artist_name, label_name, catalog_num, format, format_qty, format_description, extra_artist, cluster_id
				// 		 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10,			11				12				13			14
				while (result.next()) {
					i++;

					int id = result.getInt(1);
					String release = result.getString(2);
					String genres = result.getString(3);
					String styles = result.getString(4);
					String country = result.getString(5);
					String year = result.getString(6);
					year = year.substring(0, 4);
					String artist = result.getString(7);
					String label = result.getString(8);
					String catno = result.getString(9);
					String format = result.getString(10);
					String qty = result.getString(11);
					String description = result.getString(12);
					String extra_artist = result.getString(13);
					int cluster_id = result.getInt(14);

					release = AbstractBlock.cleanValue(release);
					country = AbstractBlock.cleanValue(country);
					artist = AbstractBlock.cleanValue(artist);
					label = AbstractBlock.cleanValue(label);
					extra_artist = AbstractBlock.cleanValue(extra_artist);

					ArrayList genreslist = new ArrayList();
					genreslist = convert2List(genres, genreslist, "\\|");
					ArrayList stylelist = new ArrayList();
					stylelist = convert2List(styles, stylelist, "\\|");

					//System.out.println(i+"\t"+label+"\t"+release+"\t"+year+"\t"+artist+"\t"+cluster_id);

					ReleaseLabel relabel = new ReleaseLabel();
					relabel.setId(id);
					relabel.setRelease(release);
					relabel.setGenreslist(genreslist);
					relabel.setStrartist(artist);
					relabel.setStyleslist(stylelist);
					relabel.setCountry(country);
					relabel.setDate(Integer.valueOf(year));
					relabel.setLabel(label);
					relabel.setCluster_id(cluster_id);
					relabel.setCatno(catno);
					relabel.setFormat(format);
					relabel.setQty(qty);
					relabel.setFormat_description(description);
					relabel.setStrextrartist(extra_artist);
					data.add(data.size(), relabel);

				}
			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		System.out.println("decay reocrd size \t" + data.size());

		return data;
	}

	public static ArrayList queryRecords(String clusters) throws Exception {

		String sql = "select r.id, r.title as release, r.genres, r.styles, r.country, date, ra.name, ac.cluster_id "
				+ "from discogs_release r, discogs_release_artist ra, discogs_artist_cluster ac where ra.release_id=r.id and ra.name = ac.artist and date <>'' "
				+ "and ac.cluster_id in (" + clusters + ") order by ac.cluster_id, date";

		//sql = "select id, release, genres, track, country, year, name, cluster_id from dr_evil where id<>11 order by cluster_id, year";

		ArrayList artists = new ArrayList();
		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release, genres, style, country, date, name, cluster_id 
			while (result.next()) {
				i++;
				int id = result.getInt(1);
				String release = result.getString(2);
				release = AbstractBlock.cleanValue(release);
				String genres = result.getString(3);
				String style = result.getString(4);
				String country = result.getString(5);
				country = AbstractBlock.cleanValue(country);
				String date = result.getString(6);
				String name = result.getString(7);
				name = AbstractBlock.cleanValue(name);
				int cluster_id = result.getInt(8);
				//System.out.println(i+"\t"+id+"\t"+name+"\t"+release+"\t"+genres+"\t"+style+"\t"+country+"\t"+date+"\t"+cluster_id);
				ArrayList genreslist = new ArrayList();
				genreslist = convert2List(genres, genreslist, "\\|");
				ArrayList stylelist = new ArrayList();
				stylelist = convert2List(style, stylelist, "\\|");
				String year = "";
				year = convert2Year(date, year, "-");

				ArtistData artist = new ArtistData();
				artist.setId(id);
				artist.setRelease(release);
				artist.setGenres(genreslist);
				artist.setStyle(stylelist);
				artist.setCountry(country);
				artist.setDate(year);
				artist.setName(name);
				artist.setCluster_id(cluster_id);

				artists.add(artists.size(), artist);

			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		System.out.println("decay reocrd size \t" + artists.size());

		return artists;

	}

	private static ArrayList convert2List(String str, ArrayList list, String splt) {
		// TODO Auto-generated method stub
		//System.out.println(str);
		String[] split = str.split(splt);
		for (int i = 0; i < split.length; i++) {
			String title = split[i];
			title = AbstractBlock.cleanValue(title);
			//System.out.println("\t"+i+"\t"+title);
			if (!title.equals("")) {
				list.add(title);
			}

		}
		return list;
	}

	private static String convert2Year(String str, String yr, String splt) {
		// TODO Auto-generated method stub

		String[] split = str.split(splt);
		yr = split[0];
		//System.out.println(str+"\t"+yr);
		return yr;
	}


}
