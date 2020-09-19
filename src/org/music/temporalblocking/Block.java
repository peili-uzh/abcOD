package org.music.temporalblocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Block {

    public Block() {
    }

    ;

    public ArrayList artists;
    public LinkedHashMap<Integer, ArrayList> blocks; // block_id - record ids (blocks are sorted)
    public HashMap<Integer, ArrayList> blockinvertedlist; // record id - block ids (blocks are sorted)
    public HashMap<String, Integer> blocklist; //  block key - block id
    public HashMap<Integer, String> timelist; // record id - year
    public HashMap<Integer, String> blockeylist; // block id  - block key
    public ArrayList labels;


    public HashMap<Integer, String> getBlockeylist() {
        return blockeylist;
    }

    public void setBlockeylist(HashMap<Integer, String> blockeylist) {
        this.blockeylist = blockeylist;
    }

    public HashMap<Integer, String> getBlocKeys(
            HashMap<String, Integer> blocklist) {
        // TODO Auto-generated method stub
        HashMap<Integer, String> blockeylist = new HashMap<Integer, String>();

        for (Map.Entry<String, Integer> e : blocklist.entrySet()) {
            String token = e.getKey();
            int blockid = e.getValue();

            blockeylist.put(blockid, token);
            //System.out.println("+++"+blockid+"\t"+token);
        }

        return blockeylist;
    }

    public void printList(HashMap<String, Integer> list) {
        // TODO Auto-generated method stub
        for (Map.Entry<String, Integer> e : list.entrySet()) {
            String token = e.getKey();
            int bid = e.getValue();
            System.out.println(bid + "\t" + token);
        }
    }

    public void printKeyList(HashMap<Integer, String> list) {
        // TODO Auto-generated method stub
        for (Map.Entry<Integer, String> e : list.entrySet()) {
            String token = e.getValue();
            int bid = e.getKey();
            System.out.println(bid + "\t" + token);
        }
    }

    public void printMap(LinkedHashMap<Integer, ArrayList> map) {
        // TODO Auto-generated method stub
        for (Map.Entry<Integer, ArrayList> e : map.entrySet()) {
            int bid = e.getKey();

            ArrayList list = e.getValue();
            System.out.println(bid + "\t" + list);
            for (int i = 0; i < list.size(); i++) {
                int id = (Integer) list.get(i);
                //System.out.println("\t"+id+"\t"+timelist.get(id));
            }

        }
    }

    public ArrayList getArtists() {
        return artists;
    }


    public void setArtists(ArrayList artists) {
        this.artists = artists;
    }


    public LinkedHashMap<Integer, ArrayList> getBlocks() {
        return blocks;
    }


    public void setBlocks(LinkedHashMap<Integer, ArrayList> blocks) {
        this.blocks = blocks;
    }


    public void updateBlocks(int bid, int id) {
        // TODO Auto-generated method stub
        ArrayList list = new ArrayList();
        if (blocks.containsKey(bid))
            list = blocks.get(bid);

        if (!list.contains(id)) {
            list.add(list.size(), id);
            blocks.put(bid, list);
        }

    }

    public void updateInvertedList(int bid, int id) {
        // TODO Auto-generated method stub
        ArrayList blist = new ArrayList();
        if (blockinvertedlist.containsKey(id))
            blist = blockinvertedlist.get(id);
        if (!blist.contains(bid)) {
            blist.add(blist.size(), bid);
            blockinvertedlist.put(id, blist);
        }
    }


    public HashMap<Integer, ArrayList> getBlockinvertedlist() {
        return blockinvertedlist;
    }

    public void setBlockinvertedlist(HashMap<Integer, ArrayList> blockinvertedlist) {
        this.blockinvertedlist = blockinvertedlist;
    }

    public HashMap<String, Integer> getBlocklist() {
        return blocklist;
    }


    public void setBlocklist(HashMap<String, Integer> blocklist) {
        this.blocklist = blocklist;
    }


    public HashMap<Integer, String> getTimelist() {
        return timelist;
    }


    public void setTimelist(HashMap<Integer, String> timelist) {
        this.timelist = timelist;
    }


    public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, LinkedHashMap<Integer, ArrayList> blockinvertedlist, HashMap<String, Integer> blocklist, HashMap<Integer, String> blockeylist) {
        // TODO Auto-generated method stub

    }

    public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, LinkedHashMap<Integer, ArrayList> blockinvertedlist, HashMap<String, Integer> blocklist, HashMap<Integer, String> blockeylist) {
        // TODO Auto-generated method stub

    }

}
