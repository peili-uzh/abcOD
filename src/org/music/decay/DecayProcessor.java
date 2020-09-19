package org.music.decay;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;
import org.music.similarity.AttributeSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DecayProcessor extends Utility {

	public ArrayList artists;

	public String atr;

	public int seq;

	private double th = 0.8;

	public ArrayList labels;
	public ArrayList relabels;
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

	public DecayProcessor(String sql, String entity) throws Exception {
		if (entity.equals("label")) {
			String clusters = "";
			clusters = getClusters(sql);

			this.labels = getData(clusters, entity);


		} else if (entity.equalsIgnoreCase("releaselabel")) {
			String clusters = "";
			clusters = getClusters(sql);
			this.relabels = getData(clusters, entity);

		}
		attrsim = new AttributeSimilarity();
	}


	public DecayProcessor(String sql) throws Exception {

		String clusters = "";
		clusters = getClusters(sql);
		//System.out.println(clusters.size());
		this.artists = queryRecords(clusters);

		attrsim = new AttributeSimilarity();
	}

	public void singValueProcess(String atr, Decay decay) {

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

		LinkedHashMap<String, ArrayList> values = new LinkedHashMap<String, ArrayList>();

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


			System.out.println("+++" + i + "\t" + cluster_id + "\t" + value + "\t" + year);

			if (!value.equals("")) {


				if (current_entity != cluster_id) {

					if (!values.isEmpty()) {
						addValues(values, current_entity);
					}

					current_entity = cluster_id;
					entities.add(entities.size(), current_entity);
					values = new LinkedHashMap<String, ArrayList>();

					createNewValue(value, year, values);

				} else {

					boolean matched = false;
					for (Map.Entry<String, ArrayList> element : values.entrySet()) {

						String previous_value = element.getKey();
						ArrayList previous_duration = element.getValue();
						double score = attrsim.StrSim(previous_value, value);
						if (score >= th) {
							matched = true;
							int end = Integer.valueOf(year) + 1;
							previous_duration.add(1, String.valueOf(end));
							values.put(previous_value, previous_duration);
							//System.out.println("\t update "+previous_value +"\t from \t"+previous_duration.get(0)+"\t to \t"+previous_duration.get(1));
						}
					}
					if (!matched) {
					/*
					if(values.size()==1){
						for(Map.Entry<String, ArrayList> element:values.entrySet()){
							String previous_value = element.getKey();
							ArrayList previous_duration = element.getValue();
							previous_duration.add(1, year);
						}
					}*/

						addValues(values, current_entity);

						//values = new LinkedHashMap<String, ArrayList>();

						//createNewValue(value, year, values);
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

		LinkedHashMap<String, ArrayList> values = new LinkedHashMap<String, ArrayList>();

		ArrayList dataset = new ArrayList();

		if (artists != null)
			dataset.addAll(artists);
		else if (labels != null)
			dataset.addAll(labels);
		else if (relabels != null)
			dataset.addAll(relabels);

		for (int i = 0; i < dataset.size(); i++) {
			String year = "";
			String value = "";
			int cluster_id = 0;

			if (artists != null) {
				ArtistData artist = (ArtistData) dataset.get(i);
				cluster_id = artist.cluster_id;
				year = artist.getDate();
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
			} else if (labels != null) {
				LabelData label = (LabelData) dataset.get(i);
				cluster_id = label.getCluster_id();
				year = label.getDate();
				if (this.atr.equals("release"))
					value = label.getRelease();
				else if (this.atr.equals("name"))
					value = label.getName();
				else if (this.atr.equals("genres")) {
					ArrayList list = label.getGenres();
					for (int n = 0; n < list.size(); n++) {
						value += list.get(n) + " ";
					}
				} else if (this.atr.equals("country"))
					value = label.getCountry();
				else if (this.atr.equals("artist"))
					value = label.getArtist();
				else if (this.atr.equals("styles")) {
					ArrayList list = label.getStyle();
					for (int n = 0; n < list.size(); n++) {
						value += list.get(n) + " ";
					}
				}
			} else if (relabels != null) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);
				cluster_id = relabel.getCluster_id();
				year = String.valueOf(relabel.getDate());
				if (this.atr.equalsIgnoreCase("label"))
					value = relabel.getLabel();
				else if (this.atr.equalsIgnoreCase("extrartist"))
					value = relabel.getStrextrartist();
				else if (this.atr.equalsIgnoreCase("styles")) {
					ArrayList list = relabel.getStyleslist();
					for (int n = 0; n < list.size(); n++) {
						value += list.get(n) + " ";
					}
				} else if (this.atr.equalsIgnoreCase("artist"))
					value = relabel.getStrartist();
				else if (this.atr.equalsIgnoreCase("country"))
					value = relabel.getCountry();
				else if (this.atr.equalsIgnoreCase("genres")) {
					ArrayList list = relabel.getGenreslist();
					for (int n = 0; n < list.size(); n++) {
						value += list.get(n) + " ";
					}
				} else if (this.atr.equalsIgnoreCase("release"))
					value = relabel.getRelease();
				else if (this.atr.equalsIgnoreCase("catno"))
					value = relabel.getCatno();
				else if (this.atr.equalsIgnoreCase("format"))
					value = relabel.getFormat() + "\t" + relabel.getQty() + "\t" + relabel.getFormat_description();
			}


			//

			if (!value.equals("")) {

				if (current_entity != cluster_id) {

					System.out.println(i + "\t" + cluster_id);

					if (!values.isEmpty()) {
						addValues(values, current_entity);
					}

					current_entity = cluster_id;
					entities.add(entities.size(), current_entity);
					values = new LinkedHashMap<String, ArrayList>();
					createNewValue(value, year, values);

				} else {

					boolean matched = false;
					for (Map.Entry<String, ArrayList> element : values.entrySet()) {

						String previous_value = element.getKey();
						ArrayList previous_duration = element.getValue();
						double score = attrsim.StrSim(previous_value, value);
						if (score >= th) {
							matched = true;
							int end = Integer.valueOf(year) + 1;
							previous_duration.add(1, String.valueOf(end));
							values.put(previous_value, previous_duration);
							//System.out.println("\t update "+previous_value +"\t from \t"+previous_duration.get(0)+"\t to \t"+previous_duration.get(1));
						}
					}
					if (!matched) {
						if (values.size() == 1) {
							for (Map.Entry<String, ArrayList> element : values.entrySet()) {
								String previous_value = element.getKey();
								ArrayList previous_duration = element.getValue();
								previous_duration.add(1, year);
							}
						}


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

	}


	private LinkedHashMap<String, ArrayList> processValues(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	private void addValues(HashMap<String, ArrayList> values, int current_entity) {
		// TODO Auto-generated method stub
		for (Map.Entry<String, ArrayList> element : values.entrySet()) {
			String value = element.getKey();
			ArrayList duration = element.getValue();
			System.out.println("\t" + value + "\t from \t" + duration.get(0) + "\t to \t" + duration.get(1));

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
		int end = Integer.valueOf(year) + 1;
		ArrayList duration = new ArrayList();
		duration.add(0, year);
		duration.add(1, String.valueOf(end));
		values.put(value, duration);
		//System.out.println("\t add "+value +"\t from \t"+duration.get(0)+"\t to \t"+duration.get(1));


	}

}
