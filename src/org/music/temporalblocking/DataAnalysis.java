package org.music.temporalblocking;

import com.wcohen.secondstring.Levenstein;
import org.music.abcod.Catalog;
import org.music.block.AbstractBlock;
import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class DataAnalysis {

	private static String SQL = "select  r.id , rn.name, a.name, rc.date_year, ln.name, dr.title, dr.country, dr.date, drl.label_name " +
			"from l_release_url l, release r, url u, release_name rn, release_country rc, discogs_release dr, discogs_release_label drl, release_label rl, label la , label_name ln, area a " +
			"where l.link = '6301' and r.id = entity0 and u.id = entity1 and rn.id = r.name and rc.release=r.id and drl.release_id=dr.id and rl.release=r.id and la.id = rl.label and la.name = ln.id " +
			"and substring(url from 32  for char_length(url)-1) = cast(dr.id as character varying) and rc.country = a.id and drl.label_name='FatCat Records'";

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

	public DataAnalysis(String sql, String entity) throws Exception {
		if (entity.equals("label")) {

			this.labels = queryLabels(sql);
			System.out.println("++++ record size \t" + this.labels.size());
		} else if (entity.equalsIgnoreCase("releaselabel")) {
			
			/*
			checkConsistency(SQL);
			
			System.out.println(musicbz_ids);
			System.out.println("discogs_label \t"+discogs_labels);
			System.out.println(labelist.size());
			
			ArrayList mblabels = new ArrayList();
			String sql2 = "select * from inter_music_release_full_new where date_year <>0 and id  in ("+musicbz_ids+")"; 
			mblabels = getinterRelease(sql2);
			System.out.println("++++ music record size \t"+mblabels.size());
			*/

			this.relabels = queryReleaseLabel(discogs_labels);
			//System.out.println("++++ record size \t"+this.relabels.size()+"\t"+labelist.size());


			//this.relabels = mergeLabels(relabels, mblabels);
			//System.out.println("total label size \t"+relabels.size());
		}

	}

	public void checkSubDistribution(String entityType, ArrayList dataset) {
		int current_entity = 0;
		String current_label = "";
		HashMap<String, ArrayList> list = new HashMap<String, ArrayList>();
		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				String country = relabel.getCountry();
				int clusterid = relabel.getCluster_id();
				ArrayList genres = relabel.getGenreslist();
				genres = relabel.getStyleslist();
				//System.out.println(genres);

				//String value = country;
				//String artists = relabel.getStrartist();
				//String artists = relabel.getStrextrartist();
				//String[] split = artists.split("\\|");
				//for(int m=0; m<split.length; m++){
				//System.out.println(relabel.getGenreslist());

				//for(int m=0; m< genres.size(); m++){
				//System.out.println(i+"\t"+clusterid+"\t"+artists+"\t"+year);
				//String artist = split[m];
				//System.out.println("\t\t\t"+artist);
				//String value = artist;

				//String genre = (String) genres.get(m);
				String value = relabel.getFormat();
				System.out.println(value);

				if (clusterid != current_entity) {
					printSubDistribution(list, current_label);
					current_entity = clusterid;
					current_label = label;
					list = new HashMap<String, ArrayList>();
					updateList(list, value, year);
					System.out.println();
					System.out.println(i + "\t" + clusterid + "\t" + label);
				} else {
					updateList(list, value, year);
				}
				//}


			}
		}

		if (!list.isEmpty())
			printSubDistribution(list, current_label);
	}

	private void updateList(HashMap<String, ArrayList> list, String value,
							int year) {
		// TODO Auto-generated method stub
		ArrayList years = new ArrayList();
		if (list.containsKey(value))
			years = list.get(value);
		years.add(years.size(), year);
		list.put(value, years);
		//System.out.println("\t"+value+"\t"+years);
	}


	private void printSubDistribution(HashMap<String, ArrayList> list, String current_label) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList> e : list.entrySet()) {
			String value = e.getKey();
			ArrayList years = e.getValue();
			if (years.size() > 1) {
				System.out.println();
				System.out.println("\t" + value + "\t" + years.size() + "\t" + current_label);
				//System.out.println();
				LinkedHashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
				int max = 0;
				int min = 3000;
				for (int i = 0; i < years.size(); i++) {
					int year = (Integer) years.get(i);
					if (max < year)
						max = year;
					if (min > year)
						min = year;
					int count = 0;
					if (temp.containsKey(year))
						count = temp.get(year);
					count += 1;
					//System.out.println("\t\t|"+year+"|\t"+count+"\t"+min+"\t"+max);
					temp.put(year, count);
				}
				//System.out.println(temp.size());
				System.out.println("\t\t" + "count");
				for (int i = min; i <= max; i++) {
					int count = 0;
					if (temp.containsKey(i))
						count = temp.get(i);
					System.out.println("\t" + i + "\t" + count);
				}
			}

		}

	}

	public void partitionByCatalog(String entityType, ArrayList dataset) {
		int current_entity = 0;
		String previous_prefix = "";

		HashMap<String, LinkedHashMap<Integer, String>> catalists = new HashMap<String, LinkedHashMap<Integer, String>>();
		HashMap<String, LinkedHashMap<Integer, Integer>> timelists = new HashMap<String, LinkedHashMap<Integer, Integer>>();
		HashMap<String, LinkedHashMap<Integer, ReleaseLabel>> releaselists = new HashMap<String, LinkedHashMap<Integer, ReleaseLabel>>();

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				Catalog cata = new Catalog(catano);
				String prefix = cata.getPrefix();
				LinkedHashMap<Integer, String> strs = cata.getStrs();
				LinkedHashMap<Integer, Integer> ints = cata.getInts();


				if (clusterid != current_entity) {

					printList(catalists, timelists, releaselists);

					System.out.println(i + "\t" + label);

					current_entity = clusterid;
					current_entity = clusterid;
					catalists = new HashMap<String, LinkedHashMap<Integer, String>>();
					timelists = new HashMap<String, LinkedHashMap<Integer, Integer>>();
					releaselists = new HashMap<String, LinkedHashMap<Integer, ReleaseLabel>>();
					System.out.println(i + "\t" + label + "\t" + clusterid);
					System.out.println();
				}

				/*
				 * sort in catalog numeric order
				 */
				boolean stop = false;
				int position = -1;
				LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog pairs in catalog order
				LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time pairs in catalog order
				LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id - record pairs in catalog order

				if (catalists.containsKey(prefix) && timelists.containsKey(prefix) && releaselists.containsKey(prefix)) {
					catalist = catalists.get(prefix);
					timelist = timelists.get(prefix);
					releaselist = releaselists.get(prefix);

					position = insertCatalogNumber(catalist, catano);

				}

				if (position == -1) {
					position = timelist.size();
				}

				timelist.put(position, year);
				catalist.put(position, catano);
				releaselist.put(position, relabel);

				timelists.put(prefix, timelist);
				catalists.put(prefix, catalist);
				releaselists.put(prefix, releaselist);

			}

			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());


		}

		printList(catalists, timelists, releaselists);

	}

	public void partitionWithGap(String entityType, ArrayList dataset) {
		int current_entity = 0;
		String previous_prefix = "";

		HashMap<String, LinkedHashMap<Integer, String>> catalists = new HashMap<String, LinkedHashMap<Integer, String>>();
		HashMap<String, LinkedHashMap<Integer, Integer>> timelists = new HashMap<String, LinkedHashMap<Integer, Integer>>();
		HashMap<String, LinkedHashMap<Integer, ReleaseLabel>> releaselists = new HashMap<String, LinkedHashMap<Integer, ReleaseLabel>>();

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				Catalog cata = new Catalog(catano);
				String prefix = cata.getPrefix();
				LinkedHashMap<Integer, String> strs = cata.getStrs();
				LinkedHashMap<Integer, Integer> ints = cata.getInts();


				if (clusterid != current_entity) {

					gapPartition(catalists, timelists, releaselists);

					//printList(catalists, timelists, releaselists);

					System.out.println("label: \t" + label);

					current_entity = clusterid;
					current_entity = clusterid;
					catalists = new HashMap<String, LinkedHashMap<Integer, String>>();
					timelists = new HashMap<String, LinkedHashMap<Integer, Integer>>();
					releaselists = new HashMap<String, LinkedHashMap<Integer, ReleaseLabel>>();
					//System.out.println(i+"\t"+label+"\t"+clusterid);
					System.out.println();
				}

				/*
				 * sort in catalog numeric order
				 */
				boolean stop = false;
				int position = -1;
				LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog pairs in catalog order
				LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time pairs in catalog order
				LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id - record pairs in catalog order

				if (catalists.containsKey(prefix) && timelists.containsKey(prefix) && releaselists.containsKey(prefix)) {
					catalist = catalists.get(prefix);
					timelist = timelists.get(prefix);
					releaselist = releaselists.get(prefix);

					position = insertCatalogNumber(catalist, catano);

				}

				if (position == -1) {
					position = timelist.size();
				}

				timelist.put(position, year);
				catalist.put(position, catano);
				releaselist.put(position, relabel);

				timelists.put(prefix, timelist);
				catalists.put(prefix, catalist);
				releaselists.put(prefix, releaselist);

			}

			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());


		}

		//printList(catalists, timelists, releaselists);

	}

	private void gapPartition(
			HashMap<String, LinkedHashMap<Integer, String>> catalists,
			HashMap<String, LinkedHashMap<Integer, Integer>> timelists,
			HashMap<String, LinkedHashMap<Integer, ReleaseLabel>> releaselists) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, LinkedHashMap<Integer, String>> element : catalists.entrySet()) {
			String prefix = element.getKey();
			LinkedHashMap<Integer, String> catalist = element.getValue();
			LinkedHashMap<Integer, Integer> timelist = timelists.get(prefix);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = releaselists.get(prefix);

			if (catalist.size() > 1) {
				System.out.println("\t" + "prefix" + "\t" + prefix);
				System.out.println();

				/*
				 * use the whole dataset to filter
				 */
				//standardPartition(catalist, timelist, releaselist);

				/*
				 * local partition with window size = 10
				 */
				int k = 10;
				//localStardardPartition(catalist, timelist, releaselist, k);
			
			/*
			 * find longest increasing sequence
			 
			System.out.println("\t id \t label \t release \t artist \t credited artists \t genres \t stlyes \t country \t year \t catalog \t format \t quantity \t format description \t label id \t partition id");*/

				findLIS(timelist, catalist, releaselist);


				printMatrix(catalist, timelist);
			}


		}
	}


	private void findLIS(LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub

		int n = timelist.size();
		/*
		 * 1. a size array to keep track of the longest LIS ending with current position
		 * 2. an accordingly string array to keep track of the path for printing out
		 * 3. index array list to keep the entry index of each path
		 */
		String[] paths = new String[n];
		int[] sizes = new int[n];
		ArrayList<ArrayList> indexs = new ArrayList<ArrayList>();

		/*
		 * 1. assign the initial values to each path/size, by setting size to 1 and path equal to the value, i.e., initially each path starting/ending with its current position
		 */
		for (int i = 0; i < n; i++) {
			sizes[i] = 1;
			paths[i] = timelist.get(i) + " ";
			//System.out.println("\t"+i+"\t"+timelist.get(i));
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(list.size(), i);
			indexs.add(i, list);
		}
		//System.out.println("indexs \t"+indexs);

		// before starting the loop, define a support variable maxLength to keep track
		int maxLength = 1;

		for (int i = 1; i < n; i++) { // loop starts from 2nd position
			for (int j = 0; j < i; j++) {
				// check if appending current index to the previous subsequence: 1 current > previous ending, and size is increasing
				if (timelist.get(i) >= timelist.get(j) && sizes[i] < sizes[j] + 1) {
					// if yes, update sizes and path
					sizes[i] = sizes[j] + 1;
					paths[i] = paths[j] + timelist.get(i) + " ";
					ArrayList<Integer> list = indexs.get(j);
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.addAll(list);
					temp.add(temp.size(), i);
					indexs.remove(i);
					indexs.add(i, temp);
					//System.out.println("\t\t"+paths[i]+"\t"+indexs);

					// append current values to end
					// update maxLength if necessary
					if (maxLength < sizes[i])
						maxLength = sizes[i];
				}
			}

		}
	
	/*
	 * print out records
	 
	for(int i=0; i<n; i++){
		ReleaseLabel relabel = releaselist.get(i);
		System.out.println("\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());	
		
	}*/

		// scan size array again to print out path when size matches MaxLength
		for (int i = 0; i < n; i++) {
			if (sizes[i] == maxLength) {
				System.out.println("LIS \t" + paths[i]);
				System.out.println("LIS index \t" + indexs.get(i));
				errPrintLIS(releaselist, indexs.get(i));
				System.out.println();
			}

		}
	}


	private void errPrintLIS(LinkedHashMap<Integer, ReleaseLabel> releaselist,
							 ArrayList indexs) {
		// TODO Auto-generated method stub
		System.out.println("\t release size \t" + releaselist.size());
		System.out.println("\t id \t label \t release \t artist \t country \t year \t catalog \t format \t label id \t partition id \t error_year");

		for (Map.Entry<Integer, ReleaseLabel> e : releaselist.entrySet()) {
			int index = e.getKey();
			ReleaseLabel relabel = e.getValue();
			if (indexs.contains(index))
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getCluster_id() + "\t" + relabel.getPartition_id() + "\t");
			else
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getCluster_id() + "\t" + relabel.getPartition_id() + "\t F");


		}
	/*
	for(int i=0; i< releaselist.size(); i++){
		ReleaseLabel relabel = releaselist.get(i);
		//if(indexs.contains(i))
			//System.out.println(i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());	
		//else
			System.err.println(i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());	
			/*
			System.out.println(i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());	
		else
			System.err.println(i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());	
		
	}*/
	}


	private void localStardardPartition(LinkedHashMap<Integer, String> catalist,
										LinkedHashMap<Integer, Integer> timelist,
										LinkedHashMap<Integer, ReleaseLabel> releaselist, int k) {
		// TODO Auto-generated method stub
		/*
		 * collect k records
		 */
		LinkedHashMap<Integer, String> tempcatalist = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, Integer> temptimelist = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, ReleaseLabel> tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();
		int count = 0;

		for (int i = 0; i < catalist.size(); i++) {
			String catano = catalist.get(i);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(i);
			int year = timelist.get(i);

			tempcatalist.put(tempcatalist.size(), catano);
			temptimelist.put(temptimelist.size(), year);
			tempreleaselist.put(tempreleaselist.size(), relabel);
			count++;

			if (count % k == 0) {
				standardPartition(tempcatalist, temptimelist, tempreleaselist);
				count = 0;
				i = i - 1;
				tempcatalist.clear();
				temptimelist.clear();
				tempreleaselist.clear();
			}
		}

		standardPartition(tempcatalist, temptimelist, tempreleaselist);

	}


	private void standardPartition(LinkedHashMap<Integer, String> catalist,
								   LinkedHashMap<Integer, Integer> timelist,
								   LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		/*
		 * scan the catalist to collect gap between every two catalog no.
		 * the gap is sorted and strored in arraylist
		 */
		ArrayList gaplist = new ArrayList();
		Catalog previous_cat = new Catalog();
		if (catalist.size() > 1) {
			for (int index = 0; index < catalist.size(); index++) {
				String catano = catalist.get(index);
				ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

				Catalog cat = new Catalog(catano);

				long order = -1;
				LinkedHashMap<Integer, Integer> ints1 = previous_cat.getInts();
				LinkedHashMap<Integer, Integer> ints2 = cat.getInts();

				if (ints1 != null && ints2 != null) {

					long value1 = cat.parseIntoInt(ints1);
					long value2 = cat.parseIntoInt(ints2);

					order = value2 - value1;

					//System.out.println("\t"+value1+"\t"+value2+"\t"+order);

					/*
					 * sort insert into arraylist
					 */
					sortInsert(order, gaplist);

					/*
					 * simply add gaps without sorting
					 */
					//gaplist.add(gaplist.size(), order);
				}

				//System.out.println("\t"+catano+"\t"+order);
				previous_cat = cat;


			}
			System.out.println("gap list: \t" + gaplist);


			long filter = 0;
	/*
	 * get the average gap, excluding top-k values
	 
	
	int k=3;
	filter = getAvgOrder(gaplist, k);
	System.out.println("avg order \t"+avgOrder);*/

			/*
			 * filter w. cutoff c = mean + 3 * deviation
			 */
			//filter = getESD(gaplist);

			/*
			 * filter w. cutoff c = median + 3 * deviation
			 */
			//filter = getMedianCutoff(gaplist);

			/*
			 * filter w. truncated mean; discard top and bottom k portion of records
			 */
			double k = .025;
			filter = getTruncatedCutoff(gaplist, k);


			filter = Math.max(filter, 100);
			fliterWithGap(filter, catalist, releaselist, timelist);

		}
	}


	private long getTruncatedCutoff(ArrayList<Long> gaplist, double k) {
		// TODO Auto-generated method stub
		long filter = 0;

		int size = gaplist.size();
		int min = (int) Math.round(size * k);
		int max = (int) Math.round((size * (1 - k)));
		//System.out.println("\t"+size+"\t"+size*k+"\t"+min+"\t"+max);

		//min = Math.min(min, 1);
		//max = Math.max(max, size-1);

		double mean = 0;
		double dvt = 0;
		double sum = 0;
		double dvt_sum = 0;
		double trim_size = 0;

		/*
		 * compute mean
		 */
		for (int i = min; i < max; i++) {
			long v = gaplist.get(i);
			sum += v;
			trim_size++;
			//System.out.println("\t\t"+v);
		}
		mean = sum / trim_size;

		/*
		 * compute deviation
		 */
		for (int j = min; j < max; j++) {
			long v = gaplist.get(j);
			double d = Math.abs(v - mean);
			dvt_sum += Math.pow(d, 2);
		}
		dvt = Math.sqrt(dvt_sum / trim_size);

		filter = (long) (mean + 3 * dvt);
		System.out.println("mean: \t" + mean + "\t deviration: \t" + dvt + "\t cutoff: \t" + filter);

		return filter;
	}


	private long getMedianCutoff(ArrayList<Long> gaplist) {
		// TODO Auto-generated method stub
		long filter = 0;
		int size = gaplist.size();
		long median = 0;
		long deviation = 0;

		if (size % 2 == 0) {
			long v1 = (Long) gaplist.get(size / 2 - 1);
			long v2 = (Long) gaplist.get(size / 2);
			median = (v1 + v2) / 2;
		} else
			median = gaplist.get(size / 2);

		long sum = 0;
		for (int i = 0; i < size; i++) {
			long v = gaplist.get(i);
			long d = Math.abs(v - median);
			sum += Math.pow(d, 2);
		}
		sum = sum / Long.valueOf(size);
		deviation = (long) Math.sqrt(sum);

		filter = median + 3 * deviation;

		System.out.println("median: \t" + median + "\t deviration: \t" + deviation + "\t cutoff: \t" + filter);
		return filter;
	}


	private long getESD(ArrayList gaplist) {
		// TODO Auto-generated method stub
		long filter = 0;
		long sum = 0;
		long mean = 0;
		long size = Long.valueOf(gaplist.size());
		long dvt = 0;

		/*
		 * compute mean
		 */
		for (int i = 0; i < gaplist.size(); i++) {
			long gap = (Long) gaplist.get(i);
			sum += gap;
		}
		mean = sum / size;

		/*
		 * compute standard deviation
		 */
		long sum_dvt = 0;
		for (int j = 0; j < gaplist.size(); j++) {
			long gap = (Long) gaplist.get(j);
			long distance = Math.abs(gap - mean);
			sum_dvt += Math.pow(distance, 2);
		}
		dvt = (long) Math.sqrt(sum_dvt / size);

		long r = 3;
		filter = mean + r * dvt;
		System.out.println("mean \t" + mean + "\t" + dvt + "\t" + filter);


		return filter;
	}


	private void fliterWithGap(long filter,
							   LinkedHashMap<Integer, String> catalist,
							   LinkedHashMap<Integer, ReleaseLabel> releaselist, LinkedHashMap<Integer, Integer> timelist) {
		// TODO Auto-generated method stub

		String idset = "";
		String partition_id = "";
		String previous_cata = "";
		int previous_year = 0;
		for (int index = 0; index < catalist.size(); index++) {
			String catano = catalist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);
			int year = timelist.get(index);
			long gap = 0;

			if (!previous_cata.equals("")) {

				gap = computeGap(previous_cata, catano);
				int distance = year - previous_year;

				if (gap > filter) {
					if (idset.endsWith(","))
						idset = idset.substring(0, idset.length() - 1);
					String update = "update discogs_release_label SET  partition_id = " + partition_id + " WHERE id in ( " + idset + " )";
					//System.out.println("\t"+update);
					System.out.println();
					idset = "";
				}


			}
			if (idset.equals(""))
				partition_id = String.valueOf(relabel.getId());

			idset += String.valueOf(relabel.getId()) + ",";

			System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + gap + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id() + "\t" + relabel.getPartition_id());

			previous_cata = catano;
			previous_year = year;
		}
		if (idset.endsWith(",")) {
			idset = idset.substring(0, idset.length() - 1);
			String update = "update discogs_release_label SET  partition_id = " + partition_id + " WHERE id in ( " + idset + " )";
			//System.out.println("\t"+update);
			//System.out.println();
		}

		//printMatrix(catalist, timelist);
	}


	private long computeGap(String previous_cata, String catano) {
		// TODO Auto-generated method stub
		long gap = 0;
		Catalog c1 = new Catalog(previous_cata);
		Catalog c2 = new Catalog(catano);

		LinkedHashMap<Integer, Integer> ints1 = c1.getInts();
		LinkedHashMap<Integer, Integer> ints2 = c2.getInts();

		//System.out.println(ints1+"\t"+ints2);
		long value1 = c1.parseIntoInt(ints1);
		long value2 = c2.parseIntoInt(ints2);

		gap = value2 - value1;
		return gap;
	}


	private long getAvgOrder(ArrayList gaplist, int k) {
		// TODO Auto-generated method stub
		long avg = 0;
		long sum = 0;
		if (gaplist.size() <= k)
			k = 0;
		int i = k;
		while (i < gaplist.size()) {
			long order = (Long) gaplist.get(i);
			sum += order;
			i++;
		}
		avg = sum / Long.valueOf(gaplist.size() - k);
		return avg;
	}


	private void sortInsert(long order, ArrayList gaplist) {
		// TODO Auto-generated method stub
		if (gaplist.isEmpty())
			gaplist.add(gaplist.size(), order);
		else {
			boolean stop = false;
			int i = 0;
			while (i < gaplist.size() && !stop) {
				long gap = (Long) gaplist.get(i);
				if (order > gap) {
					stop = true;
					gaplist.add(i, order);
				}
				i++;
			}
			if (stop == false)
				gaplist.add(gaplist.size(), order);
		}
	}


	private void printList(
			HashMap<String, LinkedHashMap<Integer, String>> catalists,
			HashMap<String, LinkedHashMap<Integer, Integer>> timelists,
			HashMap<String, LinkedHashMap<Integer, ReleaseLabel>> releaselists) {
		// TODO Auto-generated method stub

		for (Map.Entry<String, LinkedHashMap<Integer, String>> element : catalists.entrySet()) {
			String prefix = element.getKey();
			LinkedHashMap<Integer, String> catalist = element.getValue();
			LinkedHashMap<Integer, Integer> timelist = timelists.get(prefix);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = releaselists.get(prefix);

			System.out.println("\t" + "prefix" + "\t" + prefix);
			System.out.println();

			int previous_year = 0;
			for (int index = 0; index < catalist.size(); index++) {
				String catano = catalist.get(index);
				int year = timelist.get(index);
				ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

				int distance = year - previous_year;
				if (distance < 0)
					System.out.println();
				//System.out.println("\t\t"+index+"\t"+catano+"\t"+year);
				System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id());
				previous_year = year;
			}

			printMatrix(catalist, timelist);
		}

	}


	private void printMatrix(LinkedHashMap<Integer, String> catalist,
							 LinkedHashMap<Integer, Integer> timelist) {
		// TODO Auto-generated method stub
		String timeindex = "\t";

		int min = 3000;
		int max = 0;
		/*
		 * scan timelist to find the min, max timestamps
		 */
		for (int i = 0; i < timelist.size(); i++) {
			int year = timelist.get(i);
			if (year != 0) {
				if (year > max)
					max = year;
				if (year < min)
					min = year;
			}

		}

		for (int j = min; j <= max; j++) {
			timeindex += String.valueOf(j) + "\t";
		}
		System.out.println(timeindex);

		/*
		 * get distinct list of catalog
		 */
		ArrayList templist = new ArrayList();
		LinkedHashMap<String, ArrayList> tempmap = new LinkedHashMap<String, ArrayList>();
		for (Map.Entry<Integer, String> e : catalist.entrySet()) {
			int index = e.getKey();
			String cata = e.getValue();
			int year = timelist.get(index);
			ArrayList list = new ArrayList();
			if (tempmap.containsKey(cata))
				list = tempmap.get(cata);
			if (!list.contains(year))
				list.add(list.size(), year);
			tempmap.put(cata, list);
		}
		//System.out.println(tempmap);


		/*
		 * scan each row of the matrix
		 */
		for (Map.Entry<String, ArrayList> e : tempmap.entrySet()) {
			//for(int n=0; n<catalist.size(); n++){
			String cata = e.getKey();
			ArrayList list = e.getValue();
			String catarow = cata + "\t";
			for (int m = min; m <= max; m++) {
				String element = "";
				if (list.contains(m))
					element = "1";
				catarow += element + "\t";
			}
			System.out.println(catarow);
		}
	}


	private int insertCatalogNumber(LinkedHashMap<Integer, String> catalist,
									String catano) {
		// TODO Auto-generated method stub
		Catalog cata = new Catalog(catano);
		String prefix = cata.getPrefix();
		LinkedHashMap<Integer, String> strs = cata.getStrs();
		LinkedHashMap<Integer, Integer> ints = cata.getInts();

		int position = -1;
		boolean stop = false;
		int i = 0;
		//for(Map.Entry<Integer, String> e: catalist.entrySet()){
		while (i < catalist.size() && !stop) {
			String localcatano = catalist.get(i);
			Catalog localcata = new Catalog(localcatano);
			//String prefix = cata.getPrefix();
			LinkedHashMap<Integer, String> localstrs = localcata.getStrs();
			LinkedHashMap<Integer, Integer> localints = localcata.getInts();
			long order = cata.compareCatalog(cata, localcata);
			//System.out.println("\t"+catano+"\t"+localcatano+"\t"+order);
			if (order < 0) {
				stop = true;
				position = i;
			}
			i++;
		}
		return position;
		//return -1;
	}


	public void checkOrderConsistency(String entityType, ArrayList dataset) {

		int current_entity = 0;
		int index = 0;

		HashMap<Integer, LinkedHashMap<Integer, String>> catalists = new HashMap<Integer, LinkedHashMap<Integer, String>>();
		HashMap<Integer, LinkedHashMap<Integer, Integer>> timelists = new HashMap<Integer, LinkedHashMap<Integer, Integer>>();
		HashMap<Integer, LinkedHashMap<Integer, ReleaseLabel>> releaselists = new HashMap<Integer, LinkedHashMap<Integer, ReleaseLabel>>();

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				//System.out.println("\t"+label+"\t"+release+"\t"+year+"\t"+catano);

				if (clusterid != current_entity) {
					if (!catalists.isEmpty() && !releaselists.isEmpty() && !timelists.isEmpty()) {
						/*
						 * order records by catalog order
						 */

						orderCatalog(catalists, timelists, releaselists);

					}

					current_entity = clusterid;
					catalists = new HashMap<Integer, LinkedHashMap<Integer, String>>();
					timelists = new HashMap<Integer, LinkedHashMap<Integer, Integer>>();
					releaselists = new HashMap<Integer, LinkedHashMap<Integer, ReleaseLabel>>();
					index = 0;
					System.out.println(i + "\t" + label + "\t" + clusterid);
					System.out.println();
				}

				/*
				 * sort in time, catalog order
				 */
				boolean stop = false;
				int position = -1;
				int length = catano.length();
				LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog pairs in time order
				LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time pairs in time order
				LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id - record pairs in time order

				if (catalists.containsKey(length) && timelists.containsKey(length) && releaselists.containsKey(length)) {
					catalist = catalists.get(length);
					timelist = timelists.get(length);
					releaselist = releaselists.get(length);

					int m = 0;
					while (m < timelist.size() && !stop) {
						int localindex = m;
						int localyear = timelist.get(localindex);
						if (localyear == year) {
							position = localindex;
							String localcatno = catalist.get(localindex);
							int order = catano.compareTo(localcatno);
							if (order < 0) {
								stop = true;
							} else
								position++;
						}
						m++;
					}

				}
				if (position == -1) {
					position = timelist.size();
				}


				timelist.put(position, year);
				catalist.put(position, catano);
				releaselist.put(position, relabel);

				timelists.put(length, timelist);
				catalists.put(length, catalist);
				releaselists.put(length, releaselist);
				//System.out.println("\t"+position+"\t"+year+"\t"+catano);

			}

			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());


		}

		if (!catalists.isEmpty() && !releaselists.isEmpty() && !timelists.isEmpty()) {
			/*
			 * order records by catalog order
			 */
			orderCatalog(catalists, timelists, releaselists);

		}

	}

	public void checktempOrderConsistency(String entityType, ArrayList dataset) {

		int current_entity = 0;
		int index = 0;

		LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog pairs in time order
		LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time pairs in time order
		LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id - record pairs in time order


		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				//System.out.println("\t"+label+"\t"+release+"\t"+year+"\t"+catano);

				if (clusterid != current_entity) {
					if (!catalist.isEmpty() && !releaselist.isEmpty() && !timelist.isEmpty()) {
						/*
						 * order records by catalog order
						 */
						ordertempCatalog(catalist, timelist, releaselist);

					}

					current_entity = clusterid;
					catalist = new LinkedHashMap<Integer, String>();
					timelist = new LinkedHashMap<Integer, Integer>();
					releaselist = new LinkedHashMap<Integer, ReleaseLabel>();
					index = 0;
					System.out.println(i + "\t" + label + "\t" + clusterid);
					System.out.println();
				}

				/*
				 * sort in time, catalog order
				 */
				boolean stop = false;
				int position = -1;
				int m = 0;
				while (m < timelist.size() && !stop) {
					int localindex = m;
					int localyear = timelist.get(localindex);
					if (localyear == year) {
						position = localindex;
						String localcatno = catalist.get(localindex);
						int order = catano.compareTo(localcatno);
						if (order < 0) {
							stop = true;
						} else
							position++;
					}
					m++;
				}

				if (position == -1) {
					position = timelist.size();
				}

				timelist.put(position, year);
				catalist.put(position, catano);
				releaselist.put(position, relabel);


				//System.out.println("\t"+position+"\t"+year+"\t"+catano);
				index++;

			}

			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());


		}

		if (!catalist.isEmpty() && !releaselist.isEmpty() && !timelist.isEmpty()) {
			/*
			 * order records by catalog order
			 */
			ordertempCatalog(catalist, timelist, releaselist);

		}

	}


	private void orderCatalog(HashMap<Integer, LinkedHashMap<Integer, String>> catalists, HashMap<Integer, LinkedHashMap<Integer, Integer>> timelists, HashMap<Integer, LinkedHashMap<Integer, ReleaseLabel>> releaselists) {
		// TODO Auto-generated method stub
		Levenstein edit = new Levenstein();

		for (Map.Entry<Integer, LinkedHashMap<Integer, String>> element : catalists.entrySet()) {
			int length = element.getKey();
			LinkedHashMap<Integer, String> catalist = element.getValue();
			LinkedHashMap<Integer, Integer> timelist = timelists.get(length);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = releaselists.get(length);
			ArrayList temp = new ArrayList();

			System.out.println("\t catalog length= \t" + length);
			System.out.println();
			
			/*
			 * sort by time, catalog
			 
			String previous_catano = "";
			for(int m=0; m< timelist.size(); m++){
				int year = timelist.get(m);
				String catno = catalist.get(m);
				ReleaseLabel relabel = (ReleaseLabel) releaselist.get(m);
				//System.out.println("\t"+m+"\t"+year+"\t"+catno);
				
				int order = 0;
				if(!previous_catano.equals("")){
					order = previous_catano.compareTo(catno);
					//System.out.println(previous_catano+"\t"+catno+"\t"+order);
				}
				if(order >0)
					System.out.println();
				previous_catano = catno;
				System.out.println("\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id());	
				
			}*/


			for (Map.Entry<Integer, String> e : catalist.entrySet()) {
				int index = e.getKey();
				String catano = e.getValue();
				//System.out.println("!!! process catano \t"+catano+"\t w index \t"+index);
				if (temp.isEmpty()) {
					//System.out.println("\t\t add catano as new node to temp \t"+temp.size()+"\t"+catano);
					temp.add(temp.size(), index);
				} else {
					boolean stop = false;
					int i = 0;
					//System.out.println("\t insert cata to temp list \t"+temp);
					while (i < temp.size() && !stop) {
						int index1 = (Integer) temp.get(i);
						String catano1 = catalist.get(index1);
						//System.out.println("\t"+catano+"\t"+catano1);
						int order = catano.compareTo(catano1); // if order < 0, it means catano < catano1

						if (order < 0) {
							stop = true;
							temp.add(i, index);
							//System.out.println("\t\t add catano to \t"+i+"\t"+catano);
						}

						i++;
					}
					if (stop == false) {
						//System.out.println("\t\t add catano to the end \t"+temp.size()+"\t"+catano);
						temp.add(temp.size(), index);
					}


				}
			}


			int previous_position = 0;
			String previous_catano = "";
			for (int j = 0; j < temp.size(); j++) {
				int index = (Integer) temp.get(j);
				String catano = catalist.get(index);
				int year = timelist.get(index);
				ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

				if (!previous_catano.equals("")) {
					double score = edit.score(previous_catano, catano);
					//System.out.println("\t"+previous_catano+"\t"+catano+"\t"+score);
				}

				int distance = index - previous_position;
				if (distance < 0)
					System.out.println();
				//System.out.println("\t\t"+index+"\t"+catano+"\t"+year);
				System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id());
				previous_position = index;
				previous_catano = catano;
			}
		}


	}

	private void ordertempCatalog(LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		ArrayList temp = new ArrayList();

		for (Map.Entry<Integer, String> e : catalist.entrySet()) {
			int index = e.getKey();
			String catano = e.getValue();
			if (temp.isEmpty()) {
				//System.out.println("\t\t"+temp.size()+"\t"+catano);
				temp.add(temp.size(), index);
			} else {
				boolean stop = false;
				int i = 0;
				while (i < temp.size() && !stop) {
					int index1 = (Integer) temp.get(i);
					String catano1 = catalist.get(index1);
					int order = catano.compareTo(catano1); // if order < 0, it means catano < catano1
					if (order < 0) {
						stop = true;
						temp.add(i, index);
						//System.out.println("\t\t"+i+"\t"+catano);
					}

					i++;
				}
				if (stop == false) {
					//System.out.println("\t\t"+temp.size()+"\t"+catano);
					temp.add(temp.size(), index);
				}


			}
		}
		
		/*
		for(int m=0; m< timelist.size(); m++){
			int year = timelist.get(m);
			String catno = catalist.get(m);
			System.out.println("\t"+m+"\t"+year+"\t"+catno);
		}*/

		int previous_position = 0;
		for (int j = 0; j < temp.size(); j++) {
			int index = (Integer) temp.get(j);
			String catano = catalist.get(index);
			int year = timelist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

			int distance = index - previous_position;
			if (distance < 0)
				System.out.println();
			//System.out.println("\t\t"+index+"\t"+catano+"\t"+year);
			System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id());
			previous_position = index;
		}
	}


	public void checkDistribution(String entityType, ArrayList dataset) {

		int current_entity = 0;
		int current_year = 0;
		int size = 0;

		LinkedHashMap<Integer, Integer> frequency = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, Integer> gaps = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, Integer> sizes = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, Integer> distances = new LinkedHashMap<Integer, Integer>();


		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();

				if (clusterid != current_entity) {
					if (!frequency.isEmpty()) {
						//printFrequency(distances);
						//printFrequency(frequency);
						
						/*
						 * this function is to count how often a label produce a release, i.e., delta t = t_n - t_{n-1}
						 
						System.out.println();
						countGaps(frequency, gaps);*/
						updateSize(size, sizes);
					}
					current_entity = clusterid;
					current_year = year;
					size = 1;
					frequency = new LinkedHashMap<Integer, Integer>();
					distances = new LinkedHashMap<Integer, Integer>();
					frequency.put(year, 1);
					System.out.println(i + "\t" + clusterid + "\t" + label);
					System.out.println();
				} else {

					int count = 0;
					if (frequency.containsKey(Integer.valueOf(year)))
						count = frequency.get(year);
					count += 1;
					frequency.put(year, count);
					size += 1;
					//System.out.println("\t add count \t"+count+"\t to year \t"+year);

					/*
					 * count how often a label produces an album
					 */
					int distance = year - current_year;
					if (distance > 1)
						System.out.println("\t" + year + "\t" + distance);
					current_year = year;
					int value = 1;
					if (distances.containsKey(distance))
						value = distances.get(distance) + 1;
					distances.put(distance, value);
				}

				//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());	
			}

		}
		if (!frequency.isEmpty()) {
			//printFrequency(frequency);
			//printFrequency(distances);

			
			/*
			 * this function is to count how often a label produce a release, i.e., delta t = t_n - t_{n-1}
			 
			System.out.println();
			countGaps(frequency, gaps);*/

			updateSize(size, sizes);
		}

		/*
		 * print gap distribution: x axis is year; y axis is # of gaps
		 */
		//System.out.println("gap distribution");
		//printFrequency(gaps);

		/*
		 * print size distribution
		 */
		//System.out.println("size distribution");
		//printFrequency(sizes);
	}

	private void updateSize(int size, LinkedHashMap<Integer, Integer> sizes) {
		// TODO Auto-generated method stub
		int value = 1;
		if (sizes.containsKey(size))
			value = sizes.get(size) + 1;
		sizes.put(size, value);
		System.out.println("\t size \t" + size);
	}


	private void countGaps(LinkedHashMap<Integer, Integer> frequency, LinkedHashMap<Integer, Integer> gaps) {
		// TODO Auto-generated method stub
		int previous_year = 0;
		System.out.println("\t\t" + "count");
		for (Map.Entry<Integer, Integer> e : frequency.entrySet()) {
			int year = e.getKey();
			int gap = 0;
			if (previous_year > 0)
				gap = year - previous_year;
			previous_year = year;
			if (gap > 0)
				System.out.println("\t" + year + "\t" + gap);
			if (gap > 0) {
				int count = 1;
				if (gaps.containsKey(gap))
					count = gaps.get(gap) + 1;
				gaps.put(gap, count);
			}
		}
	}


	private void printFrequency(LinkedHashMap<Integer, Integer> frequency) {
		// TODO Auto-generated method stub
		int min = 3000;
		int max = 0;
		for (Map.Entry<Integer, Integer> e : frequency.entrySet()) {
			int year = e.getKey();
			if (year > max)
				max = year;
			if (year < min)
				min = year;
		}
		System.out.println("\t\t" + "count");
		int total_count = 0;
		for (int i = min; i <= max; i++) {
			int count = 0;
			if (frequency.containsKey(i))
				count = frequency.get(i);
			System.out.println("\t" + i + "\t" + count);
			total_count += count;
		}
		System.out.println("\t total count \t" + total_count);
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
					if (distance > 0 && label1.equals(label2)) {
						//System.out.println(i+"\t"+id+"\t"+release1+"\t"+label1+"\t"+country1+"\t"+year1+"\t"+release2+"\t"+label2+"\t"+country2+"\t"+year2+"\t"+distance);
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

				//System.out.println(i+"\t"+id+"\t"+release+"\t"+label+"\t"+country+"\t"+year+"\t"+artist+"\t"+extra+"\t"+catno+"\t"+format+"\t"+discogsid);

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

			//
		}
		System.out.println(relabels.size());
		return relabels;
	}

	private ArrayList queryReleaseLabel(String labels) throws Exception {
		// TODO Auto-generated method stub

		String sql = /*"select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id, r.id "+
				  "from discogs_release r, discogs_release_artist ra, discogs_release_label rl, discogs_label l, discogs_release_format rf, discogs_release_extraartist re "+
				  "where ra.release_id=r.id  and rf.release_id = r.id and re.release_id = r.id "+
				  //"and r.id=rl.release_id and rl.label_name = l.name and   l.name in ("+labels+") and date<>'?'  order by l.id, date, catno";
				  "and r.id=rl.release_id and rl.label_name = l.name and  l.id<50 and l.id>0 order by l.id, date, catno";
		// date<>'?' and date<> '???' and date<>'????' and date<>'Unknown' and*/
				"select rl.id, r.title as release, r.genres, r.styles, r.country, date, ra.name as artist_name, rl.label_name, rl.catno, rf.name as " + "format, rf.qty, rf.description, re.name as extra_artist, l.id as cluster_id, r.id, rl.partition_id " +
						"from discogs_release r, discogs_release_artist ra, discogs_release_label rl, " + "discogs_label l, discogs_release_format rf, discogs_release_extraartist re " +
						"where ra.release_id=r.id " + "and rf.release_id = r.id and re.release_id = r.id and r.id=rl.release_id and rl.label_name = l.name and date<>'?' and date<> '???' and date<>'????' and date<>'Unknown' and date<>'' and labelcount=1  " +
						//"and l.id in "+"(select distinct l.id as cluster_id "+"from discogs_release r, discogs_release_label rl, "+"discogs_label l where date ='' and r.id=rl.release_id and rl.label_name = l.name and l.id<200)  and labelcount=1  " +
						"and l.id in (109, 34231, 79, 157, 36605, 34599) order by l.id, catno, date;";
		//"and l.id in (34231)  order by l.id, catno, date;";


		ArrayList records = new ArrayList();
		LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();
		this.clusterlist = new HashMap<Integer, Integer>();

		Connection con = ConnectionPool.getConnection();

		if (!con.isClosed()) {
			int i = 0;
			Statement st = con.createStatement();
			System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			//select id, release_name, genres, styles, country, date, artist_name, label_name, catalog_num, format, format_qty, format_description, extra_artist, cluster_id,  rid,  partition_id
			// 		 1, 	2,			3, 		4, 		5, 		6, 		7, 				8, 			9		10,			11				12				13			14			15	    16

			while (result.next()) {
				int id = result.getInt(1);
				String release = result.getString(2);
				String genres = result.getString(3);
				String styles = result.getString(4);
				String country = result.getString(5);
				String year = result.getString(6);
				if (year.equals("?") || year.equals("???") || year.equals("????") || year.equals("Unknown") || year.equals("??") || year.equals("None") || year.equals("Not Known") || year.equals(""))
					year = "0";
				//System.out.println(year);
				if (year.length() > 4)
					year = year.substring(0, 4);

				String artist = result.getString(7);
				String label = result.getString(8);
				String catno = result.getString(9);
				/*
				 * 
				
				catno = catno.replace("-", "");
				catno = catno.replace(" ", "");
				catno = catno.replace("/", "");
				catno = catno.replace(",", "");
				catno = catno.replace(".", "");
				catno = catno.replace("_", "");
				catno = catno.replace(":", "");
				 */

				catno = catno.toUpperCase();
				catno = catno.trim();

				String format = result.getString(10);
				String qty = result.getString(11);
				//format += " "+qty;
				String description = result.getString(12);
				description = sortStrings(description);
				//format += " "+description;
				String extra_artist = result.getString(13);
				int cluster_id = result.getInt(14);
				int rid = result.getInt(15);
				int partition_id = result.getInt(16);

				//System.out.println(label+"\t"+year+"\t"+catno);

				if (labelist != null) {


					for (Map.Entry<Integer, String> e : labelist.entrySet()) {
						int mbid = e.getKey();
						String mblabel = e.getValue();
						//System.out.println("lablist \t"+mbid+"\t"+mblabel);
						if (!clusterlist.containsKey(mbid)) {
							if (mblabel.equals(label)) {
								clusterlist.put(mbid, cluster_id);
								//System.out.println("\t"+mbid+"\t"+mblabel+"\t"+cluster_id);
							}
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
						artists += "|" + artist;
						relabel.setStrartist(artists);
					}

					String extra_artists = relabel.getStrextrartist();
					if (!extra_artists.contains(extra_artist)) {
						extra_artists += "|" + extra_artist;
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
					relabel.setPartition_id(partition_id);

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
			//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getFormat_description()+"\t"+relabel.getQty()+"\t"+relabel.getCluster_id());	
			records.add(records.size(), relabel);
		}
		System.out.println("discogs label size \t" + records.size());
		return records;

	}

	private String sortStrings(String description) {
		// TODO Auto-generated method stub
		//String temp = "";

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

		//System.out.println(list+"| \t| "+list.toString());
		return list.toString();
	}


	public DataAnalysis(String sql) throws Exception {

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
