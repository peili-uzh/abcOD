package org.music.abcod;

import org.music.block.AbstractBlock;
import org.music.connection.ConnectionPool;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProcessor {

	public ArrayList labels;
	public ArrayList relabels;
	public ArrayList artists;
	public HashMap<Integer, Integer> clusterlist;

	public ArrayList getLabels() {
		return labels;
	}

	public void setLabels(ArrayList labels) {
		this.labels = labels;
	}

	public ArrayList getRelabels() {
		return relabels;
	}

	public void setRelabels(ArrayList relabels) {
		this.relabels = relabels;
	}

	public ArrayList getArtists() {
		return artists;
	}

	public void setArtists(ArrayList artists) {
		this.artists = artists;
	}

	public HashMap<Integer, Integer> getClusterlist() {
		return clusterlist;
	}

	public void setClusterlist(HashMap<Integer, Integer> clusterlist) {
		this.clusterlist = clusterlist;
	}

	public AbstractProcessor(String sql, String entity) throws Exception {
		if (entity.equals("label")) {

			this.labels = queryLabels(sql);
			System.out.println("++++ record size \t" + this.labels.size());
		} else {
			if (entity.equalsIgnoreCase("releaselabel")) {
				this.relabels = queryReleaseLabel("releaselabel", sql);
			}
			if (entity.equalsIgnoreCase("cars")) {
				this.relabels = queryCar(sql);
			}
		}

	}

	private ArrayList<ReleaseLabel> queryCar(String sql) throws Exception {
		ArrayList<ReleaseLabel> records = new ArrayList<ReleaseLabel>();
		LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();
		this.clusterlist = new HashMap<Integer, Integer>();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				int id = result.getInt("id");
				String carTitle = result.getString("title");
				String cartype = result.getString("cartype");
				String country = "";
				String year = result.getString("year");
				if (year.equalsIgnoreCase("") || year == null)
					year = "0";

				String[] yearSplit = year.split("/");
				year = yearSplit[yearSplit.length - 1];

				String brand = result.getString("brand").replace("(", "");
				String vin = result.getString("vin").replaceAll(" ", "").replace("/", "").replace("-", "").replace("–",
						"");
				vin = cleanCatno(vin);

				String model = result.getString("model");
				String description = "";// result.getString("description");
				description = sortStrings(description);

				int cluster_id = 100;// result.getInt("id");
				int rid = result.getInt("id");
				String new_year = "";
				new_year = parseDateToYear(new_year);

				String ground_truth = result.getString("true_year");
				ground_truth = parseDateToYear(ground_truth);

				int block_id = result.getInt("block_id");
				int order_id = result.getInt("order_id");
				int partition_id = block_id + result.getInt("model");

				/*
				 * if (!year.equals("0")) { ground_truth = "0"; }
				 */


				if (temp.containsKey(id)) {

				} else {
					ReleaseLabel relabel = new ReleaseLabel();
					relabel.setId(id);
					relabel.setRelease(carTitle);
					relabel.setGenreslist(new ArrayList());
					relabel.setStyleslist(new ArrayList());
					relabel.setCountry(country);
					relabel.setDate(Integer.valueOf(year));
					relabel.setLabel(brand);
					relabel.setStrartist("");
					relabel.setCluster_id(cluster_id);
					relabel.setStrextrartist("");
					relabel.setFormat(model);
					relabel.setQty("");
					relabel.setFormat_description(description);
					relabel.setCatno(vin);
					relabel.setPartition_id(partition_id);
					relabel.setNew_date(Integer.valueOf(new_year));
					relabel.setNew_country("");
					relabel.setNew_release("");
					relabel.setNew_label("");
					relabel.setNew_catno("");
					relabel.setNew_format("");
					relabel.setGround_truth(Integer.valueOf(ground_truth));

					relabel.setBlockID(block_id);
					relabel.setOrderID(order_id);

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
			// System.out.println(relabel.getLabel() + "\t" + id + "\t" +
			// relabel.getLabel() + "\t" + relabel.getRelease() + "\t" +
			// relabel.getDate() + "\t" + relabel.getCatno() + "\t" +
			// relabel.getFormat() + "\t" + relabel.getOrderID());
			records.add(records.size(), relabel);
		}
		System.out.println("car size \t" + records.size());
		return records;
	}

	private ArrayList queryReleaseLabel(String labels, String sql) throws Exception {
		// TODO Auto-generated method stub

		ArrayList records = new ArrayList();
		LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();
		this.clusterlist = new HashMap<Integer, Integer>();

		Connection con = ConnectionPool.getConnection();
//		System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));


		if (!con.isClosed()) {
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			// select id, release_name, genres, styles, country, date,
			// artist_name, label_name, catalog_num, format, format_qty,
			// format_description, extra_artist, cluster_id, rid, partition_id,
			// new_date, new_country, new_title, new_label, new_catno,
			// new_format
			// 1, 2, 3, 4, 5, 6, 7, 8, 9 10, 11 12 13 14 15 16 17 18 19 20 21 22
			int i = 0;

			while (result.next()) {
                int id = i;//result.getInt("id");
                String release = result.getString("release_1");
                // String release = result.getString("release");
                String genres = "";// result.getString("genres");
                String styles = "";// result.getString("styles");
                String country = "";// result.getString("country");
                String year = result.getString("date");
                year = parseDateToYear(year);
                double parsedYear = Double.parseDouble(year);

                String artist = "";// result.getString("artist");
                String label = result.getString("label_name");
                String catno = result.getString("catno");
                catno = cleanCatno(catno);

                String format = "";// result.getString("format");
                String qty = "";// result.getString("qty");
                // format += " "+qty;
                String description = "";// result.getString("description");
				description = sortStrings(description);
				// format += " "+description;
				String extra_artist = "";// result.getString("extra_artist");
				int cluster_id = label.hashCode();// this is the label id:
				// result.getInt("cluster_id");
				// int rid = result.getInt("release_id");
				// this is the series id
				int partition_id = 0;// result.getInt("partition_id");
				String new_year = "";// result.getString("new_date");
				new_year = parseDateToYear(new_year);

				String ground_truth = "2999";// result.getString("ground_truth");//
				// "2016";//
				ground_truth = parseDateToYear(ground_truth);

				// System.out.println(i + "\t" + catno + "\t" + year);
				// System.out.println(catno);

				/*
				 * if (!year.equals("0")) { ground_truth = "0"; }
				 */

				ArrayList genreslist = new ArrayList();
				genreslist = convert2List(genres, genreslist, "\\|");
				ArrayList stylelist = new ArrayList();
				if (styles != null) {
					stylelist = convert2List(styles, stylelist, "\\|");
				}

				if (!year.equals("0")) {
					if (temp.containsKey(id)) {
						ReleaseLabel relabel = temp.get(id);
						String artists = relabel.getStrartist();
						if (artist != null && !artists.contains(artist)) {
							artists += "|" + artist;
							relabel.setStrartist(artists);
						}

						String extra_artists = relabel.getStrextrartist();
						if (extra_artist != null && !extra_artists.contains(extra_artist)) {
							extra_artists += "|" + extra_artist;
							relabel.setStrextrartist(extra_artists);
						}
						/*
						 * ArrayList artists = releaselabel.getArtist();
						 * if(!artists.contains(artist)) artists.add(artist);
						 * ArrayList extra_artists = releaselabel.getExtra_artist();
						 * if(!extra_artists.contains(extra_artist))
						 * extra_artists.add(extra_artist);
						 */
					} else {
                        ReleaseLabel relabel = new ReleaseLabel();
                        relabel.setId(id);
                        relabel.setRelease(release);
                        relabel.setGenreslist(genreslist);
                        relabel.setStyleslist(stylelist);
                        relabel.setCountry(country);
                        relabel.setDate((int) parsedYear); //relabel.setDate(Integer.parseInt(year));
                        relabel.setLabel(label);
                        relabel.setStrartist(artist);
                        relabel.setCluster_id(cluster_id);
                        relabel.setStrextrartist(extra_artist);
                        relabel.setFormat(format);
                        relabel.setQty(qty);
                        relabel.setFormat_description(description);
                        relabel.setCatno(catno);
                        relabel.setPartition_id(partition_id);
                        relabel.setNew_date(Integer.parseInt(new_year));
                        // relabel.setNew_country(new_country);
                        // relabel.setNew_release(new_title);
                        // relabel.setNew_label(new_label);
                        // relabel.setNew_catno(new_catno);
                        // relabel.setNew_format(new_format);
                        relabel.setGround_truth(Integer.valueOf(ground_truth));

                        temp.put(id, relabel);

                    }
				}
				i++;
			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
			int id = e.getKey();
			ReleaseLabel relabel = e.getValue();
			// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getFormat_description()+"\t"+relabel.getQty()+"\t"+relabel.getCluster_id());
			records.add(records.size(), relabel);
		}
		System.out.println("discogs label size \t" + records.size());
//		System.out.println("Memory in MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));

		return records;

	}

	public static int insertCatalogNumber(ArrayList<String> catalist, String catano) {
		// TODO Auto-generated method stub
		Catalog cata = new Catalog(catano);
		String prefix = cata.getPrefix();
		LinkedHashMap<Integer, String> strs = cata.getStrs();
		LinkedHashMap<Integer, Integer> ints = cata.getInts();

		int position = -1;
		boolean stop = false;
		int i = 0;
		// for(Map.Entry<Integer, String> e: catalist.entrySet()){
		while (i < catalist.size() && !stop) {
			String localcatano = catalist.get(i);
			Catalog localcata = new Catalog(localcatano);
			// String prefix = cata.getPrefix();
			LinkedHashMap<Integer, String> localstrs = localcata.getStrs();
			LinkedHashMap<Integer, Integer> localints = localcata.getInts();
			long order = cata.compareCatalog(cata, localcata);
			// System.out.println("\t"+catano+"\t"+localcatano+"\t"+order);
			if (order < 0) {
				stop = true;
				position = i;
			}
			i++;
		}
		return position;
	}

	private String sortStrings(String description) {
		// TODO Auto-generated method stub
		// String temp = "";

		description = description.replace("[", "");
		description = description.replace("]", "");

		ArrayList list = new ArrayList();
		String[] split = description.split(",");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
			int position = 0;
			if (!list.isEmpty()) {
				boolean stop = false;
				int j = 0;
				while (j < list.size() && !stop) {
					String localstr = (String) list.get(j);
					int order = str.compareTo(localstr);
					if (order > 0) {
						stop = true;
						position = j;
					}
					j++;
				}
				if (stop != true)
					position = list.size();
			}
			list.add(position, str);
		}

		// System.out.println(list+"| \t| "+list.toString());
		return list.toString();
	}

	private String cleanCatno(String catno) {
		// TODO Auto-generated method stub

		/*
		 *
		 * catno = catno.replace("-", ""); catno = catno.replace(" ", ""); catno
		 * = catno.replace("/", ""); catno = catno.replace(",", ""); catno =
		 * catno.replace(".", ""); catno = catno.replace("_", ""); catno =
		 * catno.replace(":", "");
		 */

		catno = catno.toUpperCase();
		catno = catno.trim();

		return catno;
	}

	private String parseDateToYear(String year) {
		// -1 means null
		if (year != null) {
			if (year.equals("?") || year.equals("???") || year.equals("????") || year.equals("Unknown")
					|| year.equals("??") || year.equals("None") || year.equals("Not Known") || year.equals("")
					|| year.startsWith(" "))
				year = "0";
			if (year.length() > 4)
				year = year.trim().replace("-", "").substring(0, 4);
		} else {
			year = "0";
		}

		return year;
	}

	public ArrayList queryLabels(String sql) throws Exception {

		ArrayList labels = new ArrayList();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			// select id, release, genres, style, country, date, name,
			// cluster_id
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

				// System.out.println(i+"\t"+id+"\t"+name+"\t"+release+"\t"+artist+"\t"+genres+"\t"+style+"\t"+country+"\t"+date+"\t"+cluster_id);

				i++;

			}

			st.close();
		}

		ConnectionPool.putConnection(con);

		return labels;
	}

	private static ArrayList convert2List(String str, ArrayList list, String splt) {
		// TODO Auto-generated method stub
		// System.out.println(str);
		String[] split = str.split(splt);
		for (int i = 0; i < split.length; i++) {
			String title = split[i];
			title = AbstractBlock.cleanValue(title);
			// System.out.println("\t"+i+"\t"+title);
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
		// System.out.println(str+"\t"+yr);
		return yr;
	}
}
