package org.music.temporalblocking;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class TimelessBlocking extends Block {

	public TimelessBlocking() {
	}

	;

	public int seq;

	public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, LinkedHashMap<Integer, ArrayList> blockinvertedlist, HashMap<String, Integer> blocklist, HashMap<Integer, String> blockeylist) {
		// TODO Auto-generated method stub
		this.blocklist = blocklist;
		this.blocks = blocks;
		this.blockinvertedlist = blockinvertedlist;
		this.blockeylist = blockeylist;

		timelist = new HashMap<Integer, String>();
		int current_entity = 0;
		int current_year = 0;

		seq = 0;

		for (int i = 0; i < dataset.size(); i++) {
			if (entityType.equals("label")) {
				LabelData label = (LabelData) dataset.get(i);

				int id = label.getId();
				String name = label.getName();
				String release = label.getRelease();
				String country = label.getCountry();
				ArrayList genres = label.getGenres();
				ArrayList styles = label.getStyle();
				String year = label.getDate();
				String artist = label.getArtist();
				//System.out.println(i+"\t"+name);

				updateToken(i, name, "name");
				//updateToken(i, release, "release");
				//updateToken(i, country, "country");
				//updateTokens(i, genres, "genres");
				//updateTokens(i, styles, "styles");

				timelist.put(i, year);
			} else if (entityType.equalsIgnoreCase("releaselabel")) {
				ReleaseLabel relabel = (ReleaseLabel) dataset.get(i);

				int id = relabel.getId();
				String label = relabel.getLabel();
				String release = relabel.getRelease();
				String year = String.valueOf(relabel.getDate());
				int clusterid = relabel.getCluster_id();
				updateToken(i, label, "label");

				timelist.put(i, year);

				if (clusterid != current_entity) {
					current_entity = clusterid;

				} else {
					int distance = Math.abs(relabel.getDate() - current_year);
					if (distance > 10) {

					}

					//System.out.println("\t"+id+"\t"+relabel.getLabel()+"\t"+relabel.getRelease()+"\t"+relabel.getStrartist()+"\t"+relabel.getStrextrartist()+"\t"+relabel.getGenreslist()+"\t"+relabel.getStyleslist()+"\t"+relabel.getCountry()+"\t"+relabel.getDate()+"\t"+relabel.getCatno()+"\t"+relabel.getFormat()+"\t"+relabel.getCluster_id());

				}

				current_year = relabel.getDate();
				System.out.println("\t" + id + "\t" + relabel.getLabel() + "\t" + relabel.getRelease() + "\t" + relabel.getStrartist() + "\t" + relabel.getStrextrartist() + "\t" + relabel.getGenreslist() + "\t" + relabel.getStyleslist() + "\t" + relabel.getCountry() + "\t" + relabel.getDate() + "\t" + relabel.getCatno() + "\t" + relabel.getFormat() + "\t" + relabel.getCluster_id());

				//System.out.println(i+"\t"+label+"\t"+year+"\t"+release);
			}

		}

		//printMap(blocks);
		//printMap(blockinvertedlist);

		//printList(blocklist);
		this.blockeylist = getBlocKeys(blocklist);
	}

	public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, LinkedHashMap<Integer, ArrayList> blockinvertedlist, HashMap<String, Integer> blocklist, HashMap<Integer, String> blockeylist) {
		// TODO Auto-generated method stub

		this.blocklist = blocklist;
		this.blocks = blocks;
		this.blockinvertedlist = blockinvertedlist;
		this.blockeylist = blockeylist;

		timelist = new HashMap<Integer, String>();

		seq = 0;

		for (int i = 0; i < artists.size(); i++) {
			ArtistData artist = (ArtistData) artists.get(i);

			int id = artist.getId();
			String name = artist.getName();
			String release = artist.getRelease();
			String country = artist.getCountry();
			ArrayList genres = artist.getGenres();
			ArrayList styles = artist.getStyle();
			String year = artist.getDate();
			//System.out.println(i+"\t"+name);

			updateToken(i, name, "name");
			updateToken(i, release, "release");
			updateToken(i, country, "country");
			updateTokens(i, genres, "genres");
			updateTokens(i, styles, "styles");

			timelist.put(i, year);
		}

		//printMap(blocks);
		//printMap(blockinvertedlist);

		//printList(blocklist);
		this.blockeylist = getBlocKeys(blocklist);
	}

	private void updateTokens(int id, ArrayList list, String attr) {
		// TODO Auto-generated method stub
		for (int i = 0; i < list.size(); i++) {
			String value = (String) list.get(i);
			updateToken(id, value, attr);
		}
	}


	private void updateToken(int id, String value, String attr) {
		// TODO Auto-generated method stub
		if (!value.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(value);
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				token = token.trim();
				//token = "token";
				if (!token.equals("")) {
					token = "(" + attr + ") " + token;
					if (blocklist.containsKey(token)) {
						int bid = blocklist.get(token);
						updateBlocks(bid, id);
						updateInvertedList(bid, id);

					} else {
						blocklist.put(token, seq);
						createNewBlock(id);
						updateInvertedList(seq, id);
						seq++;
					}
				}
			}
		}
	}


	public void updateInvertedList(int bid, int id) {
		// TODO Auto-generated method stub
		ArrayList blist = new ArrayList();
		if (blockinvertedlist.containsKey(id))
			blist = blockinvertedlist.get(id);
		if (!blist.contains(bid)) {
			sortedInsertion(blist, bid);
			//blist.add(blist.size(), bid);
			blockinvertedlist.put(id, blist);
		}
	}

	private int findPosition(ArrayList blist, int id) {
		// TODO Auto-generated method stub
		boolean found = false;
		int i = 0;
		while (!found && i < blist.size()) {
			int id2 = (Integer) blist.get(i);
			if (id2 > id)
				found = true;
			else
				i++;
		}

		return i;
	}


	private void sortedInsertion(ArrayList blist, int bid) {
		// TODO Auto-generated method stub
		if (blist.isEmpty())
			blist.add(blist.size(), bid);
		else {
			int index = 0;
			index = findPosition(blist, bid);
			//System.out.println("\t insert "+bid+" into "+blist+"\t"+index);
			blist.add(index, bid);
			//insertPosition(index, bid, blist);
			//System.out.println("\t\t"+blist);

		}
	}

	private void insertPosition(int index, int bid, ArrayList blist) {
		// TODO Auto-generated method stub
		/*
		int size = blist.size();
		for(int i= size-1; i >= index; i--){
			int bid2 = (Integer) blist.get(i);
			blist.add(i+1, bid2);
			
		}*/
		blist.add(index, bid);
	}

	public void createNewBlock(int id) {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		list.add(list.size(), id);
		blocks.put(seq, list);

	}

}
