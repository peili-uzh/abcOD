package org.music.temporalblocking;

import org.music.data.ArtistData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TemporalBlocking extends Block {

	public TemporalBlocking(int t) {
		this.t = t;
	}

	;

	public int t;
	public int seq = 0;

	public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, LinkedHashMap<Integer, ArrayList> blockinvertedlist, HashMap<String, Integer> blocklist, HashMap<Integer, String> blockeylist) {
		// TODO Auto-generated method stub
		this.artists = artists;
		this.blocks = new LinkedHashMap<Integer, ArrayList>();
		this.blockinvertedlist = new LinkedHashMap<Integer, ArrayList>();
		this.blocklist = blocklist;


		TimelessBlocking timelessblock = new TimelessBlocking();

		//System.out.println("process temporal block");


		timelessblock.process(artists, blocks, blockinvertedlist, blocklist, blockeylist);

		blockeylist = timelessblock.getBlockeylist();

		this.blockeylist = new HashMap<Integer, String>();


		//System.out.println(blocks.size());
		timelist = timelessblock.getTimelist();

		for (Map.Entry<Integer, ArrayList> e : blocks.entrySet()) {
			int bid = e.getKey();
			ArrayList list = e.getValue();

			if (list.size() > 1) {
				System.out.println();
				System.out.println(bid + "\t" + blockeylist.get(bid) + "\t" + list);
				System.out.println();
				System.out.println("\t id \t year \t cluster_id");

				HashMap<Integer, LinkedHashMap<Integer, Integer>> hisgram = new HashMap<Integer, LinkedHashMap<Integer, Integer>>();

				//System.out.println(bid+"\t"+list.size());
				int start = 0;
				int end = start + t;
				int finalid = (Integer) list.get(list.size() - 1);
				int finalyr = Integer.valueOf(timelist.get(finalid));

				int firstid = (Integer) list.get(0);
				int firstyr = Integer.valueOf(timelist.get(firstid));

				for (int i = 0; i < list.size(); i++) {
					int id = (Integer) list.get(i);
					int year = Integer.valueOf(timelist.get(id));
					ArtistData a1 = (ArtistData) artists.get(id);
					int cid = a1.getCluster_id();
					System.out.println("\t" + id + "\t" + year + "\t" + a1.getCluster_id());

					updateHisgram(hisgram, cid, year);

					int localend = Math.min(year + t, finalyr);
					if (localend > end) {
						start = year;
						end = localend;
						int newbid = seq;
						String newkey = "";
						if (blockeylist.containsKey(bid))
							newkey = blockeylist.get(bid) + ":" + String.valueOf(start) + "-" + String.valueOf(end);
						//newkey = blockeylist.get(bid+":"+String.valueOf(start)+"-"+String.valueOf(end));
						//System.out.println("\t temporal block "+newkey);
						this.blockeylist.put(newbid, newkey);


						for (int j = i; j < list.size(); j++) {
							int id2 = (Integer) list.get(j);
							int year2 = Integer.valueOf(timelist.get(id2));
							ArtistData a2 = (ArtistData) artists.get(id2);
							if (year2 <= end && year2 >= start) {
								//System.out.println("\t\t add record \t"+id2+"\t"+year2+"\t"+a2.getDate()+"\t"+a2.getCluster_id());

								updateBlocks(newbid, id2);
								updateInvertedList(newbid, id2);
								//addBlock(seq, );
							}
						}
						seq++;
					}


				}

				printHisgram(hisgram, firstyr, finalyr);
			}


		}

		//printMap(this.blocks);
		//printMap(this.blockinvertedlist);

		for (Map.Entry<Integer, String> e : this.blockeylist.entrySet()) {
			int bid = e.getKey();
			String key = e.getValue();
			ArrayList list = this.blocks.get(bid);
			//System.out.println("\t temporal block "+bid+"\t"+key+"\t"+list.size());

		}

	}

	private void printHisgram(HashMap<Integer, LinkedHashMap<Integer, Integer>> hisgram, int firstyr, int finalyr) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, LinkedHashMap<Integer, Integer>> e : hisgram.entrySet()) {
			int cid = e.getKey();
			System.out.println(cid);
			System.out.println();
			System.out.println("\t year \t count");
			LinkedHashMap<Integer, Integer> slot = e.getValue();

			for (int i = firstyr; i <= finalyr; i++) {
				int count = 0;
				if (slot.containsKey(i))
					count = slot.get(i);
				System.out.println("\t" + i + "\t" + count);
			}
			/*
			for(Map.Entry<Integer, Integer> s: slot.entrySet()){
				int year = s.getKey();
				int count = s.getValue();
				System.out.println("\t"+year+"\t"+count);
			}*/
		}
	}

	private void updateHisgram(HashMap<Integer, LinkedHashMap<Integer, Integer>> hisgram, int cid, int year) {
		// TODO Auto-generated method stub
		LinkedHashMap<Integer, Integer> slot = new LinkedHashMap<Integer, Integer>();
		slot.put(year, 1);

		if (hisgram.containsKey(cid)) {
			slot = hisgram.get(cid);
			int count = 1;
			if (slot.containsKey(year))
				count = slot.get(year) + 1;
			slot.put(year, count);
		}
		hisgram.put(cid, slot);
	}

	
	/*
	public void updateBlocks(int bid, int id) {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		if(!blocks.containsKey(bid))
			list = blocks.get(bid);
			
		if(!list.contains(id)){
			list.add(list.size(), id);
			blocks.put(bid, list);
		}
		
	}*/

}
