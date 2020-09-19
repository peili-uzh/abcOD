package org.music.decay;

import org.music.data.ArtistData;
import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CopyOfDecayProcessor extends Utility {

	public ArrayList artists;

	public String atr;

	public int seq;

	private double th = 0.8;

	public ArrayList valuelist; //id - value pair
	public ArrayList durationlist; // id - duration pair
	public ArrayList entitylist; // id - entity pair
	public HashMap<Integer, ArrayList> invertedlist; // entity _id - value_ids
	public ArrayList entities;
	public HashMap<Integer, Integer> decaylist;
	public AttributeSimilarity attrsim;

	public ArrayList getArtists() {
		return artists;
	}

	public void setArtists(ArrayList artists) {
		this.artists = artists;
	}

	public CopyOfDecayProcessor(String sql) throws Exception {

		String clusters = "";
		clusters = getClusters(sql);
		//System.out.println(clusters.size());
		this.artists = queryRecords(clusters);

		attrsim = new AttributeSimilarity();
	}

	public void process(String atr, Decay decay) {

		/*
		 * initialize
		 */
		seq = 0;
		valuelist = new ArrayList();
		durationlist = new ArrayList();
		entitylist = new ArrayList();
		invertedlist = new HashMap<Integer, ArrayList>();
		decaylist = new HashMap<Integer, Integer>();
		entities = new ArrayList();
		this.atr = atr;

		int current_entity = 0;

		HashMap<String, ArrayList> values = new HashMap<String, ArrayList>();

		for (int i = 0; i < artists.size(); i++) {
			ArtistData artist = (ArtistData) artists.get(i);
			int cluster_id = artist.cluster_id;
			String year = artist.getDate();
			String value = "";
			if (this.atr.equals("name")) {
				value = artist.getName();
			}
			if (this.atr.equals("release")) {
				value = artist.getRelease();
			}
			if (this.atr.equals("country")) {
				value = artist.getCountry();
			}
			if (this.atr.equals("genres")) {
				ArrayList list = artist.getGenres();
				for (int n = 0; n < list.size(); n++) {
					value += list.get(n) + " ";
				}
			}
			if (this.atr.equals("styles")) {
				ArrayList list = artist.getStyle();
				for (int n = 0; n < list.size(); n++) {
					value += list.get(n) + " ";
				}
			}


			//System.out.println(cluster_id+"\t"+value+"\t"+year);

			if (!value.equals("")) {


				if (current_entity != cluster_id) {

					if (!values.isEmpty()) {
						addValues(values, current_entity);
					}

					current_entity = cluster_id;
					entities.add(entities.size(), current_entity);
					values = new HashMap<String, ArrayList>();

					createNewValue(value, year, values);

				} else {

					boolean matched = false;
					for (Map.Entry<String, ArrayList> element : values.entrySet()) {
						String previous_value = element.getKey();
						ArrayList previous_duration = element.getValue();
						double score = attrsim.StrSim(previous_value, value);
						if (score >= th) {
							matched = true;
							previous_duration.add(1, year);
							values.put(previous_value, previous_duration);
							//System.out.println("\t update "+previous_value +"\t from \t"+previous_duration.get(0)+"\t to \t"+previous_duration.get(1));
						}
					}
					if (!matched) {
						createNewValue(value, year, values);
					}

				}

			}
		}

		if (!values.isEmpty()) {
			addValues(values, current_entity);
			//entities.add(entities.size(), current_entity);

		}

		// compare each entity pair
		decay.CompareBetweenEntity(entities, invertedlist, valuelist, durationlist, decaylist);

		System.out.println("entity size \t" + entities.size());
		for (int j = 0; j < entities.size(); j++) {
			int cluster1 = (Integer) entities.get(j);
			ArrayList list1 = invertedlist.get(cluster1);
			//System.out.println(j+"\t"+cluster1+"\t"+list1.size());
			for (int m = j + 1; m < entities.size(); m++) {
				int cluster2 = (Integer) entities.get(m);
				ArrayList list2 = invertedlist.get(cluster2);
				//System.out.println("\t compare with \t"+m+"\t"+cluster2+"\t"+list2.size());

				compareValuesForAgreement(list1, list2);

			}
		}

		// compute decay
		for (int c = 0; c < 20; c++) {
			double agr = computeAgr(c);
			System.out.println(c + "\t" + agr);
		}
	}


	private double computeAgr(int c) {
		// TODO Auto-generated method stub
		double agr = 0;
		int count = 0;
		int totalcount = 0;
		for (Map.Entry<Integer, Integer> e : decaylist.entrySet()) {
			int t = e.getKey();
			int s = e.getValue();
			totalcount += s;
			if (t <= c) {
				count += s;
			}
		}

		agr = Double.valueOf(count) / Double.valueOf(totalcount);

		return agr;
	}

	private void compareValuesForAgreement(ArrayList list1, ArrayList list2) {
		// TODO Auto-generated method stub
		boolean match = false;
		for (int i = 0; i < list1.size(); i++) {
			int id1 = (Integer) list1.get(i);
			String str1 = (String) valuelist.get(id1);
			ArrayList duration1 = (ArrayList) durationlist.get(id1);
			for (int j = 0; j < list2.size(); j++) {
				int id2 = (Integer) list2.get(j);
				String str2 = (String) valuelist.get(id2);
				ArrayList duration2 = (ArrayList) durationlist.get(id2);
				double score = attrsim.StrSim(str1, str2);
				//System.out.println("\t\t"+str1+"\t"+str2+"\t"+score);
				if (score >= th) {
					match = true;
					int delta = getDelta(duration1, duration2);
					System.out.println("\t\t" + str1 + "\t" + str2 + "\t" + score + "\t" + delta);
					updateDecay(delta);
				}

			}
		}
		if (!match) {
			int delta = 10000;
			updateDecay(delta);

		}
	}

	private void updateDecay(int delta) {
		// TODO Auto-generated method stub
		if (!decaylist.containsKey(delta)) {
			int count = 1;
			decaylist.put(delta, count);
			//System.out.println("add to agr decay \t"+delta+"\t"+count);
		} else {
			int count = decaylist.get(delta);
			count += 1;
			decaylist.put(delta, count);
			//System.out.println("add to agr decay \t"+delta+"\t"+count);
		}
	}

	private int getDelta(ArrayList duration1, ArrayList duration2) {
		// TODO Auto-generated method stub
		int delta = 0;
		String d10 = (String) duration1.get(0);
		String d11 = (String) duration1.get(1);
		//System.out.println(d10+"\t"+d11);

		String d20 = (String) duration2.get(0);
		String d21 = (String) duration2.get(1);
		//System.out.println(d20+"\t"+d21);

		if (Integer.valueOf(d10) < Integer.valueOf(d20)) {
			delta = Math.max(Integer.valueOf(d20) - Integer.valueOf(d11) + 1, 0);
		} else {
			delta = Math.max(Integer.valueOf(d10) - Integer.valueOf(d21) + 1, 0);
		}
		return delta;
	}

	private void addValues(HashMap<String, ArrayList> values, int current_entity) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList> element : values.entrySet()) {
			String value = element.getKey();
			ArrayList duration = element.getValue();
			System.out.println(current_entity + "\t" + value + "\t from \t" + duration.get(0) + "\t to \t" + duration.get(1));

			valuelist.add(seq, value);
			durationlist.add(seq, duration);
			entitylist.add(seq, current_entity);

			if (!invertedlist.containsKey(current_entity)) {
				ArrayList list = new ArrayList();
				list.add(list.size(), seq);
				invertedlist.put(current_entity, list);
			} else {
				ArrayList list = invertedlist.get(current_entity);
				list.add(list.size(), seq);
				invertedlist.put(current_entity, list);
			}

			seq += 1;

		}
	}

	private void createNewValue(String value, String year,
								HashMap<String, ArrayList> values) {
		// TODO Auto-generated method stub
		ArrayList duration = new ArrayList();
		duration.add(0, year);
		duration.add(1, year);
		values.put(value, duration);
		//System.out.println("\t add "+value +"\t from \t"+duration.get(0)+"\t to \t"+duration.get(1));


	}

}
