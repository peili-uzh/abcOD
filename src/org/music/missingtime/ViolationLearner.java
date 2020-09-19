package org.music.missingtime;

import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViolationLearner extends AbstractProcessor {

	public ViolationLearner(String sql, String entity) throws Exception {
		super(sql, entity);
		// TODO Auto-generated constructor stub
	}

	public HashMap<Integer, Integer> distancelist;


	public HashMap<Integer, Integer> getDistancelist() {
		return distancelist;
	}

	public void setDistancelist(HashMap<Integer, Integer> distancelist) {
		this.distancelist = distancelist;
	}

	public void scanSeries(String entityType, ArrayList dataset) {

		int current_series = 0;
		this.distancelist = new HashMap<Integer, Integer>();

		ArrayList<String> catalist = new ArrayList<String>(); // id-catalog pairs in catalog order
		ArrayList timelist = new ArrayList<Integer>(); // id-time pairs in catalog order
		ArrayList<ReleaseLabel> releaselist = new ArrayList<ReleaseLabel>(); // id - record pairs in catalog order


		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				int year = relabel.getNew_date();
				//int clusterid = relabel.getCluster_id();
				String catano = relabel.getNew_catno();
				int seriesid = relabel.getPartition_id();

				Catalog cata = new Catalog(catano);
				String prefix = cata.getPrefix();
				LinkedHashMap<Integer, String> strs = cata.getStrs();
				LinkedHashMap<Integer, Integer> ints = cata.getInts();


				if (seriesid != current_series) {

					collectDeltaT(timelist);

					System.out.println("label prefix: \t" + prefix);
					System.out.println("\t id \t label \t release \t artist \t credited artist \t genres \t styles \t country \t catalog \t year \t label id \t format \t series id");

					current_series = seriesid;
					catalist = new ArrayList<String>();
					timelist = new ArrayList<Integer>();
					releaselist = new ArrayList<ReleaseLabel>();
					System.out.println();
				}

				/*
				 * sort in catalog numeric order
				 */
				int position = -1;

				position = insertCatalogNumber(catalist, catano);

				if (position == -1) {
					position = timelist.size();
				}


				System.out.println("\t" + id + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getCatno() + "\t" + relabel.getNew_date() + "\t" + relabel.getPartition_id() + "\t" + relabel.getFormat() + "\t" + relabel.getCluster_id());
				timelist.add(position, year);
				catalist.add(position, catano);
				releaselist.add(position, relabel);
				//System.out.println("\t"+position+"\t"+timelist);

			}
		}

		if (!timelist.isEmpty())
			collectDeltaT(timelist);

	}

	public static int insertCatalogNumber(ArrayList<String> catalist,
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
	}

	private void collectDeltaT(ArrayList<Integer> timelist) {
		// TODO Auto-generated method stub
		if (timelist.size() > 1) {
			//System.out.println();
			int current_time = 0;

			for (int i = 0; i < timelist.size(); i++) {
				int time = timelist.get(i);
				//System.out.println("\t\t"+i+"\t"+time);
				if (current_time != 0 && time != 0) {
					int distance = time - current_time;
					if (distance < 0) {
						//System.out.println("\t\t\t"+(-distance));
						int count = 1;
						if (this.distancelist.containsKey(-distance))
							count = distancelist.get(-distance) + 1;
						distancelist.put((-distance), count);
					}
				}
				current_time = time;
			}

		}
	}

	public void getErrorProb() {
		int max_distance = 0;

		System.out.println("\t distance \t count");

		for (Map.Entry<Integer, Integer> e : this.distancelist.entrySet()) {
			int distance = e.getKey();
			int count = e.getValue();
			System.out.println("\t" + distance + "\t" + count);
			if (max_distance < distance)
				max_distance = distance;

		}

		System.out.println("error prob.");

		for (int i = 0; i <= max_distance; i++) {
			double prob = getProb(i);

		}
	}

	private double getProb(int i) {
		// TODO Auto-generated method stub
		double prob = 0;
		double sum_count = 0;
		double err_count = 0;

		for (Map.Entry<Integer, Integer> e : this.distancelist.entrySet()) {
			int distance = e.getKey();
			int count = e.getValue();
			//System.out.println("\t"+distance+"\t"+count);
			sum_count += count;
			if (i < distance)
				err_count += count;

		}

		prob = err_count / sum_count;
		System.out.println("\t" + i + "\t" + prob + "\t" + err_count + "\t" + sum_count);

		return prob;
	}

}
