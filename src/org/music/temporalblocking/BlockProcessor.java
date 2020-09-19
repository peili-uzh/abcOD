package org.music.temporalblocking;

import org.music.block.AbstractBlock;
import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class BlockProcessor {

	private static String SQL = "select  r.id , rn.name, a.name, rc.date_year, ln.name, dr.title, dr.country, dr.date, drl.label_name " +
			"from l_release_url l, release r, url u, release_name rn, release_country rc, discogs_release dr, discogs_release_label drl, release_label rl, label la , label_name ln, area a " +
			"where l.link = '6301' and r.id = entity0 and u.id = entity1 and rn.id = r.name and rc.release=r.id and drl.release_id=dr.id and rl.release=r.id and la.id = rl.label and la.name = ln.id " +
			"and substring(url from 32  for char_length(url)-1) = cast(dr.id as character varying) and rc.country = a.id and drl.label_name = 'Chrysalis'";

	public ArrayList artists;
	public LinkedHashMap<Integer, ArrayList> blocks; // block_id - record ids (blocks are sorted)
	public LinkedHashMap<Integer, ArrayList> blockinvertedlist; // record id - block ids (blocks are sorted)
	public HashMap<String, Integer> blocklist; //  block key - block id
	public HashMap<Integer, String> blockeylist; // blockid - blockey
	public ArrayList labels;
	public ArrayList relabels;
	private HashMap<Integer, String> labelist;
	private String discogs_labels;
	private String musicbz_ids;
	private HashMap<Integer, Integer> clusterlist;


	public LinkedHashMap<Integer, ArrayList> getBlocks() {
		return blocks;
	}


	public void setBlocks(LinkedHashMap<Integer, ArrayList> blocks) {
		this.blocks = blocks;
	}

	public LinkedHashMap<Integer, ArrayList> getBlockinvertedlist() {
		return blockinvertedlist;
	}

	public ArrayList getRelabels() {
		return relabels;
	}

	public void setRelabels(ArrayList relabels) {
		this.relabels = relabels;
	}

	public void setBlockinvertedlist(
			LinkedHashMap<Integer, ArrayList> blockinvertedlist) {
		this.blockinvertedlist = blockinvertedlist;
	}

	public HashMap<String, Integer> getBlocklist() {
		return blocklist;
	}

	public void setBlocklist(HashMap<String, Integer> blocklist) {
		this.blocklist = blocklist;
	}

	public HashMap<Integer, String> getBlockeylist() {
		return blockeylist;
	}

	public void setBlockeylist(HashMap<Integer, String> blockeylist) {
		this.blockeylist = blockeylist;
	}

	public ArrayList getLabels() {
		return labels;
	}

	public void setLabels(ArrayList labels) {
		this.labels = labels;
	}

	public ArrayList getArtists() {
		return artists;
	}

	public void setArtists(ArrayList artists) {
		this.artists = artists;
	}

	public BlockProcessor(String sql, String entity) throws Exception {
		if (entity.equals("label")) {

			this.labels = queryLabels(sql);
			System.out.println("++++ record size \t" + this.labels.size());
		} else if (entity.equalsIgnoreCase("releaselabel")) {


			checkConsistency(SQL);

			System.out.println(musicbz_ids);
			System.out.println("discogs_label \t" + discogs_labels);
			System.out.println(labelist.size());

			ArrayList mblabels = new ArrayList();
			String sql2 = "select * from inter_music_release_full_new where date_year <>0 and id  in (" + musicbz_ids + ")";
			mblabels = getinterRelease(sql2);
			System.out.println("++++ music record size \t" + mblabels.size());


			this.relabels = queryReleaseLabel(discogs_labels);
			System.out.println("++++ record size \t" + this.relabels.size() + "\t" + labelist.size());


			this.relabels = mergeLabels(relabels, mblabels);
			//System.out.println("total label size \t"+relabels.size());
		}

	}

	private void checkConsistency(String sql) throws Exception {
		// TODO Auto-generated method stub
		Connection con = ConnectionPool.getConnection();

		this.labelist = new HashMap<Integer, String>();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release_name, country, year, label_name, discogs_name, discogs_country, discogs_date, discogs_label
			//        1, 	2,			3, 		4, 		5, 			6, 				7, 				8, 				9
			while (result.next()) {
				int id = result.getInt(1);
				String release1 = result.getString(2);
				String country1 = result.getString(3);
				//country = AbstractBlock.cleanValue(country);
				int year1 = result.getInt(4);
				String label1 = result.getString(5);
				String release2 = result.getString(6);
				String country2 = result.getString(7);
				String year2 = result.getString(8);
				String label2 = result.getString(9);
				//String[] split = year2.split("-");


				if (year1 != 0 && !year2.equals("") && !year2.equals("?") && !year2.equals("None")) {
					year2 = year2.substring(0, 4);
					int distance = Math.abs(year1 - Integer.parseInt(year2));
					if (distance > 10 && label1.equals(label2)) {
						System.out.println(i + "\t" + id + "\t" + release1 + "\t" + label1 + "\t" + country1 + "\t" + year1 + "\t" + release2 + "\t" + label2 + "\t" + country2 + "\t" + year2 + "\t" + distance);
						if (i == 0)
							musicbz_ids = String.valueOf(id);
						else
							musicbz_ids += ", " + String.valueOf(id);

						if (i == 0)
							discogs_labels = "'" + label2 + "'";
						else if (!discogs_labels.contains(label2))
							discogs_labels += ", '" + label2 + "'";

						if (!labelist.containsKey(id))
							labelist.put(id, label2);
						i++;
					}

				}

			}

			st.close();
		}

		ConnectionPool.putConnection(con);
	}

	private ArrayList getinterRelease(String sql) throws Exception {
		// TODO Auto-generated method stub
		ArrayList releases = new ArrayList();
		LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();

		Connection con = ConnectionPool.getConnection();
		LinkedHashMap<Integer, ReleaseLabel> records = new LinkedHashMap<Integer, ReleaseLabel>();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);


			//select id, release, country, date, label, format, catalog_number, artist, extra_artist, discogs_id
			// 	 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10
			while (result.next()) {
				int id = result.getInt(1);
				String release = result.getString(2);
				String country = result.getString(3);
				String year = result.getString(4);
				String label = result.getString(5);
				String format = result.getString(6);
				String catno = result.getString(7);
				String artist = result.getString(8);
				String extra = result.getString(9);
				int discogsid = result.getInt(10);
				if (extra == null) {
					extra = "";
				}
				if (format == null)
					format = "";
				if (catno == null)
					catno = "";
				/*
				label = AbstractBlock.cleanValue(label);
				release = AbstractBlock.cleanValue(release);
				artist = AbstractBlock.cleanValue(artist);
				extra = AbstractBlock.cleanValue(extra);
				country = AbstractBlock.cleanValue(country);
				*/

				System.out.println(i + "\t" + id + "\t" + release + "\t" + label + "\t" + country + "\t" + year + "\t" + artist + "\t" + extra + "\t" + catno + "\t" + format + "\t" + discogsid);

				if (temp.containsKey(id)) {
					ReleaseLabel relabel = temp.get(id);
					String artists = relabel.getStrartist();
					if (!artists.contains(artist)) {
						artists += ", " + artist;
						relabel.setStrartist(artists);
					}

					String extra_artists = relabel.getStrextrartist();
					if (!extra_artists.contains(extra)) {
						extra_artists += ", " + extra;
						relabel.setStrextrartist(extra_artists);
					}
				} else {
					ArrayList list = new ArrayList();
					ReleaseLabel releaselabel = new ReleaseLabel();
					releaselabel.setStrartist(artist);
					releaselabel.setCatno(catno);
					releaselabel.setCluster_id(discogsid);
					releaselabel.setCountry(country);
					if (year != null)
						releaselabel.setDate(Integer.parseInt(year));
					releaselabel.setStrextrartist(extra);
					releaselabel.setFormat(format);
					releaselabel.setFormat_description("");
					releaselabel.setGenreslist(list);
					releaselabel.setId(id);
					releaselabel.setLabel(label);
					releaselabel.setQty("");
					releaselabel.setRelease(release);
					releaselabel.setStyleslist(list);

					temp.put(id, releaselabel);
				}

				i++;

			}
			//System.out.println(i);
			st.close();
		}

		ConnectionPool.putConnection(con);

		for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
			int id = e.getKey();
			ReleaseLabel relabel = e.getValue();
			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());	
			releases.add(releases.size(), relabel);
		}
		System.out.println("musicbrainz label \t" + releases.size());
		return releases;
	}

	public String getLabels(String ids) throws Exception {
		// TODO Auto-generated method stub
		String labels = "";

		String sql = "select distinct label_name from discogs_release_label where release_id in (" + ids + ")";

		Connection con = ConnectionPool.getConnection();
		HashSet set = new HashSet();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			//System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release, genres, style, country, date, name, cluster_id 
			while (result.next()) {
				String name = result.getString(1);

				if (i == 0)
					labels = "'" + name + "'";
				else
					labels += ", '" + name + "'";
				i++;

				//System.out.println(i+"\t"+name+"\t"+cluster_id+"\t"+clusters);

			}
			st.close();
		}

		ConnectionPool.putConnection(con);

		return labels;
	}

	private ArrayList mergeLabels(ArrayList relabels, ArrayList mblabels) {
		// TODO Auto-generated method stub
		ArrayList data = new ArrayList();
		//data.addAll(relabels);
		/*
		 * get mblabel cluster_id
		 */
		for (int i = 0; i < mblabels.size(); i++) {
			ReleaseLabel r1 = (ReleaseLabel) mblabels.get(i);
			int id1 = r1.getId();
			int cluster1 = -1;
			//System.out.println("\t mblabels \t"+id1+"\t"+clusterlist.get(id1));
			if (clusterlist.containsKey(id1)) {
				cluster1 = clusterlist.get(id1);
				r1.setCluster_id(cluster1);
				data.add(data.size(), r1);
				//System.out.println(i+"\t add "+id1+"\t"+cluster1+"\t"+r1.getRelease()+"\t"+r1.getDate());
			}


		}

		/*
		 * merge data in time order
		 */
		for (int j = 0; j < data.size(); j++) {
			ReleaseLabel r1 = (ReleaseLabel) data.get(j);
			int yr1 = r1.getDate();
			int cluster1 = r1.getCluster_id();

			int m = 0;
			boolean found = false;
			int position = 0;
			while (m < relabels.size() && !found) {
				ReleaseLabel r2 = (ReleaseLabel) relabels.get(m);
				int yr2 = r2.getDate();
				int cluster2 = r2.getCluster_id();

				if (cluster1 == cluster2) {
					if (yr1 < yr2) {
						found = true;
						//System.out.println("\t"+yr1+"\t"+yr2);
						relabels.add(m, r1);
					} else
						position = m;
				}

				m++;
			}

			if (!found) {
				if (position > 0)
					relabels.add(position + 1, r1);
				else
					relabels.add(relabels.size(), r1);
			}

			System.out.println(relabels.size());
		}

		return relabels;
	}

	private ArrayList queryReleaseLabel(String labels) throws Exception {
		// TODO Auto-generated method stub

		String sql = "select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id, r.id " +
				"from discogs_release r, discogs_release_artist ra, discogs_release_label rl, discogs_label l, discogs_release_format rf, discogs_release_extraartist re " +
				"where ra.release_id=r.id  and date <>'' and rf.release_id = r.id and re.release_id = r.id " +
				"and r.id=rl.release_id and rl.label_name = l.name and   l.name in (" + labels + ") and date<>'?'  order by l.id, date, catno";

		ArrayList records = new ArrayList();
		LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();
		this.clusterlist = new HashMap<Integer, Integer>();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release_name, genres, styles, country, date, artist_name, label_name, catalog_num, format, format_qty, format_description, extra_artist, cluster_id
			// 		 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10,			11				12				13			14	

			while (result.next()) {
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
				format += " " + qty;
				String description = result.getString(12);
				format += " " + description;
				String extra_artist = result.getString(13);
				int cluster_id = result.getInt(14);
				int rid = result.getInt(15);

				for (Map.Entry<Integer, String> e : labelist.entrySet()) {
					int mbid = e.getKey();
					String mblabel = e.getValue();
					//System.out.println("lablist \t"+mbid+"\t"+mblabel);
					if (!clusterlist.containsKey(mbid)) {
						if (mblabel.equals(label)) {
							clusterlist.put(mbid, cluster_id);
							System.out.println("\t" + mbid + "\t" + mblabel + "\t" + cluster_id);
						}
					}
				}
				
				/*
				release = AbstractBlock.cleanValue(release);
				country = AbstractBlock.cleanValue(country);
				artist = AbstractBlock.cleanValue(artist);
				label = AbstractBlock.cleanValue(label);
				extra_artist = AbstractBlock.cleanValue(extra_artist);
				format = format.replace("[", "");
				format = format.replace("]", "");
				//format = AbstractBlock.cleanValue(format);
				styles = AbstractBlock.cleanValue(styles);
				genres = AbstractBlock.cleanValue(genres);
				//catno = AbstractBlock.cleanValue(catno);
				*/

				ArrayList genreslist = new ArrayList();
				genreslist = convert2List(genres, genreslist, "\\|");
				ArrayList stylelist = new ArrayList();
				stylelist = convert2List(styles, stylelist, "\\|");

				if (temp.containsKey(id)) {
					ReleaseLabel relabel = temp.get(id);
					String artists = relabel.getStrartist();
					if (!artists.contains(artist)) {
						artists += ", " + artist;
						relabel.setStrartist(artists);
					}

					String extra_artists = relabel.getStrextrartist();
					if (!extra_artists.contains(extra_artist)) {
						extra_artists += ", " + extra_artist;
						relabel.setStrextrartist(extra_artists);
					}
					/*
					ArrayList artists = releaselabel.getArtist();
					if(!artists.contains(artist))
						artists.add(artist);
					ArrayList extra_artists = releaselabel.getExtra_artist();
					if(!extra_artists.contains(extra_artist))
						extra_artists.add(extra_artist);*/
				} else {
					ReleaseLabel relabel = new ReleaseLabel();
					relabel.setId(id);
					relabel.setRelease(release);
					relabel.setGenreslist(genreslist);
					relabel.setStyleslist(stylelist);
					relabel.setCountry(country);
					relabel.setDate(Integer.valueOf(year));
					relabel.setLabel(label);
					relabel.setStrartist(artist);
					relabel.setCluster_id(cluster_id);
					relabel.setStrextrartist(extra_artist);
					relabel.setFormat(format);
					relabel.setQty(qty);
					relabel.setFormat_description(description);
					relabel.setCatno(catno);

					temp.put(id, relabel);

				}


				i++;

			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
			int id = e.getKey();
			ReleaseLabel relabel = e.getValue();
			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());	
			records.add(records.size(), relabel);
		}
		//System.out.println("discogs label size \t"+records.size());
		return records;

	}

	public BlockProcessor(String sql) throws Exception {

		this.artists = queryRecords(sql);
		System.out.println("++++ record size \t" + this.artists.size());

	}

	public void processEntity(Block block, String entityType) {
		blocks = new LinkedHashMap<Integer, ArrayList>();
		blockinvertedlist = new LinkedHashMap<Integer, ArrayList>();
		blocklist = new HashMap<String, Integer>();
		blockeylist = new HashMap<Integer, String>();

		if (entityType.equalsIgnoreCase("label"))
			block.processEntity(entityType, labels, blocks, blockinvertedlist, blocklist, blockeylist);

		else if (entityType.equalsIgnoreCase("releaselabel"))
			block.processEntity(entityType, relabels, blocks, blockinvertedlist, blocklist, blockeylist);
	}

	public void process(Block block) {

		blocks = new LinkedHashMap<Integer, ArrayList>();
		blockinvertedlist = new LinkedHashMap<Integer, ArrayList>();
		blocklist = new HashMap<String, Integer>();
		blockeylist = new HashMap<Integer, String>();


		block.process(artists, blocks, blockinvertedlist, blocklist, blockeylist);

	}

	public ArrayList queryLabels(String sql) throws Exception {

		ArrayList labels = new ArrayList();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release, genres, style, country, date, name, cluster_id 
			while (result.next()) {
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
				label.setStyle(stylelist);
				label.setCountry(country);
				label.setDate(year);
				label.setName(name);
				label.setArtist(artist);
				label.setCluster_id(cluster_id);


				labels.add(labels.size(), label);

				//System.out.println(i+"\t"+id+"\t"+name+"\t"+release+"\t"+artist+"\t"+genres+"\t"+style+"\t"+country+"\t"+date+"\t"+cluster_id);

				i++;

			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		return labels;

	}

	public ArrayList queryRecords(String sql) throws Exception {

		ArrayList artists = new ArrayList();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release, genres, style, country, date, name, cluster_id 
			while (result.next()) {
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

				int index = findPosition(artists, year);


				//artists.add(index, artist);

				artists.add(artists.size(), artist);

				//System.out.println(i+"\t"+id+"\t"+name+"\t"+release+"\t"+genres+"\t"+style+"\t"+country+"\t"+date+"\t"+cluster_id);

				i++;

			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		return artists;

	}

	private int findPosition(ArrayList artists, String year) {
		// TODO Auto-generated method stub
		int i = 0;
		if (!artists.isEmpty()) {
			boolean found = false;
			while (!found && i < artists.size()) {
				ArtistData artist = (ArtistData) artists.get(i);
				int year2 = Integer.valueOf(artist.getDate());
				if (year2 > Integer.valueOf(year))
					found = true;

				else
					i++;
			}
		}

		return i;
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
