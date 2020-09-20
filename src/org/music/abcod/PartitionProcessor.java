package org.music.abcod;

import com.wcohen.secondstring.Levenstein;
import org.jetbrains.annotations.NotNull;
import org.music.connection.ConnectionPool;
import org.music.data.ArtistData;
import org.music.data.Pipe;
import org.music.data.ReleaseLabel;
import org.music.segmentation.baseline.ScaleProcessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class PartitionProcessor extends AbstractProcessor {

	public PartitionProcessor(String sql, String entity) throws Exception {
		super(sql, entity);
	}

	public LinkedHashMap<Integer, ArrayList> blocks; // block_id - record ids
	// (blocks are sorted)
	public LinkedHashMap<Integer, ArrayList> blockinvertedlist; // record id -
	// block ids
	// (blocks are
	// sorted)
	public HashMap<String, Integer> blocklist; // block key - block id
	public HashMap<Integer, String> blockeylist; // blockid - blockey
	private HashMap<Integer, String> labelist;

	public static ArrayList<LinkedHashMap<Integer, String>> finalcatalists;
	public static ArrayList<LinkedHashMap<Integer, Integer>> finaltimelists;
	public static ArrayList<LinkedHashMap<Integer, ReleaseLabel>> finalreleaselists;

	public static ArrayList<LinkedHashMap<Integer, String>> gapCataLists;
	public static ArrayList<LinkedHashMap<Integer, Integer>> gapTimeLists;
	public static ArrayList<LinkedHashMap<Integer, ReleaseLabel>> gapReleaseLists;

	private int fp;
	private int fn;
	private int tp;
	private int tn;
	private int original_missing;
	private int repress_missing;
	private int increasingSeriesCount;
	private int decreasingSeriesCount;
	private int increasingSeriesSize;
	private int decreasingSeriesSize;

	public LinkedHashMap<Integer, ArrayList> getBlocks() {
		return blocks;
	}

	public void setBlocks(LinkedHashMap<Integer, ArrayList> blocks) {
		this.blocks = blocks;
	}

	public LinkedHashMap<Integer, ArrayList> getBlockinvertedlist() {
		return blockinvertedlist;
	}

	public void setBlockinvertedlist(LinkedHashMap<Integer, ArrayList> blockinvertedlist) {
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
				// System.out.println(genres);

				// String value = country;
				// String artists = relabel.getStrartist();
				// String artists = relabel.getStrextrartist();
				// String[] split = artists.split("\\|");
				// for(int m=0; m<split.length; m++){
				// System.out.println(relabel.getGenreslist());

				// for(int m=0; m< genres.size(); m++){
				// System.out.println(i+"\t"+clusterid+"\t"+artists+"\t"+year);
				// String artist = split[m];
				// System.out.println("\t\t\t"+artist);
				// String value = artist;

				// String genre = (String) genres.get(m);
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
			}
		}

		if (!list.isEmpty())
			printSubDistribution(list, current_label);
	}

	private void updateList(HashMap<String, ArrayList> list, String value, int year) {
		// TODO Auto-generated method stub
		ArrayList years = new ArrayList();
		if (list.containsKey(value))
			years = list.get(value);
		years.add(years.size(), year);
		list.put(value, years);
		// System.out.println("\t"+value+"\t"+years);
	}

	private void printSubDistribution(HashMap<String, ArrayList> list, String current_label) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList> e : list.entrySet()) {
			String value = e.getKey();
			ArrayList years = e.getValue();
			if (years.size() > 1) {
				System.out.println();
				System.out.println("\t" + value + "\t" + years.size() + "\t" + current_label);
				// System.out.println();
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
					// System.out.println("\t\t|"+year+"|\t"+count+"\t"+min+"\t"+max);
					temp.put(year, count);
				}
				// System.out.println(temp.size());
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

					// printList(catalists, timelists, releaselists);

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
				LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog
				// pairs
				// in
				// catalog
				// order
				LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time
				// pairs
				// in
				// catalog
				// order
				LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id
				// -
				// record
				// pairs
				// in
				// catalog
				// order

				if (catalists.containsKey(prefix) && timelists.containsKey(prefix)
						&& releaselists.containsKey(prefix)) {
					catalist = catalists.get(prefix);
					timelist = timelists.get(prefix);
					releaselist = releaselists.get(prefix);

					// position = insertCatalogNumber(catalist, catano);

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

			// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());

		}

		// printList(catalists, timelists, releaselists);

	}

	public static void block(String entityType, ArrayList dataset) {
		int current_entity = 0;
		String previous_prefix = "";

		HashMap<String, ArrayList<String>> catalists = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<Integer>> timelists = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<ReleaseLabel>> releaselists = new HashMap<String, ArrayList<ReleaseLabel>>();

		finalcatalists = new ArrayList<LinkedHashMap<Integer, String>>();
		finaltimelists = new ArrayList<LinkedHashMap<Integer, Integer>>();
		finalreleaselists = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int year = relabel.getDate();
				int clusterId = relabel.getCluster_id();
				String catano = relabel.getCatno();

				Catalog cata = new Catalog(catano);
				String prefix = cata.getPrefix();

				if (clusterId != current_entity) {
					generateBlock(catalists, timelists, releaselists);

					current_entity = clusterId;
					catalists = new HashMap<String, ArrayList<String>>();
					timelists = new HashMap<String, ArrayList<Integer>>();
					releaselists = new HashMap<String, ArrayList<ReleaseLabel>>();
					// System.out.println();
				}

				/*
				 * sort in catalog numeric order
				 */
				int position = -1;
				/* id-catalog pairs in catalog order */
				ArrayList<String> catalist = new ArrayList<String>();
				ArrayList<Integer> timelist = new ArrayList<Integer>();
				/* id-time pairs in catalog order */
				ArrayList<ReleaseLabel> releaselist = new ArrayList<ReleaseLabel>();
				/* id-record pairs in catalog order */

				if (catalists.containsKey(prefix) && timelists.containsKey(prefix)
						&& releaselists.containsKey(prefix)) {
					catalist = catalists.get(prefix);
					timelist = timelists.get(prefix);
					releaselist = releaselists.get(prefix);

					position = insertCatalogNumber(catalist, catano);

				}

				if (position == -1) {
					position = timelist.size();
				}

				timelist.add(position, year);
				catalist.add(position, catano);
				releaselist.add(position, relabel);

				timelists.put(prefix, timelist);
				catalists.put(prefix, catalist);
				releaselists.put(prefix, releaselist);
			}
		}

		if (!catalists.isEmpty()) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				generateBlock(catalists, timelists, releaselists);
			} else if (entityType.equalsIgnoreCase("cars")) {

			}
		}

		if (entityType.equalsIgnoreCase("cars")) {
			generateCarBlock(dataset);
		}

		/*
		 * evaluate partitioning
		 */
		Evaluation evaluation = new Evaluation();
		// evaluation.evaluationPartition(finalreleaselists);
	}

	private static void generateCarBlock(ArrayList<ReleaseLabel> dataset) {
		HashMap<Integer, ArrayList<ReleaseLabel>> carMap = new HashMap<Integer, ArrayList<ReleaseLabel>>();
		for (ReleaseLabel car : dataset) {
			int blockID = car.getBlockID();
			if (carMap.containsKey(blockID)) {
				ArrayList<ReleaseLabel> carList = carMap.get(blockID);
				carList.add(car);
				Collections.sort(carList);
				carMap.put(blockID, carList);
			} else {
				ArrayList<ReleaseLabel> carList = new ArrayList<ReleaseLabel>();
				carList.add(car);
				Collections.sort(carList);
				carMap.put(blockID, carList);
			}
		}

		for (Map.Entry<Integer, ArrayList<ReleaseLabel>> carListEntry : carMap.entrySet()) {
			int index = carListEntry.getKey();
			ArrayList<ReleaseLabel> carList = carListEntry.getValue();
			ArrayList<String> catalist = new ArrayList<String>();
			ArrayList<Integer> timelist = new ArrayList<Integer>();
			ArrayList<ReleaseLabel> releaselist = new ArrayList<ReleaseLabel>();

			for (ReleaseLabel car : carList) {
				String vin = car.getCatno();
				int year = car.getDate();

				catalist.add(catalist.size(), vin);
				timelist.add(timelist.size(), year);
				releaselist.add(releaselist.size(), car);
			}

			addBlock(catalist, releaselist, timelist);
		}

	}

	public static void partitionWithGap(String entityType, ArrayList dataset) {
		int current_entity = 0;
		String previous_prefix = "";

		HashMap<String, ArrayList<String>> catalists = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<Integer>> timelists = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<ReleaseLabel>> releaselists = new HashMap<String, ArrayList<ReleaseLabel>>();

		gapCataLists = new ArrayList<LinkedHashMap<Integer, String>>();
		gapTimeLists = new ArrayList<LinkedHashMap<Integer, Integer>>();
		gapReleaseLists = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				Catalog cata = new Catalog(catano);
				String prefix = cata.getPrefix();

				if (clusterid != current_entity) {

					/*
					 * iterative partitioning w. gap
					 */
					gapPartition(catalists, timelists, releaselists);

					/*
					 * partition w. gap
					 */
					current_entity = clusterid;
					current_entity = clusterid;
					catalists = new HashMap<String, ArrayList<String>>();
					timelists = new HashMap<String, ArrayList<Integer>>();
					releaselists = new HashMap<String, ArrayList<ReleaseLabel>>();
					// System.out.println();
				}

				/*
				 * sort in catalog numeric order
				 */
				int position = -1;
				/* id-catalog pairs in catalog order */
				ArrayList<String> catalist = new ArrayList<String>();
				ArrayList<Integer> timelist = new ArrayList<Integer>();
				/* id-time pairs in catalog order */
				ArrayList<ReleaseLabel> releaselist = new ArrayList<ReleaseLabel>();
				/* id-record pairs in catalog order */

				if (catalists.containsKey(prefix) && timelists.containsKey(prefix)
						&& releaselists.containsKey(prefix)) {
					catalist = catalists.get(prefix);
					timelist = timelists.get(prefix);
					releaselist = releaselists.get(prefix);

					position = insertCatalogNumber(catalist, catano);

				}

				if (position == -1) {
					position = timelist.size();
				}

				timelist.add(position, year);
				catalist.add(position, catano);
				releaselist.add(position, relabel);

				timelists.put(prefix, timelist);
				catalists.put(prefix, catalist);
				releaselists.put(prefix, releaselist);
			}
		}

		if (!catalists.isEmpty())
			gapPartition(catalists, timelists, releaselists);

		/*
		 * evaluate partitioning
		 */
		Evaluation evaluation = new Evaluation();
		// evaluation.evaluationPartition(gapReleaseLists);
	}

	/*
	 * private static void iterativeGapPartition(HashMap<String,
	 * LinkedHashMap<Integer, String>> catalists, HashMap<String,
	 * LinkedHashMap<Integer, Integer>> timelists, HashMap<String,
	 * LinkedHashMap<Integer, ReleaseLabel>> releaselists) { // TODO
	 * Auto-generated method stub // add catalists, timelists, releaselists into
	 * Q
	 *
	 * ArrayList<LinkedHashMap<Integer, String>> tempcatalists = new
	 * ArrayList<LinkedHashMap<Integer, String>>();
	 * ArrayList<LinkedHashMap<Integer, Integer>> temptimelists = new
	 * ArrayList<LinkedHashMap<Integer, Integer>>();
	 * ArrayList<LinkedHashMap<Integer, ReleaseLabel>> tempreleaselists = new
	 * ArrayList<LinkedHashMap<Integer, ReleaseLabel>>(); int size =
	 * catalists.size(); for(Map.Entry<String, LinkedHashMap<Integer, String>>
	 * element : catalists.entrySet()){ String prefix = element.getKey();
	 * LinkedHashMap<Integer, String> catalist = element.getValue();
	 * LinkedHashMap<Integer, Integer> timelist = timelists.get(prefix);
	 * LinkedHashMap<Integer, ReleaseLabel> releaselist =
	 * releaselists.get(prefix);
	 *
	 * int index = tempcatalists.size(); tempcatalists.add(index, catalist);
	 * temptimelists.add(index, timelist); tempreleaselists.add(index,
	 * releaselist); }
	 *
	 * // for each partition in the list, split if biggest gap > threshold, and
	 * add the two partitions back to the queen; otherwise, add the partition to
	 * output set
	 *
	 * int i = 0; while(!tempcatalists.isEmpty()&&i<tempcatalists.size()){
	 * ArrayList< String> catalist = tempcatalists.get(i); ArrayList<Integer>
	 * timelist = temptimelists.get(i); ArrayList<ReleaseLabel> releaselist =
	 * tempreleaselists.get(i);
	 *
	 * //System.out.println("partition \t"+i);
	 *
	 * if(catalist.size()>1){
	 *
	 * long filter = 0; long maxGap = 0; filter = standardPartition(catalist,
	 * timelist, releaselist); maxGap = getMaxValue(catalist, releaselist);
	 * if(maxGap>filter){ // partition with maxGap
	 *
	 * // * remove partition
	 *
	 * tempcatalists.remove(i); temptimelists.remove(i);
	 * tempreleaselists.remove(i); //i --;
	 *
	 * filterWithMaxGap(maxGap, catalist, timelist, releaselist, tempcatalists,
	 * temptimelists, tempreleaselists, i);
	 *
	 * } else{ outputPartition(catalist, timelist, releaselist); i++; }
	 *
	 * }else{ outputPartition(catalist, timelist, releaselist); i++; }
	 *
	 * //System.out.println(); }
	 *
	 * }
	 */

	private static void filterWithMaxGap(long filter, LinkedHashMap<Integer, String> catalist,
										 LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, ReleaseLabel> releaselist,
										 ArrayList<LinkedHashMap<Integer, String>> tempcatalists,
										 ArrayList<LinkedHashMap<Integer, Integer>> temptimelists,
										 ArrayList<LinkedHashMap<Integer, ReleaseLabel>> tempreleaselists, int i) {
		// TODO Auto-generated method stub
		String previous_cata = "";
		int previous_year = 0;

		/*
		 * initialize hash-maps for catalog, year, and release
		 */
		LinkedHashMap<Integer, String> tempcatalist = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, ReleaseLabel> tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();
		LinkedHashMap<Integer, Integer> temptimelist = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < catalist.size(); index++) {
			String catano = catalist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);
			int year = timelist.get(index);
			long gap = 0;

			if (!previous_cata.equals("")) {

				gap = computeGap(previous_cata, catano);
				int distance = year - previous_year;

				if (gap >= filter) {
					// System.out.println();

					/*
					 * cutoff partition; add records of previous part into
					 * final_xxx_list; empty temp_xxx_list;
					 */

					tempcatalists.add(i, tempcatalist);
					temptimelists.add(i, temptimelist);
					tempreleaselists.add(i, tempreleaselist);
					// System.out.println("add to the quenue
					// \t"+i+"\t"+tempcatalist.size()+"\t"+tempcatalists.size());
					i++;

					tempcatalist = new LinkedHashMap<Integer, String>();
					temptimelist = new LinkedHashMap<Integer, Integer>();
					tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();

					// System.out.println("\t tempcatalist size
					// \t"+tempcatalist.size()+"\t"+finalcatalists.size());
				}

			}

			// System.out.println("\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+gap+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id());

			/*
			 * add value into three temp hash-map
			 */
			int size = tempcatalist.size();
			tempcatalist.put(size, catano);
			temptimelist.put(size, year);
			tempreleaselist.put(size, relabel);

			previous_cata = catano;
			previous_year = year;
		}

		if (!tempcatalist.isEmpty()) {
			tempcatalists.add(i, tempcatalist);
			temptimelists.add(i, temptimelist);
			tempreleaselists.add(i, tempreleaselist);
			// System.out.println("add to the quenue
			// \t"+i+"\t"+tempcatalist.size()+"\t"+tempcatalists.size());

		}

		// printMatrix(catalist, timelist);
	}

	private static long getMaxValue(LinkedHashMap<Integer, String> catalist,
									LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		long maxGap = 0;
		ArrayList gaplist = new ArrayList();
		// gaplist = sortGaps(catalist, releaselist);
		// System.out.println(gaplist);
		maxGap = (Long) gaplist.get(0);
		return maxGap;
	}

	private static void outputPartition(LinkedHashMap<Integer, String> catalist,
										LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		int size = finalcatalists.size();

		finalcatalists.add(size, catalist);
		finaltimelists.add(size, timelist);
		finalreleaselists.add(size, releaselist);
		// System.out.println("add to output
		// \t"+size+"\t"+catalist.size()+"\t"+finalcatalists.size());
	}

	private static void generateBlock(
			HashMap<String, ArrayList<String>> catalists,
			HashMap<String, ArrayList<Integer>> timelists,
			HashMap<String, ArrayList<ReleaseLabel>> releaselists) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList<String>> element : catalists.entrySet()) {
			String prefix = element.getKey();
			ArrayList<String> catalist = element.getValue();
			ArrayList<Integer> timelist = timelists.get(prefix);
			ArrayList<ReleaseLabel> releaselist = releaselists.get(prefix);

			if (catalist.size() > 1)
				addBlock(catalist, releaselist, timelist);
		}
	}

	private static void gapPartition(HashMap<String, ArrayList<String>> catalists,
									 HashMap<String, ArrayList<Integer>> timelists, HashMap<String, ArrayList<ReleaseLabel>> releaselists) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList<String>> element : catalists.entrySet()) {
			String prefix = element.getKey();
			ArrayList<String> catalist = element.getValue();
			ArrayList<Integer> timelist = timelists.get(prefix);
			ArrayList<ReleaseLabel> releaselist = releaselists.get(prefix);

			if (catalist.size() > 1) {

				/*
				 * use the whole dataset to filter
				 */
				long filter = 0;
				filter = standardPartition(catalist, timelist, releaselist);

				/*
				 * local partition with window size = 10
				 *
				 * int k = 10; //localStardardPartition(catalist, timelist,
				 * releaselist, k);
				 */

				// System.out.println();
				// System.out.println("\t id \t label \t release \t artist \t
				// credited artists \t genres \t stlyes \t country \t year \t
				// year_distance \t catalog \t catalog distance \t format \t
				// quantity \t format description \t label id \t partition id");

				filterWithGap(filter, catalist, releaselist, timelist);
			}

		}
	}

	public void findLIB(int deltat) {

		for (int count = 0; count < finalcatalists.size(); count++) {

			System.out.println("Process partition \t" + count + "\n");
			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			int max_deltat = deltat;

			// while(deltat <= max_deltat){
			System.out.println("\t delta year = " + deltat);

			LIB(timelist, catalist, releaselist, deltat);
			// deltat ++;
			// }

		}

	}

	private void LIB(LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, String> catalist,
					 LinkedHashMap<Integer, ReleaseLabel> releaselist, int deltat) {
		// TODO Auto-generated method stub
		// n bandlist: for key i, store the increasing bands ending at i
		ArrayList<HashMap<Pipe, ArrayList>> bandlists = new ArrayList<HashMap<Pipe, ArrayList>>();
		int n = timelist.size();
		for (int i = 0; i < n; i++) {
			int time = timelist.get(i);
			HashMap<Pipe, ArrayList> bandlist = new HashMap<Pipe, ArrayList>();

			Pipe pipe = new Pipe();
			pipe.setLower1(time - deltat);
			pipe.setLower2(time);
			pipe.setUpper1(time);
			pipe.setUpper2(time + deltat);
			pipe.setId(i);

			ArrayList band = new ArrayList();
			band.add(band.size(), pipe);

			bandlist.put(pipe, band);

			bandlists.add(bandlists.size(), bandlist);
		}

		int maxLength = 1;

		for (int i = 1; i < n; i++) {
			if (timelist.get(i) > 0) {
				int time1 = timelist.get(i);
				// System.out.println("\t process "+i+"\t"+timelist.get(i));
				HashMap<Pipe, ArrayList> bandlist1 = bandlists.get(i);

				for (int j = 0; j < i; j++) {
					if (timelist.get(j) > 0) {
						// System.out.println("\t\t process
						// "+j+"\t"+timelist.get(j));
						int time2 = timelist.get(j);

						if ((timelist.get(i) + deltat) >= timelist.get(j)) {
							HashMap<Pipe, ArrayList> bandlist2 = bandlists.get(j);
							for (Map.Entry<Pipe, ArrayList> e2 : bandlist2.entrySet()) {
								Pipe pipe2 = e2.getKey();
								ArrayList band2 = e2.getValue();
								// System.out.println("\t\t\t process pipe
								// \t"+pipe2);
								for (int m = 0; m < band2.size(); m++) {
									Pipe p = (Pipe) band2.get(m);
									// System.out.println("\t\t\t\t[("+p.getLower1()+","+p.getLower2()+"),("+p.getUpper1()+","+p.getUpper2()+")]");
								}

								Pipe pipe1 = new Pipe();
								pipe1.setId(i);
								if (pipe2.getLower1() > (time1 - deltat)) {
									pipe1.setLower1(pipe2.getLower1());
									pipe1.setLower2(time1);

									pipe1.setUpper1(pipe1.getLower1() + deltat);
									pipe1.setUpper2(pipe1.getLower2() + deltat);

								} else {
									pipe1.setLower1(time1 - deltat);
									pipe1.setLower2(time1);
									pipe1.setUpper1(time1);
									pipe1.setUpper2(time1 + deltat);

								}

								// System.out.println("\t\t\t add new node");
								// System.out.println("\t\t\t\t[("+pipe1.getLower1()+","+pipe1.getLower2()+"),("+pipe1.getUpper1()+","+pipe1.getUpper2()+")]");

								if (pipe1.getLower1() <= pipe1.getLower2() && pipe1.getUpper1() <= pipe1.getUpper2()) {
									ArrayList temp = new ArrayList();
									temp.addAll(band2);
									temp.add(temp.size(), pipe1);

									/*
									 * if size increases, replace band1 by band2
									 * + pipe1
									 */
									Set<Pipe> keyset = bandlist1.keySet();
									Iterator<Pipe> it = keyset.iterator();
									// System.out.println("\t\t check
									// bandlist1");
									boolean stop = false;
									while (it.hasNext() && !stop) {
										Pipe p1 = it.next();
										// System.out.println("\t\t\t\t[("+p1.getLower1()+","+p1.getLower2()+"),("+p1.getUpper1()+","+p1.getUpper2()+")]");
										if ((p1.getLower1() == pipe1.getLower1())
												&& (p1.getLower2() == pipe1.getLower2())
												&& (p1.getUpper1() == pipe1.getUpper1())
												&& (p1.getUpper2() == pipe1.getUpper2())) {
											stop = true;
											// System.out.println("\t replace
											// band1 w. band2");
											ArrayList band1 = bandlist1.get(p1);
											if (band2.size() + 1 > band1.size()) {
												bandlist1.put(p1, temp);
											}

										}

									}

									if (!stop) {
										bandlist1.put(pipe1, temp);
										// System.out.println("\t add new
										// band1");
									}

									// System.out.println("\t current bandlist
									// size \t"+bandlist1.size());

									if (temp.size() > maxLength) {
										maxLength = temp.size();
									}
									// System.out.println("\t max length
									// \t"+maxLength);
								}

							}
						}
					}
				}
			}
		}
		// System.out.println("\t maxlength: "+maxLength);
		// print out pipe w. maxLength
		for (int j = 0; j < bandlists.size(); j++) {
			// System.out.println("\t position "+j);
			HashMap<Pipe, ArrayList> bandlist = bandlists.get(j);
			for (Map.Entry<Pipe, ArrayList> e : bandlist.entrySet()) {
				Pipe pipe = e.getKey();
				ArrayList band = e.getValue();
				if (band.size() == maxLength) {
					// indexlist to store record id in lib
					ArrayList indexs = new ArrayList();
					for (int m = 0; m < band.size(); m++) {

						Pipe p = (Pipe) band.get(m);
						int id = p.getId();
						indexs.add(indexs.size(), id);
						System.out.println("\t\t[(" + p.getLower1() + "," + p.getLower2() + "),(" + p.getUpper1() + ","
								+ p.getUpper2() + ")] \t" + p.getId());
					}
					printPipe(band, releaselist, indexs);
				}

			}
		}

	}

	private void printPipe(ArrayList band, LinkedHashMap<Integer, ReleaseLabel> releaselist, ArrayList indexs) {
		// TODO Auto-generated method stub
		System.out.println("index: " + indexs);
		System.out.println(
				"\t id \t label \t release \t artist \t country \t catalog \t lower1 \t lower2 \t year \t upper1 \t upper2 \t detected_year \t true_year \t format \t format_description  \t partition id \t if record is detected as error \t if records is indeed wrong  \t true_country \t true_release \t true_label \t true_catno \t true_format \t label id");

		for (Map.Entry<Integer, ReleaseLabel> e : releaselist.entrySet()) {
			int index = e.getKey();
			ReleaseLabel relabel = e.getValue();
			String year_distance = String.valueOf(Math.abs(relabel.getDate() - relabel.getNew_date()));

			int year = relabel.getDate();
			String description = relabel.getFormat_description();
			String cat = relabel.getCatno();

			if (year_distance.equals("0"))
				year_distance = "";
			else
				year_distance = "F";
			if (indexs.contains(index) && year > 0) {
				Pipe p = findPipe(band, index);
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
						+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getCatno() + "\t" + p.getLower1() + "\t" + p.getLower2() + "\t" + relabel.getDate()
						+ "\t" + p.getUpper1() + "\t" + p.getUpper2() + "\t" + relabel.getDate() + "\t"
						+ relabel.getNew_date() + "\t" + relabel.getFormat() + "\t" + relabel.getFormat_description()
						+ "\t" + relabel.getPartition_id() + "\t \t" + year_distance + "\t" + relabel.getNew_country()
						+ "\t" + relabel.getNew_release() + "\t" + relabel.getNew_label() + "\t"
						+ relabel.getNew_catno() + "\t" + relabel.getNew_format() + "\t" + relabel.getCluster_id());
				if (year_distance.equals("F"))
					fn += 1;
				else
					tn += 1;
			} else {

				/*
				 * Detect the index range
				 */
				String yearRange = "";

				int lowerIndex = 0;
				lowerIndex = findLowerYearBound(index, indexs);
				int upperIndex = 0;
				upperIndex = findUpperYearBound(index, indexs);

				/*
				 * detect the year range
				 */
				int lowerYear = -1;
				lowerYear = getReleaseYear(lowerIndex, releaselist);
				int upperYear = -1;
				upperYear = getReleaseYear(upperIndex, releaselist);

				if (lowerYear == upperYear) {
					if (lowerYear == -1)
						yearRange = "unknown";
					else
						yearRange = String.valueOf(lowerYear);
				} else {
					if (lowerYear == -1)
						yearRange = "<=" + upperYear;
					else if (upperYear == -1)
						yearRange = ">=" + lowerYear;
					else if (upperYear > lowerYear)
						yearRange = "[" + String.valueOf(lowerYear) + ", " + String.valueOf(upperYear) + "]";
					else if (upperYear < lowerYear)
						yearRange = "[" + String.valueOf(upperYear) + ", " + String.valueOf(lowerYear) + "]";
				}

				// if(relabel.getNew_date()>0&&relabel.getDate()==0)
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
						+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getCatno() + "\t \t \t" + relabel.getDate() + "\t \t \t" + yearRange + "\t"
						+ relabel.getNew_date() + "\t" + relabel.getFormat() + "\t" + relabel.getFormat_description()
						+ "\t" + relabel.getPartition_id() + "\t F \t" + year_distance + "\t" + relabel.getNew_country()
						+ "\t" + relabel.getNew_release() + "\t" + relabel.getNew_label() + "\t"
						+ relabel.getNew_catno() + "\t" + relabel.getNew_format() + "\t" + relabel.getCluster_id());
				// else
				// System.out.println(index+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getCountry()+"\t"+relabel.getCatno()+"\t"+relabel.getDate()+"\t"+relabel.getFormat()+"\t"+relabel.getFormat_description()+"\t"+relabel.getPartition_id()+"\t
				// F
				// \t"+year_distance+"\t"+relabel.getNew_date()+"\t"+relabel.getNew_country()+"\t"+relabel.getNew_release()+"\t"+relabel.getNew_label()+"\t"+relabel.getNew_catno()+"\t"+relabel.getNew_format()+"\t"+yearRange+"\t"+relabel.getCluster_id());

				if (year_distance.equals("F"))
					tp += 1;
				else
					fp += 1;

			}

		}

	}

	private Pipe findPipe(ArrayList band, int index) {
		// TODO Auto-generated method stub
		Pipe p = new Pipe();
		int i = 0;
		boolean stop = false;
		while (!stop && i < band.size()) {
			Pipe pipe = (Pipe) band.get(i);
			if (pipe.getId() == index) {
				p = pipe;
				stop = true;
			}
			i++;
		}
		return p;
	}

	public void findLIS(int deltat) {
		// TODO Auto-generated method stub

		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;
		repress_missing = 0;
		original_missing = 0;

		for (int count = 0; count < finalcatalists.size(); count++) {

			System.out.println("Process partition \t" + count + "\n");
			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			int max_deltat = deltat;

			// while(deltat <= max_deltat){
			System.out.println("\t delta year = " + deltat);

			errorDetection(timelist, catalist, releaselist, deltat);
			// deltat ++;
			// }

		}

		System.out.println("LIS evaluation \t fp \t fn \t tp \t tn");
		System.out.println("\t" + fp + "\t" + fn + "\t" + tp + "\t" + tn);

		System.out.println("repress missing \t original missing");
		System.out.println(repress_missing + "\t" + original_missing);
	}

	private ArrayList<Integer> errorDetection(LinkedHashMap<Integer, Integer> timelist,
											  LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, ReleaseLabel> releaselist, int deltat) {
		// TODO Auto-generated method stub

		ArrayList<Integer> lib = new ArrayList<Integer>();
		int n = timelist.size();

		/*
		 * 1. a size array to keep track of the longest LIS ending with current
		 * position 2. an accordingly string array to keep track of the path for
		 * printing out 3. index array list to keep the entry index of each path
		 */
		String[] paths = new String[n];
		int[] sizes = new int[n];
		ArrayList<ArrayList> indexs = new ArrayList<ArrayList>();

		/*
		 * 1. assign the initial values to each path/size, by setting size to 1
		 * and path equal to the value, i.e., initially each path
		 * starting/ending with its current position exclude missing year (year
		 * = 0)
		 */
		for (int i = 0; i < n; i++) {
			if (timelist.get(i) == 0) { // if time = 0, size = 0
				sizes[i] = 0;
				paths[i] = "";
				ArrayList<Integer> list = new ArrayList<Integer>();
				indexs.add(i, list);
			} else {
				sizes[i] = 1;
				paths[i] = timelist.get(i) + " ";
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(list.size(), i);
				indexs.add(i, list);
			}

		}
		// System.out.println("indexs \t"+indexs);

		// before starting the loop, define a support variable maxLength to keep
		// track
		int maxLength = 1;

		for (int i = 1; i < n; i++) { // loop starts from 2nd position
			if (timelist.get(i) > 0) { // exclude sequence ending with 0
				for (int j = 0; j < i; j++) {
					if (timelist.get(j) > 0) {
						// check if appending current index to the previous
						// subsequence: 1 current > previous ending, and size is
						// increasing
						// if(timelist.get(i) >= timelist.get(j) && sizes[i] <
						// sizes[j] +1){ // increasing sequence
						if ((timelist.get(i) + deltat) >= timelist.get(j) && sizes[i] < (sizes[j] + 1)) { // increasing
							// band

							// if yes, update sizes and path
							sizes[i] = sizes[j] + 1;
							paths[i] = paths[j] + timelist.get(i) + " ";
							ArrayList<Integer> list = indexs.get(j);
							ArrayList<Integer> temp = new ArrayList<Integer>();
							temp.addAll(list);
							temp.add(temp.size(), i);
							indexs.remove(i);
							indexs.add(i, temp);
							// System.out.println("\t\t"+paths[i]+"\t"+indexs);

							// append current values to end
							// update maxLength if necessary
							if (maxLength < sizes[i])
								maxLength = sizes[i];
						}
					}

				}
			}

		}

		// count number of LIS
		int number = 0;
		for (int i = 0; i < n; i++) {
			if (sizes[i] == maxLength) {
				number += 1;
			}

		}
		// scan size array again to print out path when size matches MaxLength
		if (number > 0) {
			for (int i = 0; i < n; i++) {
				if (sizes[i] == maxLength) {
					// System.out.println("LIS \t"+paths[i]);
					// System.out.println("LIS index \t"+indexs.get(i));
					errPrintLIS(releaselist, indexs.get(i));
					lib = indexs.get(i);
					// System.out.println();
				}

			}
		}

		return lib;

	}

	private void errPrintLIS(LinkedHashMap<Integer, ReleaseLabel> releaselist, ArrayList indexs) {
		// TODO Auto-generated method stub
		// System.out.println("\t release size \t"+releaselist.size());
		System.out.println(
				"\t id \t label \t release \t artist \t country \t catalog \t year \t detected_year \t true_year \t format \t format_description  \t partition id \t if record is detected as error \t if records is indeed wrong  \t true_country \t true_release \t true_label \t true_catno \t true_format \t label id");

		for (Map.Entry<Integer, ReleaseLabel> e : releaselist.entrySet()) {
			int index = e.getKey();
			ReleaseLabel relabel = e.getValue();
			String year_distance = String.valueOf(Math.abs(relabel.getDate() - relabel.getNew_date()));

			int year = relabel.getDate();
			String description = relabel.getFormat_description();
			String cat = relabel.getCatno();
			if (year == 0 && !cat.equals("NONE")) {
				if (description.contains("Repress") || description.contains("Reissue"))
					repress_missing++;
				else {
					original_missing++;
					// System.out.println(index+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id()+"\t"+relabel.getPartition_id()+"\t
					// F
					// \t"+year_distance+"\t"+relabel.getNew_date()+"\t"+relabel.getNew_country()+"\t"+relabel.getNew_release()+"\t"+relabel.getNew_label()+"\t"+relabel.getNew_catno()+"\t"+relabel.getNew_format());
				}
			}

			if (year_distance.equals("0"))
				year_distance = "";
			else
				year_distance = "F";
			if (indexs.contains(index) && year > 0) {
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
						+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getCatno() + "\t" + relabel.getDate() + "\t" + relabel.getDate() + "\t"
						+ relabel.getNew_date() + "\t" + relabel.getFormat() + "\t" + relabel.getFormat_description()
						+ "\t" + relabel.getPartition_id() + "\t \t" + year_distance + "\t" + relabel.getNew_country()
						+ "\t" + relabel.getNew_release() + "\t" + relabel.getNew_label() + "\t"
						+ relabel.getNew_catno() + "\t" + relabel.getNew_format() + "\t" + relabel.getCluster_id());
				if (year_distance.equals("F"))
					fn += 1;
				else
					tn += 1;
			} else {

				/*
				 * Detect the index range
				 */
				String yearRange = "";

				int lowerIndex = 0;
				lowerIndex = findLowerYearBound(index, indexs);
				int upperIndex = 0;
				upperIndex = findUpperYearBound(index, indexs);

				/*
				 * detect the year range
				 */
				int lowerYear = -1;
				lowerYear = getReleaseYear(lowerIndex, releaselist);
				int upperYear = -1;
				upperYear = getReleaseYear(upperIndex, releaselist);

				if (lowerYear == upperYear) {
					if (lowerYear == -1)
						yearRange = "unknown";
					else
						yearRange = String.valueOf(lowerYear);
				} else {
					if (lowerYear == -1)
						yearRange = "<=" + upperYear;
					else if (upperYear == -1)
						yearRange = ">=" + lowerYear;
					else if (upperYear > lowerYear)
						yearRange = "[" + String.valueOf(lowerYear) + ", " + String.valueOf(upperYear) + "]";
					else if (upperYear < lowerYear)
						yearRange = "[" + String.valueOf(upperYear) + ", " + String.valueOf(lowerYear) + "]";
				}

				// if(relabel.getNew_date()>0&&relabel.getDate()==0)
				System.out.println(index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
						+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getCatno() + "\t" + relabel.getDate() + "\t" + yearRange + "\t"
						+ relabel.getNew_date() + "\t" + relabel.getFormat() + "\t" + relabel.getFormat_description()
						+ "\t" + relabel.getPartition_id() + "\t F \t" + year_distance + "\t" + relabel.getNew_country()
						+ "\t" + relabel.getNew_release() + "\t" + relabel.getNew_label() + "\t"
						+ relabel.getNew_catno() + "\t" + relabel.getNew_format() + "\t" + relabel.getCluster_id());
				// else
				// System.out.println(index+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getCountry()+"\t"+relabel.getCatno()+"\t"+relabel.getDate()+"\t"+relabel.getFormat()+"\t"+relabel.getFormat_description()+"\t"+relabel.getPartition_id()+"\t
				// F
				// \t"+year_distance+"\t"+relabel.getNew_date()+"\t"+relabel.getNew_country()+"\t"+relabel.getNew_release()+"\t"+relabel.getNew_label()+"\t"+relabel.getNew_catno()+"\t"+relabel.getNew_format()+"\t"+yearRange+"\t"+relabel.getCluster_id());

				if (year_distance.equals("F"))
					tp += 1;
				else
					fp += 1;

			}

		}

	}

	private int findUpperYearBound(int index, ArrayList indexs) {
		// TODO Auto-generated method stub
		int upperIndex = -1;
		boolean stop = false;
		int i = 0;
		while (i < indexs.size() && !stop) {
			int localIndex = (Integer) indexs.get(i);
			if (localIndex > index) {
				upperIndex = localIndex;
				stop = true;
			}

			i++;
		}
		return upperIndex;
	}

	private int getReleaseYear(int index, LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		int year = -1;

		if (releaselist.containsKey(index)) {
			ReleaseLabel relabel = releaselist.get(index);
			year = relabel.getDate();
		}

		return year;
	}

	private int findLowerYearBound(int index, ArrayList indexs) {
		// TODO Auto-generated method stub

		int lowerIndex = -1;
		boolean stop = false;
		int i = 0;
		while (i < indexs.size() && !stop) {
			int localIndex = (Integer) indexs.get(i);
			if (localIndex < index)
				lowerIndex = localIndex;
			else
				stop = true;
			i++;
		}
		// System.out.println(index+"\t"+lowerIndex+"\t"+indexs);
		return lowerIndex;
	}

	private static void localStardardPartition(ArrayList<String> catalist, ArrayList<Integer> timelist,
											   ArrayList<ReleaseLabel> releaselist, int k) {
		// TODO Auto-generated method stub
		/*
		 * collect k records
		 */
		ArrayList<String> tempcatalist = new ArrayList<String>();
		ArrayList<Integer> temptimelist = new ArrayList<Integer>();
		ArrayList<ReleaseLabel> tempreleaselist = new ArrayList<ReleaseLabel>();
		int count = 0;

		for (int i = 0; i < catalist.size(); i++) {
			String catano = catalist.get(i);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(i);
			int year = timelist.get(i);

			tempcatalist.add(tempcatalist.size(), catano);
			temptimelist.add(temptimelist.size(), year);
			tempreleaselist.add(tempreleaselist.size(), relabel);
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

		// standardPartition(tempcatalist, temptimelist, tempreleaselist);

	}

	private static long standardPartition(ArrayList<String> catalist, ArrayList<Integer> timelist,
										  ArrayList<ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		/*
		 * scan the catalist to collect gap between every two catalog no. the
		 * gap is sorted and strored in arraylist
		 */
		ArrayList gaplist = new ArrayList();
		gaplist = sortGaps(catalist, releaselist);

		// System.out.println(gaplist.size()+" catalog distances"+"\t"+gaplist);

		long filter = 0;

		/*
		 * filter w. cutoff c = mean + 3 * deviation
		 */
		filter = getESD(gaplist);

		/*
		 * filter w. cutoff c = median + 3 * deviation
		 */
		// filter = getMedianCutoff(gaplist);

		/*
		 * filter w. truncated mean; discard top and bottom k portion of records
		 */
		// double k = 0;// 0.125;
		// filter = getTruncatedCutoff(gaplist, k);

		filter = Math.max(filter, 100);

		/** generate blocks without any filter */
		// filter = (Long) gaplist.get(0) + 1;
		// System.out.println("filter =\t" + filter);

		return filter;

	}

	private static ArrayList sortGaps(ArrayList<String> catalist, ArrayList<ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		ArrayList gaplist = new ArrayList();
		Catalog previous_cat = new Catalog();
		// if(catalist.size()>1){
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

				// System.out.println("\t"+value1+"\t"+value2+"\t"+order);

				/*
				 * sort insert into arraylist
				 */
				sortInsert(order, gaplist);

				/*
				 * simply add gaps without sorting
				 */
				// gaplist.add(gaplist.size(), order);
			}

			// System.out.println("\t"+catano+"\t"+order);
			previous_cat = cat;

		}
		return gaplist;
	}

	private static long getTruncatedCutoff(ArrayList<Long> gaplist, double k) {
		// TODO Auto-generated method stub
		long filter = 0;

		int size = gaplist.size();
		int min = (int) Math.round(size * k);
		int max = (int) Math.round((size * (1 - k)));
		// System.out.println("\t"+size+"\t"+size*k+"\t"+min+"\t"+max);

		// min = Math.min(min, 1);
		// max = Math.max(max, size-1);
		/*
		 * if(min==0&&min!=(max-1)){ min = 1; max = size - min; }
		 */

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
			// System.out.println("\t\t"+v);
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
		// System.out.println("mean: \t"+mean);
		// System.out.println("deviration: \t"+dvt);
		// System.out.println("cutoff: \t"+filter);

		return filter;
	}

	private static long getMedianCutoff(ArrayList<Long> gaplist) {
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

		// System.out.println("median: \t"+median+"\t deviration:
		// \t"+deviation+"\t cutoff: \t"+filter);
		return filter;
	}

	private static long getESD(ArrayList gaplist) {
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
		// System.out.println("mean \t" + mean + "\t" + dvt + "\t" + filter);

		return filter;
	}

	private static void addBlock(ArrayList<String> catalist, ArrayList<ReleaseLabel> releaselist,
								 ArrayList<Integer> timelist) {
		// TODO Auto-generated method stub

		/*
		 * initialize hash-maps for catalog, year, and release
		 */
		LinkedHashMap<Integer, String> tempcatalist = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, ReleaseLabel> tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();
		LinkedHashMap<Integer, Integer> temptimelist = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < catalist.size(); index++) {
			String catano = catalist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);
			int year = timelist.get(index);

			/*
			 * add value into three temp hash-map
			 */
			int size = tempcatalist.size();
			tempcatalist.put(size, catano);
			temptimelist.put(size, year);
			tempreleaselist.put(size, relabel);
		}

		if (!tempcatalist.isEmpty()) {
			int size = finalcatalists.size();
			finalcatalists.add(size, tempcatalist);
			finaltimelists.add(size, temptimelist);
			finalreleaselists.add(size, tempreleaselist);
		}

		// printMatrix(catalist, timelist);
	}

	private static void filterWithGap(long filter, ArrayList<String> catalist, ArrayList<ReleaseLabel> releaselist,
									  ArrayList<Integer> timelist) {
		// TODO Auto-generated method stub

		String idset = "";
		String partition_id = "";
		String previous_cata = "";
		int previous_year = 0;

		/*
		 * initialize hash-maps for catalog, year, and release
		 */
		LinkedHashMap<Integer, String> tempcatalist = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, ReleaseLabel> tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();
		LinkedHashMap<Integer, Integer> temptimelist = new LinkedHashMap<Integer, Integer>();

		for (int index = 0; index < catalist.size(); index++) {
			String catano = catalist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);
			int year = timelist.get(index);
			long gap = 0;
			int distance = 0;

			if (!previous_cata.equals("")) {

				gap = computeGap(previous_cata, catano);
				distance = year - previous_year;

				if (gap > filter) {
					/*
					 * cutoff partition; add records of previous part into
					 * final_xxx_list; empty temp_xxx_list;
					 */
					int size = gapCataLists.size();
					gapCataLists.add(size, tempcatalist);
					gapTimeLists.add(size, temptimelist);
					gapReleaseLists.add(size, tempreleaselist);

					tempcatalist = new LinkedHashMap<Integer, String>();
					temptimelist = new LinkedHashMap<Integer, Integer>();
					tempreleaselist = new LinkedHashMap<Integer, ReleaseLabel>();
				}
			}

			// System.out.println("\t" + relabel.getId() + "\t" +
			// relabel.getLabel() + "\t" + relabel.getRelease() + "\t"
			// + relabel.getStrartist() + "\t" + relabel.getStrextrartist() +
			// "\t" + relabel.getGenreslist() + "\t"
			// + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" +
			// relabel.getDate() + "\t" + distance
			// + "\t" + relabel.getCatno() + "\t" + gap + "\t" +
			// relabel.getFormat() + "\t" + relabel.getQty()
			// + "\t" + relabel.getFormat_description() + "\t" +
			// relabel.getCluster_id() + "\t"
			// + relabel.getPartition_id());

			/*
			 * add value into three temp hash-map
			 */
			int size = tempcatalist.size();
			tempcatalist.put(size, catano);
			temptimelist.put(size, year);
			tempreleaselist.put(size, relabel);

			previous_cata = catano;
			previous_year = year;
		}

		if (!tempcatalist.isEmpty()) {
			int size = gapCataLists.size();
			gapCataLists.add(size, tempcatalist);
			gapTimeLists.add(size, temptimelist);
			gapReleaseLists.add(size, tempreleaselist);
		}

		// printMatrix(catalist, timelist);
	}

	private static long computeGap(String previous_cata, String catano) {
		// TODO Auto-generated method stub
		long gap = 0;
		Catalog c1 = new Catalog(previous_cata);
		Catalog c2 = new Catalog(catano);

		LinkedHashMap<Integer, Integer> ints1 = c1.getInts();
		LinkedHashMap<Integer, Integer> ints2 = c2.getInts();

		// System.out.println(ints1+"\t"+ints2);
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

	private static void sortInsert(long order, ArrayList gaplist) {
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

	private static void printList(ArrayList<LinkedHashMap<Integer, String>> finalcatalists,
								  ArrayList<LinkedHashMap<Integer, Integer>> finaltimelists,
								  ArrayList<LinkedHashMap<Integer, ReleaseLabel>> finalreleaselists) {
		// TODO Auto-generated method stub

		System.out.println(finalcatalists.size() + "\t" + finaltimelists.size() + "\t" + finalreleaselists.size());

		for (int i = 0; i < finalcatalists.size(); i++) {
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(i);
			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(i);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(i);

			// System.out.println("\t partition_id \t catalog");
			System.out.println(
					"\t partition_id \t id \t label \t release \t artist \t country \t year \t catalog \t format  \t label id \t partition id (ground truth)");
			int previous_year = 0;
			for (int index = 0; index < catalist.size(); index++) {
				String catano = catalist.get(index);
				int year = timelist.get(index);
				ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

				// System.out.println("\t"+i+"\t"+catano);
				int distance = year - previous_year;
				System.out.println("\t" + i + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
						+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t"
						+ relabel.getCluster_id() + "\t" + relabel.getPartition_id());

				// System.out.println("\t"+i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id());
				previous_year = year;
			}

			// printMatrix(catalist, timelist);
		}

	}

	private static void printMatrix(LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, Integer> timelist) {
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
		// for(Map.Entry<Integer, String> e:catalist.entrySet()){
		for (int i = 0; i < catalist.size(); i++) {
			String cata = catalist.get(i);
			int year = timelist.get(i);
			ArrayList list = new ArrayList();
			if (tempmap.containsKey(cata))
				list = tempmap.get(cata);
			if (!list.contains(year))
				list.add(list.size(), year);
			tempmap.put(cata, list);
		}
		// System.out.println(tempmap);

		/*
		 * scan each row of the matrix
		 */
		for (Map.Entry<String, ArrayList> e : tempmap.entrySet()) {
			// for(int n=0; n<catalist.size(); n++){
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

				// System.out.println("\t"+label+"\t"+release+"\t"+year+"\t"+catano);

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
				LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog
				// pairs
				// in
				// time
				// order
				LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time
				// pairs
				// in
				// time
				// order
				LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id
				// -
				// record
				// pairs
				// in
				// time
				// order

				if (catalists.containsKey(length) && timelists.containsKey(length)
						&& releaselists.containsKey(length)) {
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
				// System.out.println("\t"+position+"\t"+year+"\t"+catano);

			}

			// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());

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

		LinkedHashMap<Integer, String> catalist = new LinkedHashMap<Integer, String>(); // id-catalog
		// pairs
		// in
		// time
		// order
		LinkedHashMap<Integer, Integer> timelist = new LinkedHashMap<Integer, Integer>(); // id-time
		// pairs
		// in
		// time
		// order
		LinkedHashMap<Integer, ReleaseLabel> releaselist = new LinkedHashMap<Integer, ReleaseLabel>(); // id
		// -
		// record
		// pairs
		// in
		// time
		// order

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getDate();
				int clusterid = relabel.getCluster_id();
				String catano = relabel.getCatno();

				// System.out.println("\t"+label+"\t"+release+"\t"+year+"\t"+catano);

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

				// System.out.println("\t"+position+"\t"+year+"\t"+catano);
				index++;

			}

			// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());

		}

		if (!catalist.isEmpty() && !releaselist.isEmpty() && !timelist.isEmpty()) {
			/*
			 * order records by catalog order
			 */
			ordertempCatalog(catalist, timelist, releaselist);

		}

	}

	private void orderCatalog(HashMap<Integer, LinkedHashMap<Integer, String>> catalists,
							  HashMap<Integer, LinkedHashMap<Integer, Integer>> timelists,
							  HashMap<Integer, LinkedHashMap<Integer, ReleaseLabel>> releaselists) {
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
			 *
			 * String previous_catano = ""; for(int m=0; m< timelist.size();
			 * m++){ int year = timelist.get(m); String catno = catalist.get(m);
			 * ReleaseLabel relabel = (ReleaseLabel) releaselist.get(m);
			 * //System.out.println("\t"+m+"\t"+year+"\t"+catno);
			 *
			 * int order = 0; if(!previous_catano.equals("")){ order =
			 * previous_catano.compareTo(catno);
			 * //System.out.println(previous_catano+"\t"+catno+"\t"+order); }
			 * if(order >0) System.out.println(); previous_catano = catno;
			 * System.out.println("\t"+relabel.getId()+"\t"+relabel.getLabel()+
			 * "\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+
			 * relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+
			 * relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.
			 * getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+
			 * relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+
			 * relabel.getCluster_id());
			 *
			 * }
			 */

			for (Map.Entry<Integer, String> e : catalist.entrySet()) {
				int index = e.getKey();
				String catano = e.getValue();
				// System.out.println("!!! process catano \t"+catano+"\t w index
				// \t"+index);
				if (temp.isEmpty()) {
					// System.out.println("\t\t add catano as new node to temp
					// \t"+temp.size()+"\t"+catano);
					temp.add(temp.size(), index);
				} else {
					boolean stop = false;
					int i = 0;
					// System.out.println("\t insert cata to temp list
					// \t"+temp);
					while (i < temp.size() && !stop) {
						int index1 = (Integer) temp.get(i);
						String catano1 = catalist.get(index1);
						// System.out.println("\t"+catano+"\t"+catano1);
						int order = catano.compareTo(catano1); // if order < 0,
						// it means
						// catano <
						// catano1

						if (order < 0) {
							stop = true;
							temp.add(i, index);
							// System.out.println("\t\t add catano to
							// \t"+i+"\t"+catano);
						}

						i++;
					}
					if (stop == false) {
						// System.out.println("\t\t add catano to the end
						// \t"+temp.size()+"\t"+catano);
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
					// System.out.println("\t"+previous_catano+"\t"+catano+"\t"+score);
				}

				int distance = index - previous_position;
				if (distance < 0)
					System.out.println();
				// System.out.println("\t\t"+index+"\t"+catano+"\t"+year);
				System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease()
						+ "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t"
						+ relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t"
						+ relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t"
						+ relabel.getQty() + "\t" + relabel.getFormat_description() + "\t" + relabel.getCluster_id());
				previous_position = index;
				previous_catano = catano;
			}
		}

	}

	private void ordertempCatalog(LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, Integer> timelist,
								  LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		ArrayList temp = new ArrayList();

		for (Map.Entry<Integer, String> e : catalist.entrySet()) {
			int index = e.getKey();
			String catano = e.getValue();
			if (temp.isEmpty()) {
				// System.out.println("\t\t"+temp.size()+"\t"+catano);
				temp.add(temp.size(), index);
			} else {
				boolean stop = false;
				int i = 0;
				while (i < temp.size() && !stop) {
					int index1 = (Integer) temp.get(i);
					String catano1 = catalist.get(index1);
					int order = catano.compareTo(catano1); // if order < 0, it
					// means catano <
					// catano1
					if (order < 0) {
						stop = true;
						temp.add(i, index);
						// System.out.println("\t\t"+i+"\t"+catano);
					}

					i++;
				}
				if (stop == false) {
					// System.out.println("\t\t"+temp.size()+"\t"+catano);
					temp.add(temp.size(), index);
				}

			}
		}

		/*
		 * for(int m=0; m< timelist.size(); m++){ int year = timelist.get(m);
		 * String catno = catalist.get(m);
		 * System.out.println("\t"+m+"\t"+year+"\t"+catno); }
		 */

		int previous_position = 0;
		for (int j = 0; j < temp.size(); j++) {
			int index = (Integer) temp.get(j);
			String catano = catalist.get(index);
			int year = timelist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

			int distance = index - previous_position;
			if (distance < 0)
				System.out.println();
			// System.out.println("\t\t"+index+"\t"+catano+"\t"+year);
			System.out.println("\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t"
					+ relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t"
					+ relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t"
					+ relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getQty() + "\t"
					+ relabel.getFormat_description() + "\t" + relabel.getCluster_id());
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
						// printFrequency(distances);
						// printFrequency(frequency);

						/*
						 * this function is to count how often a label produce a
						 * release, i.e., delta t = t_n - t_{n-1}
						 *
						 * System.out.println(); countGaps(frequency, gaps);
						 */
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
					// System.out.println("\t add count \t"+count+"\t to year
					// \t"+year);

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

				// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());
			}

		}
		if (!frequency.isEmpty()) {
			// printFrequency(frequency);
			// printFrequency(distances);

			/*
			 * this function is to count how often a label produce a release,
			 * i.e., delta t = t_n - t_{n-1}
			 *
			 * System.out.println(); countGaps(frequency, gaps);
			 */

			updateSize(size, sizes);
		}

		/*
		 * print gap distribution: x axis is year; y axis is # of gaps
		 */
		// System.out.println("gap distribution");
		// printFrequency(gaps);

		/*
		 * print size distribution
		 */
		// System.out.println("size distribution");
		// printFrequency(sizes);
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

			// select id, release_name, country, year, label_name, discogs_name,
			// discogs_country, discogs_date, discogs_label
			// 1, 2, 3, 4, 5, 6, 7, 8, 9
			while (result.next()) {
				int id = result.getInt(1);
				String release1 = result.getString(2);
				String country1 = result.getString(3);
				// country = AbstractBlock.cleanValue(country);
				int year1 = result.getInt(4);
				String label1 = result.getString(5);
				String release2 = result.getString(6);
				String country2 = result.getString(7);
				String year2 = result.getString(8);
				String label2 = result.getString(9);
				// String[] split = year2.split("-");

				/*
				 * if(year1!=0&&!year2.equals("")&&!year2.equals("?")&&!year2.
				 * equals("None")){ year2 = year2.substring(0, 4); int distance
				 * = Math.abs(year1-Integer.parseInt(year2));
				 * if(distance>0&&label1.equals(label2)){
				 * //System.out.println(i+"\t"+id+"\t"+release1+"\t"+label1+"\t"
				 * +country1+"\t"+year1+"\t"+release2+"\t"+label2+"\t"+country2+
				 * "\t"+year2+"\t"+distance); if (i==0) musicbz_ids =
				 * String.valueOf(id); else musicbz_ids +=
				 * ", "+String.valueOf(id);
				 *
				 * if(i==0) discogs_labels = "'"+label2+"'"; else
				 * if(!discogs_labels.contains(label2)) discogs_labels +=
				 * ", '"+label2+"'";
				 *
				 * if(!labelist.containsKey(id)) labelist.put(id, label2); i++;
				 * }
				 *
				 * }
				 */

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

			// select id, release, country, date, label, format, catalog_number,
			// artist, extra_artist, discogs_id
			// 1, 2, 3, 4, 5, 6, 7, 8, 9 10
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
				 * label = AbstractBlock.cleanValue(label); release =
				 * AbstractBlock.cleanValue(release); artist =
				 * AbstractBlock.cleanValue(artist); extra =
				 * AbstractBlock.cleanValue(extra); country =
				 * AbstractBlock.cleanValue(country);
				 */

				// System.out.println(i+"\t"+id+"\t"+release+"\t"+label+"\t"+country+"\t"+year+"\t"+artist+"\t"+extra+"\t"+catno+"\t"+format+"\t"+discogsid);

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
			// System.out.println(i);
			st.close();
		}

		ConnectionPool.putConnection(con);

		for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
			int id = e.getKey();
			ReleaseLabel relabel = e.getValue();
			// System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());
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
			// System.out.println(sql);
			ResultSet result = st.executeQuery(sql);

			// select id, release, genres, style, country, date, name,
			// cluster_id
			while (result.next()) {
				String name = result.getString(1);

				if (i == 0)
					labels = "'" + name + "'";
				else
					labels += ", '" + name + "'";
				i++;

				// System.out.println(i+"\t"+name+"\t"+cluster_id+"\t"+clusters);

			}
			st.close();
		}

		ConnectionPool.putConnection(con);

		return labels;
	}

	private ArrayList mergeLabels(ArrayList relabels, ArrayList mblabels) {
		// TODO Auto-generated method stub
		ArrayList data = new ArrayList();
		// data.addAll(relabels);
		/*
		 * get mblabel cluster_id
		 */
		for (int i = 0; i < mblabels.size(); i++) {
			ReleaseLabel r1 = (ReleaseLabel) mblabels.get(i);
			int id1 = r1.getId();
			int cluster1 = -1;
			// System.out.println("\t mblabels
			// \t"+id1+"\t"+clusterlist.get(id1));
			if (clusterlist.containsKey(id1)) {
				cluster1 = clusterlist.get(id1);
				r1.setCluster_id(cluster1);
				data.add(data.size(), r1);
				// System.out.println(i+"\t add
				// "+id1+"\t"+cluster1+"\t"+r1.getRelease()+"\t"+r1.getDate());
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
						// System.out.println("\t"+yr1+"\t"+yr2);
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

	/*
	 * public void processEntity(Block block, String entityType){ blocks = new
	 * LinkedHashMap<Integer, ArrayList> (); blockinvertedlist = new
	 * LinkedHashMap<Integer, ArrayList> (); blocklist = new HashMap<String,
	 * Integer>(); blockeylist = new HashMap<Integer, String>();
	 *
	 * if(entityType.equalsIgnoreCase("label")) block.processEntity(entityType,
	 * labels, blocks, blockinvertedlist, blocklist, blockeylist);
	 *
	 * else if(entityType.equalsIgnoreCase("releaselabel"))
	 * block.processEntity(entityType, relabels, blocks, blockinvertedlist,
	 * blocklist, blockeylist); }
	 *
	 * public void process(Block block){
	 *
	 * blocks = new LinkedHashMap<Integer, ArrayList> (); blockinvertedlist =
	 * new LinkedHashMap<Integer, ArrayList> (); blocklist = new HashMap<String,
	 * Integer>(); blockeylist = new HashMap<Integer, String>();
	 *
	 *
	 * block.process(artists, blocks, blockinvertedlist, blocklist,
	 * blockeylist);
	 *
	 * }
	 */

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

	private static void printPartition(LinkedHashMap<Integer, String> catalist,
									   LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub

		// System.out.println("\t partition_id \t catalog");
		System.out.println(
				"\t partition_id \t id \t label \t release \t artist \t country \t year \t catalog \t format  \t label id \t partition id (ground truth) \t groud_truth date");
		int previous_year = 0;
		for (int index = 0; index < catalist.size(); index++) {
			String catano = catalist.get(index);
			int year = timelist.get(index);
			ReleaseLabel relabel = (ReleaseLabel) releaselist.get(index);

			// System.out.println("\t"+i+"\t"+catano);
			int distance = year - previous_year;
			System.out.println("\t" + index + "\t" + relabel.getId() + "\t" + relabel.getLabel() + "\t"
					+ relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getCountry() + "\t"
					+ relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t"
					+ relabel.getCluster_id() + "\t" + relabel.getPartition_id() + relabel.getGround_truth());

			// System.out.println("\t"+i+"\t"+relabel.getId()+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getQty()+"\t"+relabel.getFormat_description()+"\t"+relabel.getCluster_id());
			previous_year = year;
		}

		// printMatrix(catalist, timelist);

	}

	public void AllPossibleWorldProcess(int deltat) {

		for (int count = 0; count < finalcatalists.size(); count++) {

			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			/*
			 * find the list of possible cutoff point, i.e., if r_i+1.T + delta
			 * t < r_i.T, r_i.T is a cutting point
			 */
			ArrayList<Integer> cutPoints = new ArrayList<Integer>();
			cutPoints = getCutPoints(timelist, deltat);

			int size = cutPoints.size();
			int numPSW = (int) Math.pow(2, size);

			if (size > 0) {
				System.out.println("Process partition \t" + count + "\n");

				printPartition(catalist, timelist, releaselist);

				printMatrix(catalist, timelist);

				System.out.println("Cut Points \t" + cutPoints);
				// System.out.println("Possible world \t"+numPSW);

				ArrayList<ArrayList> possibleWorlds = new ArrayList<ArrayList>(numPSW);
				possibleWorlds = getPossibleWorlds(numPSW, cutPoints);

				HashMap<ArrayList, ArrayList> profits = new HashMap<ArrayList, ArrayList>();

				for (int i = 0; i < possibleWorlds.size(); i++) {
					ArrayList<Integer> possibleWorld = possibleWorlds.get(i);

					ArrayList profit = new ArrayList();

					possibleWorld.add(possibleWorld.size(), timelist.size() - 1);

					// System.out.println("\t Possible world
					// \t"+i+"\t"+possibleWorld);

					double sumBenefit = 0;
					ArrayList<Integer> maxPossibleWorld = possibleWorlds.get(i);

					for (int j = 0; j < possibleWorld.size(); j++) {

						int currentIndex = possibleWorld.get(j);
						int previousIndex = -1;
						if (j - 1 >= 0)
							previousIndex = possibleWorld.get(j - 1);

						double maxLength = getMaxLength(previousIndex, currentIndex, cutPoints);

						double gain = processPartition(timelist, catalist, releaselist, previousIndex, currentIndex,
								deltat, profit);
						/*
						 * if(maxGain < gain){ maxGain = gain; maxPossibleWorld
						 * = possibleWorld; }
						 */

						double benefit = gain - maxLength;
						// System.out.println("\t gain \t maxlength \t benefit
						// \t"+gain+"\t"+maxLength+"\t"+benefit);

						sumBenefit += benefit;
					}

					// System.out.println("\t Possible world gain
					// \t"+possibleWorld+"\t"+sumBenefit);

					profit.add(0, sumBenefit);

					profits.put(possibleWorld, profit);

				}
				printProfits(profits);
			}
		}
	}

	private double getMaxLength(int previousIndex, int currentIndex, ArrayList<Integer> cutPoints) {
		// TODO Auto-generated method stub
		// System.out.println("Get Max Length
		// \t"+previousIndex+"\t"+currentIndex+"\t"+cutPoints);

		double maxLength = 0;

		for (int i = 0; i < cutPoints.size(); i++) {
			int index = cutPoints.get(i);
			if (index <= currentIndex) {
				if (index > previousIndex) {
					if (i - 1 >= 0) {
						int pindex = cutPoints.get(i - 1);
						int length = index - pindex;
						maxLength = Math.max(maxLength, length);
						// System.out.println("\t"+pindex+"\t"+index +
						// "\t"+length+"\t"+maxLength);
					} else if (i == 0) {
						int pindex = 0;
						int length = index - pindex + 1;
						maxLength = Math.max(maxLength, length);
						// System.out.println("\t"+pindex+"\t"+index +
						// "\t"+length+"\t"+maxLength);
					}
				}

				if (i == cutPoints.size() - 1 && index >= previousIndex) {
					int pindex = index;
					int length = currentIndex - pindex;
					maxLength = Math.max(maxLength, length);
					// System.out.println("\t"+pindex+"\t"+currentIndex +
					// "\t"+length+"\t"+maxLength);
				}
			}
		}
		return maxLength;
	}

	private void printProfits(HashMap<ArrayList, ArrayList> profits) {
		// TODO Auto-generated method stub
		/*
		 * System.out.println(
		 * "PossibleWorld \t EIB \t  LIB \t Error \t LIB - EIB \t LIB - Error \t LIB-EIB^2"
		 * ); for(Map.Entry<ArrayList, ArrayList> e : profits.entrySet()){
		 * ArrayList psw = e.getKey(); ArrayList profit = e.getValue();
		 *
		 * int LIB = (Integer) profit.get(0); int EIB = (Integer) profit.get(1);
		 * int Error = (Integer) profit.get(2);
		 * System.out.println(psw+"\t"+EIB+"\t"+LIB+"\t"+Error+"\t"+(LIB -
		 * EIB)+"\t"+(LIB-Error)+"\t"+(LIB-Math.pow(EIB, 2))); }
		 */

		double maxBenefit = 0;
		for (Map.Entry<ArrayList, ArrayList> e : profits.entrySet()) {
			ArrayList psw = e.getKey();
			ArrayList profit = e.getValue();
			double benefit = (Double) profit.get(0);
			maxBenefit = Math.max(maxBenefit, benefit);
		}

		System.out.println("PossibleWorld \t benefit");
		for (Map.Entry<ArrayList, ArrayList> e : profits.entrySet()) {
			ArrayList psw = e.getKey();
			ArrayList profit = e.getValue();
			double benefit = (Double) profit.get(0);
			if (benefit == maxBenefit)
				System.out.println(psw + "\t" + benefit);
		}
	}

	private double processPartition(LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, String> catalist,
									LinkedHashMap<Integer, ReleaseLabel> releaselist, int previousIndex, int currentIndex, int deltat,
									ArrayList<Integer> profit) {
		// TODO Auto-generated method stub
		// System.out.println("\t"+previousIndex+"\t"+currentIndex);
		LinkedHashMap<Integer, Integer> timelist1 = new LinkedHashMap<Integer, Integer>();
		LinkedHashMap<Integer, String> catalist1 = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, ReleaseLabel> releaselist1 = new LinkedHashMap<Integer, ReleaseLabel>();
		getSubTimelist(timelist, previousIndex, currentIndex, timelist1, catalist1, catalist, releaselist1,
				releaselist);

		ArrayList<Integer> lib1 = errorDetection(timelist1, catalist1, releaselist1, deltat);
		ArrayList<Integer> error1 = getLibError(lib1, timelist1);
		// System.out.println("Error index \t"+error1);

		int leb1 = getMaxLibError(lib1, timelist1);

		double gain1 = lib1.size() - error1.size();
		// gain1 = lib1.size() - leb1;
		// gain1 = lib1.size() - Math.pow(leb1, 2);
		// System.out.println("Gain: \t"+gain1);

		// System.out.println();
		/*
		 * if(profit.isEmpty()){ profit.add(0, lib1.size()); profit.add(1,
		 * leb1); profit.add(2, error1.size()); } else{
		 * if(profit.get(0)<lib1.size()) profit.set(0, lib1.size());
		 * if(profit.get(1)<leb1) profit.set(1, leb1);
		 * if(profit.get(2)<error1.size()) profit.set(2, error1.size()); }
		 */

		return gain1;
	}

	private ArrayList<ArrayList> getPossibleWorlds(int numPSW, ArrayList<Integer> cutPoints) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList> possibleWorlds = new ArrayList<ArrayList>(numPSW);
		int size = cutPoints.size();

		for (int i = 1; i <= numPSW; i++) {
			int num = i;
			ArrayList<Integer> digits = new ArrayList<Integer>();
			/*
			 * convert i of base 10 to value of base 2
			 */
			int index = 0;
			while (num != 0) {
				int remainder = num % 2;
				num = num / 2;
				digits.add(digits.size(), remainder);
				index++;
			}

			ArrayList possibleWorld = new ArrayList();
			for (int j = 0; j < digits.size(); j++) {
				int value = digits.get(j);
				if (value == 1 && j < size) {
					int cutPoint = cutPoints.get(j);
					possibleWorld.add(possibleWorld.size(), cutPoint);

				}

			}
			// System.out.println("\t"+i+"\t"+num+"\t"+digits+"\t"+possibleWorld);
			possibleWorlds.add(possibleWorlds.size(), possibleWorld);
		}

		return possibleWorlds;
	}

	public void ManualProcess(int deltat) throws Exception {

		for (int count = 0; count < finalcatalists.size(); count++) {

			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			// 23756261
			if (releaselist.get(0).getId() == 2883936) {
				System.out.println("Process block \t" + count + "\n");

				// printPartition(catalist, timelist, releaselist);
				System.out.println();
				// printMatrix(catalist, timelist);

				System.out.println();

				Block block = new Block(timelist);
				ArrayList<Integer> sequence = block.getSequence();

				/*
				 * find pieces, i.e., the list of possible cutoff point, i.e.,
				 * if r_i+1.T + delta t < r_i.T, r_i.T is a cutting point
				 */
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(temp.size(), -1);
				/* data 3 */
				temp.add(temp.size(), 101);
				temp.add(temp.size(), 129);
				temp.add(temp.size(), 142);
				temp.add(temp.size(), 175);
				temp.add(temp.size(), 317);
				temp.add(temp.size(), 318);
				temp.add(temp.size(), 324);
				temp.add(temp.size(), 345);

				/*
				 * data 6
				 *
				 * temp.add(temp.size(), 7); temp.add(temp.size(), 104);
				 * temp.add(temp.size(), 105); temp.add(temp.size(), 152);
				 * temp.add(temp.size(), 154); temp.add(temp.size(), 155);
				 * temp.add(temp.size(), 183); temp.add(temp.size(), 218);
				 * temp.add(temp.size(), 230);
				 */

				/*
				 * data 8
				 *
				 * temp.add(temp.size(), 32); temp.add(temp.size(), 42);
				 * temp.add(temp.size(), 82); temp.add(temp.size(), 84);
				 * temp.add(temp.size(), 86); temp.add(temp.size(), 153);
				 * temp.add(temp.size(), 154); temp.add(temp.size(), 174);
				 * temp.add(temp.size(), 185); temp.add(temp.size(), 186);
				 */

				ArrayList<Piece> pieces = new ArrayList<Piece>();
				pieces = block.piece(temp);// block.findPieces(0); //
				// block.findPieces(deltat);
				// //block.findPieces(0);

				/*
				 * intialize chopping X that starts&ends at each piece; G stores
				 * the gain of each chopping in X
				 */
				ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
				ArrayList<Double> gains = new ArrayList<Double>();

				double sum_gain = 0;

				for (int i = 0; i < pieces.size(); i++) {
					Piece piece = pieces.get(i);
					// System.out.println("piece:
					// ["+piece.getStart()+","+piece.getEnd()+"]");
					double gain = piece.computeGain(block.getSequence(), deltat);
					piece.setGain(gain);
					sum_gain += gain;
					System.out.println(
							"series: [" + piece.getStart() + "," + piece.getEnd() + "] \t" + gain + "\t" + sum_gain);

					// add piece i to chopping i
					ArrayList<Piece> chopping = new ArrayList<Piece>();
					chopping.add(chopping.size(), piece);

					// add chopping i to choppings; add gain i to gains
					choppings.add(i, chopping);
					gains.add(i, gain);
					gains.add(i, sum_gain);

				}

				/*
				 * find the best chopping from piece 0 to piece i
				 *
				 * for(int i=1; i< pieces.size(); i++){ // loop from the 2nd
				 * piece Piece current_piece = pieces.get(i);
				 * //System.out.println("process piece \t"+i+"\t"+current_piece.
				 * getStart()+" to "+current_piece.getEnd()); ArrayList<Piece>
				 * max_chopping = new ArrayList<Piece>(); double max_gain = 0;
				 *
				 * for(int j=0; j<=i; j++){ double gain = 0; ArrayList<Piece>
				 * chopping = new ArrayList<Piece>(); // merge piece (i-j) to
				 * piece i Piece previous_piece = pieces.get(i-j);
				 * //System.out.println("\t\t merge piece "+previous_piece.
				 * getStart()+" to "+current_piece.getEnd()); Piece new_piece =
				 * new Piece(); new_piece.setStart(previous_piece.getStart());
				 * new_piece.setEnd(current_piece.getEnd()); double new_gain =
				 * new_piece.computeGain(sequence, deltat);
				 * new_piece.setGain(new_gain);
				 *
				 * // get the best chopping from piece 0 to piece i-j-1, and its
				 * gain if(i-j-1>=0){ ArrayList<Piece> previous_chopping =
				 * choppings.get(i-j-1); chopping.addAll(previous_chopping);
				 * chopping.add(chopping.size(), new_piece);
				 *
				 * double previous_gain = gains.get(i-j-1); gain = new_gain +
				 * previous_gain;
				 *
				 *
				 * } else{ gain = new_gain; chopping.add(chopping.size(),
				 * new_piece); } //System.out.println(
				 * "\t\t\t current chopping when merging piece "+(i-j)+
				 * " to piece "+i); for(int s=0; s<chopping.size(); s++){ Piece
				 * p = chopping.get(s);
				 * //System.out.println("\t\t\t\t"+s+" series: ["+p.getStart()+
				 * ","+p.getEnd()+"]"); }
				 *
				 *
				 * // check if current chopping is the best if(gain>max_gain){
				 * max_chopping.clear(); max_chopping.addAll(chopping); max_gain
				 * = gain; }
				 * //System.out.println("\t\t current gain / max_gain : "+gain+
				 * " ; "+max_gain); } // update the best chopping from piece 0
				 * to piece i, and gain choppings.set(i, max_chopping);
				 * gains.set(i, max_gain); //System.out.println(
				 * "\t\t best chopping from piece 0 to piece "+i); for(int m=0;
				 * m< max_chopping.size(); m++){ Piece piece =
				 * max_chopping.get(m);
				 * //System.out.println("\t\t\t"+m+" series: ["+piece.getStart()
				 * +","+piece.getEnd()+"]"); }
				 *
				 * }
				 */
				// best chopping:
				int n = choppings.size();
				ArrayList<Piece> chopping = new ArrayList<Piece>();
				chopping = choppings.get(n - 1);
				for (int i = 0; i < chopping.size(); i++) {
					Piece piece = chopping.get(i);
					System.out.println("\t" + i + " series: [" + piece.getStart() + "," + piece.getEnd() + "]");
				}
				System.out.println("\t max gain:" + gains.get(n - 1));
			}
		}
	}

	public void DynamicSetProcess(int deltat) {

		int theta = 7;
		System.out.println("theta = " + theta);
		/*
		 * instead of treating each record separately, consider records of the
		 * same catalog # as a set
		 */
		for (int count = 0; count < finalcatalists.size(); count++) {

			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			// data 12
			if (releaselist.get(0).getId() == 23307884) {

				Block block = new Block(timelist, catalist);

				block.setDeltat(deltat);

				ArrayList<HashSet> set_sequence = new ArrayList<HashSet>();
				ArrayList<HashSet> oppo_set_sequence = new ArrayList<HashSet>();
				set_sequence = block.getSet_sequence();
				oppo_set_sequence = block.getOppo_set_sequence();
				ArrayList<String> cata_sequence = block.getCata_sequence();

				// System.out.println(set_sequence.size()+"\t"+oppo_set_sequence.size()+"\t"+cata_sequence.size());
				ArrayList<Piece> pieces = new ArrayList<Piece>();
				pieces = block.findSetPieces(0);

				System.out.println("# of pieces :" + pieces.size());

				/*
				 * intialize chopping X that starts&ends at each piece; G stores
				 * the gain of each chopping in X
				 **/
				ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
				ArrayList<Double> gains = new ArrayList<Double>();

				block.setChoppings(choppings);
				block.setGains(gains);

				for (int i = 0; i < pieces.size(); i++) {
					Piece piece = pieces.get(i);
					// System.out.println("piece:
					// ["+piece.getStart()+","+piece.getEnd()+"]
					// \t"+(piece.getEnd()-piece.getStart()+1));

					int size = piece.getPieceSize(set_sequence);// getSize(set_sequence);
					piece.setSize(size);

					double gain = piece.computeSetGain(set_sequence, oppo_set_sequence, deltat);
					piece.setGain(gain);

					// System.out.println("\t"+piece.size+"\t"+piece.getGain()+"\t"+piece.isIncrease());

					// add piece i to chopping i
					ArrayList<Piece> chopping = new ArrayList<Piece>();
					chopping.add(chopping.size(), piece);

					// add chopping i to choppings; add gain i to gains
					choppings.add(i, chopping);
					gains.add(i, gain);

				}

				/*
				 * find the best chopping from piece 0 to piece i
				 *
				 * for(int i=1; i< pieces.size(); i++){ // loop from the 2nd
				 * piece Piece current_piece = pieces.get(i);
				 * System.out.println(i+"process piece \t"+i+"\t"+current_piece.
				 * getStart()+" to "+current_piece.getEnd()+"\t"+pieces.size());
				 * ArrayList<Piece> max_chopping = new ArrayList<Piece>();
				 * double max_gain = 0;
				 *
				 * for(int j=0; j<=i; j++){ double gain = 0; ArrayList<Piece>
				 * chopping = new ArrayList<Piece>(); // merge piece (i-j) to
				 * piece i Piece previous_piece = pieces.get(i-j);
				 * System.out.println(j+"\t merge piece "+previous_piece.
				 * getStart()+" to "+current_piece.getEnd()+"\t"+pieces.size());
				 * Piece new_piece = new Piece();
				 * new_piece.setStart(previous_piece.getStart());
				 * new_piece.setEnd(current_piece.getEnd()); int size =
				 * new_piece.getSize(set_sequence); new_piece.setSize(size);
				 *
				 * double new_gain =
				 * new_piece.computeSetGain(set_sequence,oppo_set_sequence,
				 * deltat); int max_error = new_piece.getMax_set_errors();
				 * System.out.println("\t\t new piece \t"+new_gain+"\t"+
				 * new_piece.isIncrease()); //System.out.println(
				 * "\t\t max error size when merging piece "+j+" to "+i+": "
				 * +max_error); new_piece.setGain(new_gain);
				 *
				 * int org_size = pieces.size(); int new_position =
				 * block.checkConsistency(new_piece, i-j, i); int new_size =
				 * pieces.size();
				 * System.out.println("\t # pieces/choppings/gains \t"+pieces.
				 * size()+"\t"+choppings.size()+"\t"+gains.size());
				 *
				 * if(org_size<new_size){
				 * System.out.println("retart from piece \t"+new_position);
				 *
				 * i = new_position; j = -1; current_piece = pieces.get(i);
				 * System.out.println(i+"re-process piece \t"+i+"\t"+
				 * current_piece.getStart()+" to "+current_piece.getEnd()+"\t"+
				 * pieces.size()); max_chopping = new ArrayList<Piece>();
				 * max_gain = 0;
				 *
				 * }else{ // no pieces are split if(max_error <= theta){ // get
				 * the best chopping from piece 0 to piece i-j-1, and its gain
				 * if(i-j-1>=0){ ArrayList<Piece> previous_chopping =
				 * choppings.get(i-j-1); chopping.addAll(previous_chopping);
				 * chopping.add(chopping.size(), new_piece);
				 *
				 * double previous_gain = gains.get(i-j-1); gain = new_gain +
				 * previous_gain;
				 *
				 *
				 * } else{ gain = new_gain; chopping.add(chopping.size(),
				 * new_piece); } //System.out.println(
				 * "\t\t\t current chopping when merging piece "+(i-j)+
				 * " to piece "+i); for(int s=0; s<chopping.size(); s++){ Piece
				 * p = chopping.get(s);
				 * //System.out.println("\t\t\t\t"+s+" series: ["+p.getStart()+
				 * ","+p.getEnd()+"]"); }
				 *
				 *
				 * // check if current chopping is the best if(gain>max_gain){
				 * max_chopping.clear(); max_chopping.addAll(chopping); max_gain
				 * = gain; }
				 * System.out.println("\t\t\t current gain / max_gain : "+gain+
				 * " ; "+max_gain); }
				 *
				 * }
				 *
				 *
				 *
				 *
				 *
				 *
				 *
				 * } // update the best chopping from piece 0 to piece i, and
				 * gain choppings.set(i, max_chopping); gains.set(i, max_gain);
				 * System.out.println("\t best chopping from piece 0 to piece "
				 * +i); for(int m=0; m< max_chopping.size(); m++){ Piece piece =
				 * max_chopping.get(m);
				 * System.out.println("\t\t"+m+" series: ["+piece.getStart()+","
				 * +piece.getEnd()+"]"); }
				 *
				 * }
				 *
				 * int n = choppings.size(); ArrayList<Piece> chopping = new
				 * ArrayList<Piece>(); chopping = choppings.get(n-1); for(int
				 * i=0; i< chopping.size();i++){ Piece piece = chopping.get(i);
				 * System.out.println("\t"+i+"\t series: ["+piece.getStart()+","
				 * +piece.getEnd()+"] \t"+piece.getGain()+"\t"); }
				 * System.out.println("\t \t max gain \t"+gains.get(n-1));
				 * printSetLIBs(chopping, set_sequence);
				 *
				 * //printProcess(choppings, sequence);
				 */

			}
		}

	}

	private void printSetLIBs(ArrayList<Piece> chopping, ArrayList<HashSet> set_sequence) {
		// TODO Auto-generated method stub
		System.out.println("\t lib \t error");

		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			int[][] lib = piece.getSet_lib();
			int[][] errors = piece.set_errors;
			for (int j = piece.getStart(); j <= piece.getEnd(); j++) {

				HashSet years = set_sequence.get(j);
				Iterator it = years.iterator();
				while (it.hasNext()) {
					int year = (Integer) it.next();

					if (year > 0) {
						// check if year is w.i. the band
						boolean outlier = true;
						int n = 0;
						while (n < lib.length && outlier) {
							int id = lib[n][0];
							int year2 = lib[n][1];
							if (j == id && year == year2)
								outlier = false;
							n++;
						}
						if (outlier)
							System.out.println(j + "\t \t" + year);
						else
							System.out.println(j + "\t" + year);

					}
				}
			}
			System.out.println();
		}
	}

	public void errorMissingProcess(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		// System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		// System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, 3, theta);

						syntheticMiss.setPercent(percent);
						syntheticError.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> errorSequence = syntheticError.sampleWithoutReplacement();
						ArrayList<Integer> missSequence = syntheticMiss.sampleWithoutReplacement();

						// System.out.println("\t" + errorSequence.size() + "\t"
						// + missSequence.size());

						ArrayList<Integer> newSequence = new ArrayList<Integer>();

						for (int j = 0; j < errorSequence.size(); j++) {
							int missYear = missSequence.get(j);
							int errorYear = errorSequence.get(j);


							int newYear = 0;
							if (missYear == errorYear || missYear == 0)
								newYear = missYear;
							else
								newYear = errorYear;
							// System.out.println("\t\t" + j + "\t" + missYear +
							// "\t" + errorYear + "\t" + newYear);

							newSequence.add(newSequence.size(), newYear);
						}

						// System.out.println("new sequence \t" + newSequence);

						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticError.getTruths();
						// syntheticError.getTruths();

						// Find pieces
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						if (newSequence.size() > 100)
							pieces = block.findLCMBPieces(deltat);
						else
							pieces = block.withoutPieces();
						// System.out.println("!!# of pieces :" +
						// pieces.size());

						// Discover series
						ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, newSequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticError.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticError.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
						// Evaluate series
						getSeries(series, chopping, releaselist);
					}
				}
			}
			Evaluation evaluation = new Evaluation();
			// evaluation.evaluationPartition(series);

			avgPrecision = avgCorrect / avgRepair;
			// System.out.println(avgPrecision);

			System.out.println("\t" + (percent * 100) + " % \t" + avgPrecision);
			overallAccuracy += avgPrecision;
			size++;
		}
		overallAccuracy = overallAccuracy / size;
		System.out.println("\t" + overallAccuracy);
	}

	public void seriesDiscoveryWithMissingErroneousValues(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					String ids = "";

					// if (releaselist.get(0).getId() == 33166737) {
					// if(releaselist.size() > 200){

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, deltat, theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, deltat, theta);

						syntheticMiss.setPercent(percent);
						syntheticError.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> errorSequence = syntheticError.sampleWithoutReplacement();
						ArrayList<Integer> missSequence = syntheticMiss.sampleWithoutReplacement();

						// System.out.println("\t" + errorSequence.size() + "\t"
						// + missSequence.size());

						ArrayList<Integer> newSequence = new ArrayList<Integer>();

						for (int j = 0; j < errorSequence.size(); j++) {
							int missYear = missSequence.get(j);
							int errorYear = errorSequence.get(j);

							int newYear = 0;
							if (missYear == errorYear || missYear == 0)
								newYear = missYear;
							else
								newYear = errorYear;
							// System.out.println("\t\t" + j + "\t" + missYear +
							// "\t" + errorYear + "\t" + newYear);

							newSequence.add(newSequence.size(), newYear);
						}

						// System.out.println("new sequence \t" + newSequence);

						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();
						// syntheticError.getTruths();

						// Find pieces
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						if (newSequence.size() > 100)
							pieces = block.findLCMBPieces(deltat);
						else
							pieces = block.withoutPieces();
						// System.out.println("!!# of pieces :" +
						// pieces.size());

						// Discover series
						ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, newSequence);

						// Evaluate series
						getSeries(series, chopping, releaselist);

						/*
						 * // Evaluate precision HashMap<Integer, Triple>
						 * repairs = repairValues(chopping, newSequence, deltat,
						 * truths); HashMap<Integer, Triple> sampleRepairs =
						 * syntheticMiss.getRepairs(); repairSamples(repairs,
						 * sampleRepairs); ArrayList<Integer> precision =
						 * syntheticMiss.evaluatePairs(); avgCorrect +=
						 * precision.get(0); avgRepair += precision.get(1);
						 */
					}
				}
			}

			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);
			/*
			 * avgPrecision = avgCorrect / avgRepair;
			 * System.out.println(avgPrecision);
			 *
			 * // System.out.println("\t" + (percent * 100) + " % \t" + //
			 * avgPrecision); overallAccuracy += avgPrecision; size++;
			 */
		}
		/*
		 * overallAccuracy = overallAccuracy / size; // System.out.println("\t"
		 * + overallAccuracy);
		 */
	}

	public void missingErrorProcess(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 3;
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					String ids = "";

					// if (releaselist.get(0).getId() == 33166737) {
					// if(releaselist.size() > 200){

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, deltat, theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, deltat, theta);

						syntheticMiss.setPercent(percent);
						syntheticError.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> errorSequence = syntheticError.sampleWithoutReplacement();
						ArrayList<Integer> missSequence = syntheticMiss.sampleWithoutReplacement();

						// System.out.println("\t" + errorSequence.size() + "\t"
						// + missSequence.size());

						ArrayList<Integer> newSequence = new ArrayList<Integer>();

						for (int j = 0; j < errorSequence.size(); j++) {
							int missYear = missSequence.get(j);
							int errorYear = errorSequence.get(j);

							int newYear = 0;
							if (missYear == errorYear || missYear == 0)
								newYear = missYear;
							else
								newYear = errorYear;
							// System.out.println("\t\t" + j + "\t" + missYear +
							// "\t" + errorYear + "\t" + newYear);

							newSequence.add(newSequence.size(), newYear);
						}

						// System.out.println("new sequence \t" + newSequence);

						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();
						// syntheticError.getTruths();

						// Find pieces
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						if (newSequence.size() > 100)
							pieces = block.findLCMBPieces(deltat);
						else
							pieces = block.withoutPieces();
						// System.out.println("!!# of pieces :" +
						// pieces.size());

						// Discover series
						ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, newSequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
					}
				}
			}
			avgPrecision = avgCorrect / avgRepair;
			System.out.println(avgPrecision);

			// System.out.println("\t" + (percent * 100) + " % \t" +
			// avgPrecision);
			overallAccuracy += avgPrecision;
			size++;
		}
		overallAccuracy = overallAccuracy / size;
		// System.out.println("\t" + overallAccuracy);
	}

	/**
	 * Process with real-world data: we know the ground truth of missing and
	 * erroneous data.
	 *
	 * @param deltat
	 * @param theta
	 * @throws Exception
	 */
	public void BaselineDynamicProcessWithGroundTruth(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		// System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;
		ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
		Evaluation evaluation = new Evaluation();
		HashMap<Integer, Triple> totalRepairs = new HashMap<Integer, Triple>();

		// System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.0; percent <= 0.0; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					String ids = "";
					// printPartition(catalist, timelist, releaselist);
					// System.out.println();
					// printMatrix(catalist, timelist);

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					if (sequence.size() > 1) {
						ArrayList<Integer> newSequence = block.getSequence();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = new HashMap<Integer, Integer>();
						for (int n = 0; n < releaselist.size(); n++) {
							ReleaseLabel relabel = releaselist.get(n);
							// System.out.println("\t truth \t" + n + "\t" +
							// relabel.getGround_truth() + "\t"
							// + relabel.getDate());
							truths.put(n, relabel.getGround_truth());
						}

						boolean existTruth = validTruth(truths);

						if (existTruth) {
							/*
							 * // Set truths in blocks
							 * block.setGround_truths(truths);
							 *
							 * // Find pieces ArrayList<Piece> pieces = new
							 * ArrayList<Piece>(); if (newSequence.size() > 100)
							 * { pieces = block.findLCMBPieces(deltat); } else {
							 * pieces = block.withoutPieces(); }
							 *
							 * // Discover series ArrayList<Piece> chopping =
							 * seriesDiscovery(deltat, theta, newSequence,
							 * pieces); // printLIBs(chopping, newSequence);
							 */

							// Discover series
							ScaleProcessor scaleProcessor = new ScaleProcessor(deltat);
							scaleProcessor.processBlock(newSequence, releaselist);
							ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = getSubSeries(
									scaleProcessor.series, releaselist);

							// Add all records in a series into a piece
							ArrayList<Piece> pieces = new ArrayList<Piece>();
							pieces = block.setSeriesPiece(subSeries);

							// Discover series
							ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
							// seriesDiscovery(deltat, theta, newSequence,
							// pieces);
							// printLIBs(chopping, sequence);
							getSeries(series, chopping, releaselist);

							HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);

							// Set ground truth to repaired values
							setGroundTruths(repairs, truths);

							// Evaluate precision
							ArrayList<Integer> precision = new ArrayList<Integer>();
							precision = evaluation.evaluateRepairs(repairs);
							totalRepairs.putAll(repairs);

							avgCorrect += precision.get(0);
							avgRepair += precision.get(1);
						}
					}
				}
			}
			avgPrecision = avgCorrect / avgRepair;
			// System.out.println("average precision \t" + avgPrecision);
			overallAccuracy += avgPrecision;
			size++;
		}
		evaluation.analyseRepairs(totalRepairs, deltat);
		overallAccuracy = overallAccuracy / size;
		System.out.println("\t" + overallAccuracy);
		// evaluation.evaluationPartition(series);
	}

	/**
	 * Process with real-world data: we know the ground truth of missing and
	 * erroneous data.
	 *
	 * @param deltat
	 * @param theta
	 * @throws Exception
	 */
	public void DynamicProcessWithGroundTruth(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		// System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;
		Evaluation evaluation = new Evaluation();

		int totalSize = 0;
		// System.out.println("# blocks \t" + finalcatalists.size());
		ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

		for (double percent = 0.0; percent <= 0.0; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			HashMap<Integer, Triple> totalRepairs = new HashMap<Integer, Triple>();
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					String ids = "";

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					if (sequence.size() > 1) {
						ArrayList<Integer> newSequence = block.getSequence();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = new HashMap<Integer, Integer>();
						for (int n = 0; n < releaselist.size(); n++) {
							ReleaseLabel relabel = releaselist.get(n);
							// System.out.println("\t truth \t" + n + "\t" +
							// relabel.getGround_truth() + "\t"
							// + relabel.getDate());
							truths.put(n, relabel.getGround_truth());
						}

						boolean existTruth = validTruth(truths);

						if (existTruth) {
							// System.out.println("Process block with record
							// size \t" + timelist.size());
							// System.out.println("First release \t" +
							// releaselist.get(0).getId() + "\t"
							// + releaselist.get(0).getDate());
							/*
							 * printPartition(catalist, timelist, releaselist);
							 * System.out.println(); printMatrix(catalist,
							 * timelist);
							 */
							totalSize += timelist.size();

							// Set truths in blocks
							block.setGround_truths(truths);

							// Find pieces
							ArrayList<Piece> pieces = new ArrayList<Piece>();
							if (newSequence.size() > 100) {
								pieces = block.findLCMBPieces(deltat);
							} else {
								pieces = block.withoutPieces();
							}

							// Discover series
							ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
							// printLIBs(chopping, newSequence);
							getSeries(series, chopping, releaselist);

							HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);

							// Set ground truth to repaired values
							setGroundTruths(repairs, truths);

							// Evaluate precision
							ArrayList<Integer> precision = new ArrayList<Integer>();
							precision = evaluation.evaluateRepairs(repairs);

							avgCorrect += precision.get(0);
							avgRepair += precision.get(1);

							totalRepairs.putAll(repairs);
						}
					}
				}
			}
			evaluation.analyseRepairs(totalRepairs, deltat);

			avgPrecision = avgCorrect / avgRepair;
			System.out.println(avgPrecision);
			overallAccuracy += avgPrecision;
			size++;
		}
		overallAccuracy = overallAccuracy / size;
		// System.out.println("\t" + overallAccuracy);
		// System.out.println("total # processed records \t" + totalSize);
		// evaluation.evaluationPartition(series);

	}

	private void setGroundTruths(HashMap<Integer, Triple> repairs, HashMap<Integer, Integer> truths) {
		int repairSize = repairs.size();
		int truthsSize = truths.size();
		if (repairSize != truthsSize) {
			HashMap<Integer, Triple> repairs1 = repairs;
			HashMap<Integer, Integer> truths1 = truths;
			// System.out.println(repairSize + "\t = \t" + truthsSize);
		}

		for (int i = 0; i < repairs.size(); i++) {
			Triple triple = repairs.get(i);
			int truth = truths.get(i);
			// System.out.println(truth);
			if (triple == null) {
				// System.err.println("Don't have repair for index " + i);
			} else {
				if (truth != 0) {
					triple.setGround(truth);
				}
			}
		}
	}

	private boolean validTruth(HashMap<Integer, Integer> truths) {
		boolean existTruth = false;
		for (int i = 0; i < truths.size(); i++) {
			int truth = truths.get(i);
			if (truth > 0) {
				existTruth = true;
			}
		}
		return existTruth;
	}

	private void printGround_truth(HashMap<Integer, Integer> truths) {
		for (int i = 0; i < truths.size(); i++) {
			int truth = truths.get(i);
			System.out.println(truth);
		}
	}

	public void dynamicProcess(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		// System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		// System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;

			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1, 2);
						// paraProcessor.learnBandWidth();


						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						// SyntheticError syntheticError = new
						// SyntheticError(sequence.size(), timelist, 0, deltat,
						// theta);

						// System.out.println("\t \tavg \t min \t max");
						// for (double percent = 0.0; percent <= 0.6; percent +=
						// 0.1) {
						syntheticMiss.setPercent(percent);
						// syntheticError.setPercent(percent);

						// Pertube missing data
						// ArrayList<Integer> newSequence =
						// syntheticError.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						ArrayList<Integer> newSequence = syntheticMiss.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();

						// HashMap<Integer, Integer> truths =
						// syntheticError.getTruths();
						// syntheticError.getTruths();

						// Find pieces
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						if (newSequence.size() > 100)
							pieces = block.findLCMBPieces(deltat);
						else
							pieces = block.withoutPieces();
						// System.out.println("!!# of pieces :" +
						// pieces.size());

						// Discover series
						ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, newSequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
						// Evaluate series
						getSeries(series, chopping, releaselist);

						Evaluation evaluation = new Evaluation();
						evaluation.analyseRepairs(repairs, deltat);

					}
				}
			}
			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);

			avgPrecision = avgCorrect / avgRepair;
			// System.out.println(avgPrecision);

			// System.out.println("\t" + (percent * 100) + " % \t" +
			// avgPrecision);
			overallAccuracy += avgPrecision;
			size++;
		}
		overallAccuracy = overallAccuracy / size;
		System.out.println("\t" + overallAccuracy);
	}

	public void dynamicSeries(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					String ids = "";

					// if (releaselist.get(0).getId() == 33166737) {
					// if(releaselist.size() > 200){

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, deltat, theta);

						// System.out.println("\t \tavg \t min \t max");
						// for (double percent = 0.0; percent <= 0.6; percent +=
						// 0.1) {
						syntheticMiss.setPercent(percent);
						syntheticError.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> newSequence = syntheticError.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						// newSequence =
						// syntheticError.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticError.getTruths();
						// syntheticError.getTruths();

						// Find pieces
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						if (newSequence.size() > 100)
							pieces = block.findLCMBPieces(deltat);
						else
							pieces = block.withoutPieces();
						// System.out.println("!!# of pieces :" +
						// pieces.size());

						// Discover series
						ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, newSequence);

						// Evaluate precision
						// Evaluate series
						getSeries(series, chopping, releaselist);
					}

				}
			}
			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);
		}
		// overallAccuracy = overallAccuracy / size;
		// System.out.println("\t" + overallAccuracy);
	}

	public void gapRepair(int deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());

		for (double percent = 0.1; percent <= 0.6; percent += 0.1) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 5;
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					// LinkedHashMap<Integer, String> catalist =
					// finalcatalists.get(count);
					// LinkedHashMap<Integer, ReleaseLabel> releaselist =
					// finalreleaselists.get(count);

					String ids = "";

					// if (releaselist.get(0).getId() == 33166737) {
					// if(releaselist.size() > 200){

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						// SyntheticError syntheticError = new
						// SyntheticError(sequence.size(), timelist, 0, deltat,
						// theta);

						// System.out.println("\t \tavg \t min \t max");
						// for (double percent = 0.0; percent <= 0.6; percent +=
						// 0.1) {
						syntheticMiss.setPercent(percent);
						// syntheticError

						// Pertube missing data
						ArrayList<Integer> newSequence = syntheticMiss.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						// newSequence =
						// syntheticError.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();
						// syntheticError.getTruths();

						// Add all records into a piece
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						pieces = block.simplePiece();

						// Discover series
						ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
						// seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, sequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
					}
				}
			}
			avgPrecision = avgCorrect / avgRepair;
			System.out.println("\t" + (percent * 100) + " % \t" + avgPrecision);
		}
	}

	public void scaleRepairMissingError(int deltat, int theta) throws Exception {
		System.out.println("delta t = " + deltat + "\t theata = " + theta);
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());
		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;

			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {
					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					// System.out.println("process block \t" + count);

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, deltat, theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, deltat, theta);

						syntheticError.setPercent(percent);
						syntheticMiss.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> errorSequence = syntheticError.sampleWithoutReplacement();
						ArrayList<Integer> missSequence = syntheticMiss.sampleWithoutReplacement();

						// System.out.println("\t" + errorSequence.size() + "\t"
						// + missSequence.size());

						ArrayList<Integer> newSequence = new ArrayList<Integer>();

						for (int j = 0; j < errorSequence.size(); j++) {
							int missYear = missSequence.get(j);
							int errorYear = errorSequence.get(j);

							int newYear = 0;
							if (missYear == errorYear || missYear == 0)
								newYear = missYear;
							else
								newYear = errorYear;
							// System.out.println("\t\t" + j + "\t" + missYear +
							// "\t" + errorYear + "\t" + newYear);

							newSequence.add(newSequence.size(), newYear);
						}

						// System.out.println("new sequence \t" + newSequence);

						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();

						// Discover series
						ScaleProcessor scaleProcessor = new ScaleProcessor(deltat);
						scaleProcessor.processBlock(newSequence, releaselist);
						ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = getSubSeries(scaleProcessor.series,
								releaselist);
						// System.out.println("# of subseries: " +
						// subSeries.size());

						// Add all records in a series into a piece
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						pieces = block.setSeriesPiece(subSeries);

						// Discover series
						ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
						// seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, sequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
						double localPrecision = precision.get(0) / Double.valueOf(precision.get(1));
						// System.out.println("\t local precision: \t" +
						// localPrecision);
						min = Math.min(min, localPrecision);
						max = Math.max(max, localPrecision);

						// Evaluate series
						getSeries(series, chopping, releaselist);
					}
				}
			}
			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);

			avgPrecision = avgCorrect / avgRepair;
			System.out.println(avgPrecision);

			// System.out.println("\t" + (percent * 100) + " % \t" +
			// avgPrecision + "\t" + min + "\t" + max);
			size++;
		}
	}

	public void scaleRepairError(int deltat, int theta) throws Exception {
		// System.out.println("delta t = " + deltat + "\t theata = " + theta);
		int size = 0;

		// System.out.println("# blocks \t" + finalcatalists.size());
		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;

			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {
					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					// System.out.println("process block \t" + count);

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						// SyntheticMiss syntheticMiss = new
						// SyntheticMiss(sequence.size(), timelist, 0, deltat,
						// theta);
						SyntheticError syntheticError = new SyntheticError(sequence.size(), timelist, 0, deltat, theta);

						syntheticError.setPercent(percent);

						// Pertube missing data
						ArrayList<Integer> newSequence = syntheticError.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						// newSequence =
						// syntheticError.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticError.getTruths();
						// syntheticError.getTruths();

						// Discover series
						ScaleProcessor scaleProcessor = new ScaleProcessor(deltat);
						scaleProcessor.processBlock(newSequence, releaselist);
						ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = getSubSeries(scaleProcessor.series,
								releaselist);
						// System.out.println("# of subseries: " +
						// subSeries.size());

						// Add all records in a series into a piece
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						pieces = block.setSeriesPiece(subSeries);

						// Discover series
						ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
						// seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, sequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticError.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticError.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
						double localPrecision = precision.get(0) / Double.valueOf(precision.get(1));
						// System.out.println("\t local precision: \t" +
						// localPrecision);
						min = Math.min(min, localPrecision);
						max = Math.max(max, localPrecision);

						// Evaluate series
						getSeries(series, chopping, releaselist);
					}
				}
			}
			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);

			avgPrecision = avgCorrect / avgRepair;
			System.out.println(avgPrecision);

			// System.out.println("\t" + (percent * 100) + " % \t" +
			// avgPrecision + "\t" + min + "\t" + max);
			size++;
		}
	}

	public void scaleRepairMissing(int deltat, int theta) throws Exception {
		System.out.println("delta t = " + deltat + "\t theata = " + theta);
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());
		for (double percent = 0.05; percent <= 0.35; percent += 0.05) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 1;
			ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {
					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						// SyntheticError syntheticError = new
						// SyntheticError(sequence.size(), timelist, 0, deltat,
						// theta);

						syntheticMiss.setPercent(percent);
						// syntheticError

						// Pertube missing data
						ArrayList<Integer> newSequence = syntheticMiss.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						// newSequence =
						// syntheticError.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();
						// syntheticError.getTruths();

						// Discover series
						ScaleProcessor scaleProcessor = new ScaleProcessor(deltat);
						scaleProcessor.processBlock(newSequence, releaselist);
						ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = getSubSeries(scaleProcessor.series, releaselist);
						// System.out.println("# of subseries: " +
						// subSeries.size());

						// Add all records in a series into a piece
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						pieces = block.setSeriesPiece(subSeries);

						// Discover series
						ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
						// seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, sequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
						double localPrecision = precision.get(0) / Double.valueOf(precision.get(1));
						// System.out.println("\t local precision: \t" +
						// localPrecision);
						min = Math.min(min, localPrecision);
						max = Math.max(max, localPrecision);
						// Evaluate series
						getSeries(series, chopping, releaselist);
					}
				}
			}
			Evaluation evaluation = new Evaluation();
			evaluation.evaluationPartition(series);
			avgPrecision = avgCorrect / avgRepair;
			// System.out.println(avgPrecision);

			// System.out.println("\t" + (percent * 100) + " % \t" +
			// avgPrecision + "\t" + min + "\t" + max);
			size++;
		}
	}

	public void gapRepair(int deltat, int theta, ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series)
			throws Exception {
		System.out.println("delta t = " + deltat + "\t theata = " + theta);
		int size = 0;

		System.out.println("# blocks \t" + finalcatalists.size());
		for (double percent = 0.1; percent <= 0.6; percent += 0.1) {
			double avgPrecision = 0;
			double avgCorrect = 0;
			double avgRepair = 0;
			double min = 1;
			double max = 0;
			int repeat = 10;
			for (int i = 0; i < repeat; i++) {

				for (int count = 0; count < finalcatalists.size(); count++) {

					// System.out.println();

					LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
					LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
					LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

					// System.out.println("process block \t" + count);

					ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = getSubSeries(series, releaselist);

					String ids = "";

					/*
					 * // Print records and metrix in block
					 * System.out.println("Process block \t"+count+"\n");
					 *
					 * printPartition(catalist, timelist, releaselist);
					 * System.out.println(); printMatrix(catalist, timelist);
					 *
					 * System.out.println();
					 */

					// Print out record ids in each block
					/*
					 * for(int i = 0; i < releaselist.size(); i++){ ReleaseLabel
					 * release = releaselist.get(i); int id = release.getId();
					 * ids += String.valueOf(id) + ", "; }
					 *
					 * System.out.println("( "+ ids+" )");
					 */

					Block block = new Block(timelist);
					ArrayList<Integer> sequence = block.getSequence();

					// System.out.println("# of records in a block: \t" +
					// sequence.size());

					if (sequence.size() > 1) {
						// ParameterProcessor paraProcessor = new
						// ParameterProcessor(sequence.size(), timelist, 0, 1,
						// 2);
						// paraProcessor.learnBandWidth();

						SyntheticMiss syntheticMiss = new SyntheticMiss(sequence.size(), timelist, 0, 3, theta);
						// SyntheticError syntheticError = new
						// SyntheticError(sequence.size(), timelist, 0, deltat,
						// theta);

						// System.out.println("\t \tavg \t min \t max");
						// for (double percent = 0.0; percent <= 0.6; percent +=
						// 0.1) {
						syntheticMiss.setPercent(percent);
						// syntheticError

						// Pertube missing data
						ArrayList<Integer> newSequence = syntheticMiss.sampleWithoutReplacement();
						// System.out.println("new sequence \t" + newSequence);
						// newSequence =
						// syntheticError.sampleWithoutReplacement();
						block.setSequence(newSequence);
						HashMap<Integer, Integer> truths = syntheticMiss.getTruths();
						// syntheticError.getTruths();

						// Add all records into a piece
						ArrayList<Piece> pieces = new ArrayList<Piece>();
						// pieces = block.simplePiece();
						pieces = block.setSeriesPiece(subSeries);

						// Discover series
						ArrayList<Piece> chopping = processSingleSeries(deltat, theta, newSequence, pieces);
						// seriesDiscovery(deltat, theta, newSequence, pieces);
						// printLIBs(chopping, sequence);

						// Evaluate precision
						HashMap<Integer, Triple> repairs = repairValues(chopping, newSequence, deltat, truths);
						HashMap<Integer, Triple> sampleRepairs = syntheticMiss.getRepairs();
						repairSamples(repairs, sampleRepairs);
						ArrayList<Integer> precision = syntheticMiss.evaluatePairs();
						avgCorrect += precision.get(0);
						avgRepair += precision.get(1);
					}
				}
			}
			avgPrecision = avgCorrect / avgRepair;
			System.out.println("\t" + (percent * 100) + " % \t" + avgPrecision);
			size++;
		}
	}

	private ArrayList<LinkedHashMap<Integer, ReleaseLabel>> getSubSeries(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series,
																		 LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
		int index = 0;
		for (int i = 0; i < series.size(); i++) {
			LinkedHashMap<Integer, ReleaseLabel> map = series.get(i);
			ReleaseLabel label = map.get(0);
			if (releaselist.containsValue(label)) {
				// System.out.println("\t\t find series: " + map.size());
				LinkedHashMap<Integer, ReleaseLabel> temp = new LinkedHashMap<Integer, ReleaseLabel>();
				for (int j = 0; j < map.size(); j++) {
					ReleaseLabel label1 = map.get(j);
					// System.out.println("\t\t\t" + j + "\t" +
					// label1.getDate());
					temp.put(index, label1);
					index++;
				}
				for (Map.Entry<Integer, ReleaseLabel> e : temp.entrySet()) {
					int key = e.getKey();
					ReleaseLabel label1 = e.getValue();
					// System.out.println("\t\t\t" + key + "\t" +
					// label1.getDate());
				}
				subSeries.add(subSeries.size(), temp);
			}
		}
		return subSeries;
	}

	public void seriesWithMultipleAttributes(double deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		double overallAccuracy = 0;
		ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();

		for (int count = 0; count < finalcatalists.size(); count++) {
			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			Block block = new Block(timelist);

			ArrayList<Integer> sequence = block.getSequence();

			// Find pieces
			ArrayList<Piece> pieces = new ArrayList<Piece>();
			double start = System.currentTimeMillis();
			if (block.sequence.size() > 100)
				// pieces = block.findLCMBPieces(deltat);
				pieces = block.findLCMBPiecesWithMultipleAttributes(deltat);
			else
				pieces = block.withoutPieces();
			double endPieces = System.currentTimeMillis();

			// System.out.println("piece runtime\t" + (endPieces - start));
			block.setPieces(pieces);

			// System.out.println("# of pieces \t" + pieces.size());

			// Discover series

			// ArrayList<Piece> chopping = seriesDiscovery(deltat, theta,
			// sequence, pieces);
			ArrayList<Piece> chopping = seriesDiscoveryWithMultipleAttributes(deltat, theta, sequence, pieces);
			double endChopping = System.currentTimeMillis();
			// System.out.println("series runtime\t" + (endChopping -
			// endPieces));
			// Evaluate series
			getSeries(series, chopping, releaselist);

		}
		Evaluation evaluation = new Evaluation();

		// evaluation.evaluationPartition(series);
	}

	public void series(double deltat, int theta) throws Exception {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		// System.out.println("delta t = " + deltat + "\t theata = " + theta);

		double overallAccuracy = 0;
		ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series = new ArrayList<LinkedHashMap<Integer, ReleaseLabel>>();
		increasingSeriesCount = 0;
		decreasingSeriesCount = 0;
		increasingSeriesSize = 0;
		decreasingSeriesSize = 0;
		int maxSeriesSize = 0;

		System.out.println("finalcatalist size \t" + finalcatalists.size());
		double startLMB = System.currentTimeMillis();
		for (int count = 0; count < finalcatalists.size(); count++) {
			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			Block block = new Block(timelist);
			ArrayList<Integer> sequence = block.getSequence();
			System.out.println(count + "\t" + "sequence size" + "\t" + block.sequence.size());

			// Find pieces
			ArrayList<Piece> pieces;

//				pieces = block.withoutPieces();
			pieces = block.findLCMBPieces(deltat);
			block.setPieces(pieces);

			System.out.println("\t # of pieces \t" + pieces.size());

			// Discover series
			Piece[] choppingArray = new Piece[sequence.size()];
			ArrayList<Piece> chopping = seriesDiscovery(deltat, theta, sequence, pieces);
			// analyseSeries(chopping);
			// printLIBs(chopping, sequence);
			// Evaluate series
			getSeries(series, chopping, releaselist);

			for (int i = 0; i < chopping.size(); i++) {
				int currentSeriesLength = chopping.get(i).end - chopping.get(i).start;
				System.out.println("\t \t max series size: \t" + Math.max(currentSeriesLength, maxSeriesSize));
			}
		}
		Evaluation evaluation = new Evaluation();
		double endLMB = System.currentTimeMillis();
		double lmbTime = endLMB - startLMB;

		System.out.println("LMB Runtime: \t" + lmbTime);

		double max_series_size = 0;
		double min_series_size = 1000000;

		/*
		 * for (int index = 0; index < series.size(); index++) {
		 * LinkedHashMap<Integer, ReleaseLabel> currentSeries =
		 * series.get(index); int series_size = series.get(index).size();
		 * max_series_size = Math.max(max_series_size, series_size);
		 * min_series_size = Math.min(min_series_size, series_size); }
		 */
		// System.out.println("# series: \t" + series.size());
		// System.out.println("max series size: \t" + max_series_size);
		// System.out.println("min series size: \t" + min_series_size);
		// System.out.println("increasing series count: \t" +
		// increasingSeriesCount);
		// System.out.println("decreasing series count: \t" +
		// decreasingSeriesCount);
		// System.out.println("increasing series size: \t" +
		// increasingSeriesSize);
		// System.out.println("decreasing series size: \t" +
		// decreasingSeriesSize);

		// evaluation.evaluationPartition(series);
	}

	private void getSeries(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> series, ArrayList<Piece> chopping,
						   LinkedHashMap<Integer, ReleaseLabel> releaselist) {

		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			LinkedHashMap<Integer, ReleaseLabel> map = new LinkedHashMap<Integer, ReleaseLabel>();
			int start = piece.start;
			int end = piece.end;
			int count = 0;
			// System.out.println(start + " to " + end);
			for (int j = start; j <= end; j++) {
				ReleaseLabel release = releaselist.get(j);
				// System.out.println("\t" + release.date);
				map.put(count, release);
				count++;
			}
			series.add(series.size(), map);
		}
	}

	public ArrayList<Piece> processSingleSeries(int deltat, int theta, ArrayList<Integer> sequence,
												ArrayList<Piece> pieces) {
		/*
		 * intialize chopping X that starts&ends at each piece; G stores the
		 * gain of each chopping in X
		 **/
		ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
		ArrayList<Piece> chopping = new ArrayList<Piece>();
		ArrayList<Double> gains = new ArrayList<Double>();

		// System.out.println("piece size " + pieces.size());

		for (int i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);
			// System.out.println("piece:[" + piece.getStart() + "," +
			// piece.getEnd() + "]\t" + (piece.size));

			ArrayList<Integer> lmb = new ArrayList<Integer>();
			for (int j = piece.start; j <= piece.end; j++) {
				int year = sequence.get(j);
				// System.out.println(j + "\t" + year);
				if (year > 0)
					lmb.add(lmb.size(), j);
			}

			piece.setLMB(lmb);
			double gain = lmb.size();// piece.computeGain(sequence, deltat);
			piece.setGain(gain);

			// add piece i to chopping i
			chopping.add(chopping.size(), piece);

			// add chopping i to choppings; add gain i to gains
			choppings.add(i, chopping);
			gains.add(i, gain);
		}

		// best chopping:
		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			// System.out.println(
			// "\t" + i + "\t series: [" + piece.getStart() + "," +
			// piece.getEnd() + "] \t" + piece.getGain());
		}
		// System.out.println("\t max gain:"+gains.get(n-1));
		return chopping;
	}

	public ArrayList<Piece> seriesDiscoveryWithMultipleAttributes(double deltat, int theta, ArrayList<Integer> sequence,
																  ArrayList<Piece> pieces) throws Exception {
		/*
		 * intialize chopping X that starts&ends at each piece; G stores the
		 * gain of each chopping in X
		 **/
		ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
		ArrayList<Double> gains = new ArrayList<Double>();

		// System.out.println("piece size " + pieces.size());

		for (int i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);
			// System.out.println("piece:[" + piece.getStart() + "," +
			// piece.getEnd() + "]\t" + (piece.size));

			double gain = piece.computeGainWithMultipleAttributes(sequence, deltat);
			piece.setGain(gain);

			// add piece i to chopping i
			ArrayList<Piece> chopping = new ArrayList<Piece>();
			chopping.add(chopping.size(), piece);

			// add chopping i to choppings; add gain i to gains
			choppings.add(i, chopping);
			gains.add(i, gain);
		}

		/*
		 * find the best chopping from piece 0 to piece i
		 */
		for (int i = 1; i < pieces.size(); i++) {
			Piece current_piece = pieces.get(i);
			ArrayList<Piece> max_chopping = new ArrayList<Piece>();
			double max_gain = 0;

			for (int j = 0; j <= i; j++) {
				double gain = 0;
				ArrayList<Piece> chopping = new ArrayList<Piece>();
				// merge piece (i-j) to piece i
				Piece previous_piece = pieces.get(i - j);
				Piece new_piece = new Piece();
				new_piece.setStart(previous_piece.getStart());
				new_piece.setEnd(current_piece.getEnd());
				// System.out.println("new piece: [" + new_piece.getStart() + "
				// - " + new_piece.getEnd() + "]");
				double new_gain = new_piece.computeGainWithMultipleAttributes(sequence, deltat);
				// System.out.println("new piece: [" + new_piece.getStart() + "
				// - " + new_piece.getEnd() + "] with " + "new gain " + j + ": "
				// + new_gain);
				new_piece.setGain(new_gain);

				if (new_piece.getMaxErrors() < theta) {

					// get the best chopping from piece 0 to piece i-j-1, and
					// its gain
					if (i - j - 1 >= 0) {
						ArrayList<Piece> previous_chopping = choppings.get(i - j - 1);
						chopping.addAll(previous_chopping);
						chopping.add(chopping.size(), new_piece);

						double previous_gain = gains.get(i - j - 1);
						gain = new_gain + previous_gain;

					} else {
						gain = new_gain;
						chopping.add(chopping.size(), new_piece);
					}

					// check if current chopping is the best
					if (gain > max_gain) {
						max_chopping.clear();
						max_chopping.addAll(chopping);
						max_gain = gain;
					}
				}
			}
			// update the best chopping from piece 0 to piece i, and gain
			choppings.set(i, max_chopping);
			gains.set(i, max_gain);
		}

		// best chopping:

		int n = choppings.size();
		ArrayList<Piece> chopping = new ArrayList<Piece>();
		if (!choppings.isEmpty()) {
			chopping = choppings.get(n - 1);
			for (int i = 0; i < chopping.size(); i++) {
				Piece piece = chopping.get(i);
			}
		}

		return chopping;
	}

	public ArrayList<Piece> seriesDiscovery(double deltat, int theta, ArrayList<Integer> sequence,
											@NotNull ArrayList<Piece> pieces) {
		/*
		 * intialize chopping X that starts&ends at each piece; G stores the
		 * gain of each chopping in X
		 **/
		ArrayList<ArrayList> choppings = new ArrayList<ArrayList>();
		ArrayList<Double> gains = new ArrayList<Double>();

		for (int i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);

			double gain = piece.computeGain(sequence, deltat);
			piece.setGain(gain);

			// add piece i to chopping i
			ArrayList<Piece> chopping = new ArrayList<Piece>();
			chopping.add(chopping.size(), piece);

			// add chopping i to choppings; add gain i to gains
			choppings.add(i, chopping);
			gains.add(i, gain);

		}

		/*
		 * find the best chopping from piece 0 to piece i
		 */
		for (int i = 1; i < pieces.size(); i++) {
			Piece current_piece = pieces.get(i);
			// System.out.println( "process piece \t" + i + "\t" +
			// current_piece.getStart() + " to " + current_piece.getEnd());
			ArrayList<Piece> max_chopping = new ArrayList<Piece>();
			double max_gain = 0;

			for (int j = 0; j <= i; j++) {
				double gain = 0;
				ArrayList<Piece> chopping = new ArrayList<Piece>();
				// merge piece (i-j) to piece i
				Piece previous_piece = pieces.get(i - j);
				// System.out.println("\t\t merge piece " +
				// previous_piece.getStart() + " to " + current_piece.getEnd());
				Piece new_piece = new Piece();
				new_piece.setStart(previous_piece.getStart());
				new_piece.setEnd(current_piece.getEnd());
				double new_gain = new_piece.computeGain(sequence, deltat);
				new_piece.setGain(new_gain);
				// System.out.println("new gain " + j + ": " + new_gain);
				// System.out.println("\t\t max no of errors \t" +
				// new_piece.getMaxErrors());

				if (new_piece.getMaxErrors() < theta) {

					// get the best chopping from piece 0 to piece i-j-1, and
					// its gain
					if (i - j - 1 >= 0) {
						ArrayList<Piece> previous_chopping = choppings.get(i - j - 1);
						chopping.addAll(previous_chopping);
						chopping.add(chopping.size(), new_piece);

						double previous_gain = gains.get(i - j - 1);
						gain = new_gain + previous_gain;

					} else {
						gain = new_gain;
						chopping.add(chopping.size(), new_piece);
					}

					// check if current chopping is the best
					if (gain > max_gain) {
						max_chopping.clear();
						max_chopping.addAll(chopping);
						max_gain = gain;
					}
					// System.out.println("\t\t current gain / max_gain :
					// "+gain+" ; "+max_gain);
				}
			}
			// update the best chopping from piece 0 to piece i, and gain
			choppings.set(i, max_chopping);
			gains.set(i, max_gain);
		}

		// best chopping:

		int n = choppings.size();
		ArrayList<Piece> chopping = new ArrayList<Piece>();
		if (!choppings.isEmpty()) {
			chopping = choppings.get(n - 1);
		}

		return chopping;
	}

	private void repairSamples(HashMap<Integer, Triple> repairs, HashMap<Integer, Triple> sampleRepairs) {
		// System.out.println("");
		for (Map.Entry<Integer, Triple> e : sampleRepairs.entrySet()) {
			int index = e.getKey();
			Triple missTriple = e.getValue();

			if (repairs.containsKey(index)) {
				Triple triple = repairs.get(index);
				missTriple.setStart(triple.getStart());
				missTriple.setEnd(triple.getEnd());
			} else {
				missTriple.setStart(missTriple.getError());
				missTriple.setEnd(missTriple.getError());
				missTriple.setAvg(missTriple.getError());
			}

			// System.out.println(
			// index + "\t" + missTriple.getGround() + "\t" +
			// missTriple.getStart() + "\t" + missTriple.getEnd());

		}
	}

	/**
	 * Fix missing values in each series
	 *
	 * @param chopping
	 * @param sequence
	 * @param deltat
	 * @param groundTruths
	 * @return
	 */
	private HashMap<Integer, Triple> repairValues(
			ArrayList<Piece> chopping,
			ArrayList<Integer> sequence,
			int deltat,
			HashMap<Integer, Integer> groundTruths) {

		// System.out.println("repair values");
		HashMap<Integer, Triple> repairs = new HashMap<Integer, Triple>();

		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			Piece prePiece = new Piece();
			Piece nextPiece = new Piece();
			if (i > 0)
				prePiece = chopping.get(i - 1);
			if (i < chopping.size() - 1)
				nextPiece = chopping.get(i + 1);

			repairPiece(sequence, deltat, repairs, prePiece, piece, nextPiece);

			for (int j = piece.getStart(); j <= piece.getEnd(); j++) {
				int time = sequence.get(j);

				if (piece.lmb.contains(j))
					// System.out.println(j + "\t" + time + "\t");
					System.out.print("");//
				else {// if(piece.errors.contains(j)){
					String timeStr = "";
					if (time != 0)
						timeStr = String.valueOf(time);

					Triple triple = repairs.get(j);
					String start = "";
					if (triple.getStart() > 0)
						start = String.valueOf(triple.getStart());
					String end = "";
					if (triple.getEnd() < 3000)
						end = String.valueOf(triple.getEnd());

					String truth = "";
					if (groundTruths.containsKey(j))
						truth = String.valueOf(groundTruths.get(j));
					String avg = "";
					if (triple.getAvg() > 0)
						avg = String.valueOf(triple.getAvg());

					// System.out.println(j + "\t \t" + timeStr + "\t" + start +
					// "\t" + end + "\t" + truth + "\t" + avg);
				}

			}
			// System.out.println();
		}
		return repairs;
	}

	/**
	 * @param sequence
	 * @param deltat
	 * @param repairs
	 * @param prePiece
	 * @param piece
	 * @param nextPiece
	 * @return
	 */
	protected HashMap<Integer, Triple> repairPiece(ArrayList<Integer> sequence, int deltat,
												   HashMap<Integer, Triple> repairs, Piece prePiece, Piece piece, Piece nextPiece) {

		for (int j = piece.start; j <= piece.end; j++) {
			// if (!piece.lmb.contains(j)) {
			Triple triple = new Triple();
			triple.setStart(0);
			triple.setEnd(3000);
			repairTriple(triple, sequence, j, deltat, prePiece, piece, nextPiece);
			// System.out.println(
			// "repair for \t" + j + "\t" + sequence.get(j) + "\t" +
			// triple.getStart() + "\t" + triple.getEnd());
			repairs.put(j, triple);
			// }
		}

		return repairs;
	}

	@SuppressWarnings("rawtypes")
	private void repairTriple(Triple triple, ArrayList<Integer> sequence, int j, int deltat, Piece prePiece,
							  Piece piece, Piece nextPiece) {
		int max = 0;
		int min = 3000;

		rangeRepair(triple, sequence, j, piece);

		if ((piece.isIncrease() && triple.getEnd() == 3000) || (!piece.isIncrease() && triple.getStart() == 0))
			rangeRepair(triple, sequence, j, nextPiece);
		if ((piece.isIncrease() && triple.getStart() == 0) || (!piece.isIncrease() && triple.getEnd() == 3000))
			rangeRepair(triple, sequence, j, prePiece);
		// System.out.println("\t\t"+j+" \t ["+(triple.getStart() - deltat)+",
		// "+(triple.getEnd() + deltat)+"]");

		triple.setStart(triple.getStart() - deltat);
		triple.setEnd(triple.getEnd() + deltat);
		setAverage(triple, triple.getStart(), triple.getEnd());
	}

	@SuppressWarnings("rawtypes")
	private void rangeRepair(Triple triple, ArrayList<Integer> sequence, int j, Piece piece) {
		int max = 0;
		int min = 3000;

		if (piece.lmb != null) {
			if (piece.isIncrease()) {
				for (int i = 0; i < piece.lmb.size(); i++) {
					int index = piece.lmb.get(i);
					if (index < j)
						max = Math.max(max, sequence.get(index));
					if (index > j)
						min = Math.min(min, sequence.get(index));
				}
			} else {
				for (int i = 0; i < piece.lmb.size(); i++) {
					int index = piece.lmb.get(i);
					if (index < j)
						min = Math.min(min, sequence.get(index));
					if (index > j)
						max = Math.max(max, sequence.get(index));
				}
			}
		}

		triple.setStart(Math.max(triple.getStart(), max));
		triple.setEnd(Math.min(triple.getEnd(), min));
	}

	@SuppressWarnings("rawtypes")
	private void setAverage(Triple triple, int max, int min) {
		int avg = min;
		if (max < 2000)
			avg = (min + max) / 2;
		else if (min <= 1000)
			avg = max;
		triple.setAvg(avg);
	}

	private void printProcess(ArrayList<ArrayList> choppings, ArrayList<Integer> sequence) {
		// TODO Auto-generated method stub
		for (int i = 0; i < choppings.size(); i++) {
			ArrayList<Piece> chopping = new ArrayList<Piece>();
			chopping = choppings.get(i);
			// System.out.println("\t best chopping ending at piece "+i+": #
			// series is "+chopping.size());
			printLIBs(chopping, sequence);
		}

	}

	private void printLIBs(ArrayList<Piece> chopping, ArrayList<Integer> sequence) {
		// TODO Auto-generated method stub
		System.out.println("\t lib \t error");

		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			if (piece.increase) {
				System.out.println("increasing");
			} else {
				System.out.println("decreasing");
			}
			ArrayList lmb = piece.getLMB();
			ArrayList errors = piece.getErrors();
			for (int j = piece.getStart(); j <= piece.getEnd(); j++) {
				int time = sequence.get(j);

				if (time > 0) {

					String end = "";
					if (j == piece.getEnd())
						end = String.valueOf(sequence.get(j));

					if (lmb.contains(j))
						System.out.println(j + "\t" + time + "\t");
					else if (errors.contains(j))
						System.out.println(j + "\t \t" + time);
				}
			}
			System.out.println();
		}
	}

	private void analyseSeries(ArrayList<Piece> chopping) {
		for (int i = 0; i < chopping.size(); i++) {
			Piece piece = chopping.get(i);
			if (piece.increase) {
				// System.out.println("increasing");
				increasingSeriesCount++;
				increasingSeriesSize += piece.getEnd() - piece.getStart() + 1;
			} else {
				decreasingSeriesCount++;
				decreasingSeriesSize += piece.getEnd() - piece.getStart() + 1;
				// System.out.println("decreasing");
			}
		}
	}

	public void greedyProcess(int deltat) {
		fp = 0;
		fn = 0;
		tp = 0;
		tn = 0;

		for (int count = 0; count < finalcatalists.size(); count++) {

			System.out.println("Process partition \t" + count + "\n");
			// System.out.println();

			LinkedHashMap<Integer, Integer> timelist = finaltimelists.get(count);
			LinkedHashMap<Integer, String> catalist = finalcatalists.get(count);
			LinkedHashMap<Integer, ReleaseLabel> releaselist = finalreleaselists.get(count);

			printPartition(catalist, timelist, releaselist);
			printMatrix(catalist, timelist);

			// int deltat = 2;
			/*
			 * find the list of possible cutoff point, i.e., if r_i+1.T + delta
			 * t < r_i.T, r_i.T is a cutting point
			 */
			ArrayList<Integer> cutPoints = new ArrayList<Integer>();
			cutPoints = getCutPoints(timelist, deltat);

			/*
			 * if no cutPoint, detect errors
			 */
			// if(cutPoints.size()==0)
			// errorDetection(timelist, catalist, releaselist, deltat);

			/*
			 * preceed cutPoints list
			 */
			for (int i = 0; i < cutPoints.size(); i++) {
				System.out.println("CutPoints: \t" + cutPoints);

				int currentIndex = cutPoints.get(i);
				int nextIndex = timelist.size() - 1;
				if ((i + 1) < cutPoints.size())
					nextIndex = cutPoints.get(i + 1);
				int previousIndex = -1;
				if (i - 1 >= 0)
					previousIndex = cutPoints.get(i - 1);
				System.out.println();
				System.out.println("\t" + previousIndex + "\t" + currentIndex + "\t" + nextIndex);

				// 1. do not split
				System.out.println("\t merge two parts");
				LinkedHashMap<Integer, Integer> timelist0 = new LinkedHashMap<Integer, Integer>();
				LinkedHashMap<Integer, String> catalist0 = new LinkedHashMap<Integer, String>();
				LinkedHashMap<Integer, ReleaseLabel> releaselist0 = new LinkedHashMap<Integer, ReleaseLabel>();
				getSubTimelist(timelist, previousIndex, nextIndex, timelist0, catalist0, catalist, releaselist0,
						releaselist);
				// System.out.println("\t"+timelist0.size()+"\t"+releaselist0.size()+"\t"+catalist0.size());

				ArrayList<Integer> lib0 = errorDetection(timelist0, catalist0, releaselist0, deltat);
				System.out.println("|LIB|: \t" + lib0.size());

				ArrayList<Integer> error0 = getLibError(lib0, timelist0);
				System.out.println("Error index \t" + error0);

				int leb0 = getMaxLibError(lib0, timelist0);

				double gain0 = lib0.size() - error0.size();// Double.valueOf(error0.size())/Double.valueOf(lib0.size());
				// gain0 = lib0.size() - leb0;
				// gain0 = lib0.size() - Math.pow(leb0, 2);
				System.out.println("Gain: \t" + gain0);

				// 2. do not merge
				System.out.println("\t split two parts");
				LinkedHashMap<Integer, Integer> timelist1 = new LinkedHashMap<Integer, Integer>();
				LinkedHashMap<Integer, String> catalist1 = new LinkedHashMap<Integer, String>();
				LinkedHashMap<Integer, ReleaseLabel> releaselist1 = new LinkedHashMap<Integer, ReleaseLabel>();
				System.out.println("\t get the first part:");
				getSubTimelist(timelist, previousIndex, currentIndex, timelist1, catalist1, catalist, releaselist1,
						releaselist);

				ArrayList<Integer> lib1 = errorDetection(timelist1, catalist1, releaselist1, deltat);
				ArrayList<Integer> error1 = getLibError(lib1, timelist1);
				System.out.println("|LIB| \t" + lib1.size());
				System.out.println("Error index \t" + error1);

				int leb1 = getMaxLibError(lib1, timelist1);

				double gain1 = lib1.size() - error1.size();
				// gain1 = lib1.size() - leb1;
				// gain1 = lib1.size() - Math.pow(leb1, 2);
				// double rate1 =
				// Double.valueOf(error1.size())/Double.valueOf(lib1.size());
				System.out.println("Gain1: \t" + gain1);

				// get the second part
				LinkedHashMap<Integer, Integer> timelist2 = new LinkedHashMap<Integer, Integer>();
				LinkedHashMap<Integer, String> catalist2 = new LinkedHashMap<Integer, String>();
				LinkedHashMap<Integer, ReleaseLabel> releaselist2 = new LinkedHashMap<Integer, ReleaseLabel>();
				System.out.println("\t get the second part:");
				getSubTimelist(timelist, currentIndex, nextIndex, timelist2, catalist2, catalist, releaselist2,
						releaselist);

				ArrayList<Integer> lib2 = errorDetection(timelist2, catalist2, releaselist2, deltat);
				ArrayList<Integer> error2 = getLibError(lib2, timelist2);
				System.out.println("|LIB| \t" + lib2.size());
				System.out.println("Error index \t" + error2);
				int leb2 = getMaxLibError(lib2, timelist2);

				double gain2 = lib2.size() - error2.size();
				// gain2 = lib2.size() - leb2;
				// gain2 = lib2.size() - Math.pow(leb2, 2);
				System.out.println("Gain2: \t" + gain2);

				double gain12 = Math.max(gain1, gain2);// Math.max(lib2.size() ,
				// lib1.size()) -
				// Math.max(error2.size(),error1.size());//Double.valueOf(error2.size()+error1.size())/Double.valueOf();
				System.out.println("Gain: \t" + gain12);

				if (gain0 >= gain12) { // merge
					cutPoints.remove(i);
					System.out.println("merge result: \t" + cutPoints);
					i--;
				}

			}

			/*
			 * show final result
			 *
			 * System.out.println(); System.out.println("Final Result \t");
			 * if(cutPoints.size()==0) errorDetection(timelist, catalist,
			 * releaselist, deltat); else cutPoints.add(cutPoints.size(),
			 * timelist.size()-1);
			 *
			 * for(int j = 0; j< cutPoints.size(); j++){ int currentIndex =
			 * cutPoints.get(j); int previousIndex = 0; if(j-1>=0) previousIndex
			 * = cutPoints.get(j-1);
			 *
			 * System.out.println();
			 * System.out.println("\t"+previousIndex+"\t"+currentIndex);
			 *
			 * LinkedHashMap<Integer, Integer> timelist0 = new
			 * LinkedHashMap<Integer, Integer>(); LinkedHashMap<Integer, String>
			 * catalist0 = new LinkedHashMap<Integer, String>();
			 * LinkedHashMap<Integer, ReleaseLabel> releaselist0 = new
			 * LinkedHashMap<Integer, ReleaseLabel>(); getSubTimelist(timelist,
			 * previousIndex, currentIndex, timelist0, catalist0, catalist,
			 * releaselist0, releaselist);
			 * //System.out.println("\t"+timelist0.size()+"\t"+releaselist0.size
			 * ()+"\t"+catalist0.size());
			 *
			 * ArrayList<Integer> lib0 = errorDetection(timelist0, catalist0,
			 * releaselist0, deltat);
			 *
			 * }
			 */

		}

	}

	private ArrayList<Integer> getLibError(ArrayList<Integer> lib, LinkedHashMap<Integer, Integer> timelist) {
		ArrayList<Integer> error = new ArrayList<Integer>();

		// System.out.println("\t"+lib);

		for (int i = 0; i < timelist.size(); i++) {
			int year = timelist.get(i);
			if (!lib.contains(i) && year != 0)
				error.add(error.size(), i);
		}
		return error;
	}

	private int getMaxLibError(ArrayList<Integer> lib, LinkedHashMap<Integer, Integer> timelist) {
		ArrayList<Integer> error = new ArrayList<Integer>();
		int max = 0;

		for (int i = 0; i < timelist.size(); i++) {
			int year = timelist.get(i);
			if (!lib.contains(i) && year != 0)
				error.add(error.size(), i);
		}

		int count = 1;

		for (int j = 0; j < error.size() - 1; j++) {
			int index = error.get(j);
			int next_index = error.get(j + 1);
			if ((index + 1) == next_index)
				count++;
			else
				count = 1;
			max = Math.max(max, count);
		}

		max = Math.max(max, count);

		if (error.size() == 0)
			max = 0;

		// System.out.println("Error: \t"+error+"\t LEB: \t"+max);

		return max;
	}

	private void getSubTimelist(LinkedHashMap<Integer, Integer> timelist, int previousIndex, int nextIndex,
								LinkedHashMap<Integer, Integer> timelist0, LinkedHashMap<Integer, String> catalist0,
								LinkedHashMap<Integer, String> catalist, LinkedHashMap<Integer, ReleaseLabel> releaselist0,
								LinkedHashMap<Integer, ReleaseLabel> releaselist) {
		// TODO Auto-generated method stub
		if (previousIndex == -1) {
			timelist0.put(0, timelist.get(0));
			catalist0.put(0, catalist.get(0));
			releaselist0.put(0, releaselist.get(0));
			previousIndex++;
			// System.out.println("\t\t"+0+"\t"+timelist.get(0)+"\t"+catalist.get(0));
		}
		for (int i = previousIndex + 1; i <= nextIndex; i++) {
			// System.out.println("\t"+i+"\t"+timelist.size());
			int year = timelist.get(i);
			String cata = catalist.get(i);
			ReleaseLabel relabel = releaselist.get(i);
			timelist0.put(timelist0.size(), year);
			catalist0.put(catalist0.size(), cata);
			releaselist0.put(releaselist0.size(), relabel);
			// System.out.println("\t\t"+i+"\t"+year+"\t"+cata);
		}

	}

	private ArrayList<Integer> getCutPoints(LinkedHashMap<Integer, Integer> timelist, int deltat) {
		// TODO Auto-generated method stub
		ArrayList<Integer> cutPoints = new ArrayList<Integer>();

		int previousYear = 0;
		int previousIndex = -1;

		for (int i = 0; i < timelist.size(); i++) {
			int year = timelist.get(i);
			if (year != 0) {
				if (previousYear != 0) {
					int distance = year + deltat - previousYear;

					if (distance < 0)
						cutPoints.add(cutPoints.size(), previousIndex);// System.out.println("\t"+previousIndex);
				}
				previousYear = year;
				previousIndex = i;
				// System.out.println(i+"\t"+year);
			}
		}

		return cutPoints;
	}

}
