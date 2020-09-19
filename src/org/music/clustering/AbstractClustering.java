package org.music.clustering;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.data.Cluster;
import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

public class AbstractClustering {

    protected static ArrayList records;
    protected static HashMap<Integer, Block> blocks;
    protected static HashMap<Integer, ArrayList> blockinvertedList;
    protected static HashMap<Integer, Integer> clusterinvertedList;
    protected static HashMap<Integer, Cluster> clusters;
    protected static double para;
    protected static RecordSimilarity sim;
    protected static HashMap<Double, HashSet> pairlist;
    protected static LinkedList<Double> scorelist;

    public static ArrayList getRecords() {
        return records;
    }

    public static void setRecords(ArrayList records) {
        AbstractClustering.records = records;
    }

    public static HashMap<Integer, Block> getBlocks() {
        return blocks;
    }

    public static void setBlocks(HashMap<Integer, Block> blocks) {
        AbstractClustering.blocks = blocks;
    }

    public static double getPara() {
        return para;
    }

    public static void setPara(double para) {
        AbstractClustering.para = para;
    }

    public static HashMap<Integer, ArrayList> getBlockinvertedList() {
        return blockinvertedList;
    }

    public static void setBlockinvertedList(
            HashMap<Integer, ArrayList> blockinvertedList) {
        AbstractClustering.blockinvertedList = blockinvertedList;
    }

    public static HashMap<Integer, Integer> getClusterinvertedList() {
        return clusterinvertedList;
    }

    public static void setClusterinvertedList(
            HashMap<Integer, Integer> clusterinvertedList) {
        AbstractClustering.clusterinvertedList = clusterinvertedList;
    }

    public static HashMap<Integer, Cluster> getClusters() {
        return clusters;
    }

    public static void setClusters(HashMap<Integer, Cluster> clusters) {
        AbstractClustering.clusters = clusters;
    }

    public static RecordSimilarity getSim() {
        return sim;
    }

    public static void setSim(RecordSimilarity sim) {
        AbstractClustering.sim = sim;
    }

    protected static void addNewCluster(int id1) {
        // TODO Auto-generated method stub
        int clusterID1 = id1;
        //System.out.println("\t\t add new cluster \t"+clusterID1+"\t for \t"+id1);
        ArrayList entities1 = new ArrayList();

        entities1.add(id1);
        Cluster cluster1 = new Cluster(clusterID1, entities1);
        clusters.put(clusterID1, cluster1);
        //System.out.println("cluster size \t"+clusters.size());

        clusterinvertedList.put(id1, clusterID1);
    }

    protected static void updateSingleCluster(int id, int clusterID) {
        // TODO Auto-generated method stub
        Cluster cluster = clusters.get(clusterID);
        ArrayList entities = cluster.getEntities();
        entities.add(id);
        cluster.setEntities(entities);
        clusters.put(clusterID, cluster);
        //System.out.println("\t\t add new record \t"+id+"\t to cluster \t "+clusterID+"\t"+entities);

        clusterinvertedList.put(id, clusterID);

    }

    protected static void sortRecordPair() {
        for (Entry<Integer, Block> e : blocks.entrySet()) {
            int block_id = e.getKey();
            Block block = e.getValue();
            ArrayList entities = block.getEntities();
            for (int i = 0; i < entities.size(); i++) {
                int id1 = (Integer) entities.get(i);
                for (int j = i + 1; j < entities.size(); j++) {
                    int id2 = (Integer) entities.get(j);

                    if (id1 != id2) {
                        ArtistData a1 = (ArtistData) records.get(id1);
                        ArtistData a2 = (ArtistData) records.get(id2);
                        double score = sim.recordSimilarity(a1, a2);
                        insertPairList(score, id1, id2);
                        insertScoreList(score);

                    }
                }
            }
        }
    }

    protected static void updateMultiClusters(int clusterID1,
                                              int clusterID2) {
        // TODO Auto-generated method stub
        // merged clusterID1 w clusterID2, and remove clusterID2
        //System.out.println("merge clusters \t"+clusterID1+"\t and \t"+clusterID2);
        Cluster cluster1 = clusters.get(clusterID1);
        Cluster cluster2 = clusters.get(clusterID2);
        ArrayList entities1 = cluster1.getEntities();
        ArrayList entities2 = cluster2.getEntities();
        entities1.addAll(entities2);
        cluster1.setEntities(entities1);
        clusters.put(clusterID1, cluster1);
        clusters.remove(clusterID2);

        for (int i = 0; i < entities2.size(); i++) {
            int id = (Integer) entities2.get(i);
            clusterinvertedList.put(id, clusterID1);
        }
    }

    protected static void insertScoreList(double score) {
        // TODO Auto-generated method stub
        if (scorelist.contains(score))
            return;
        else if (scorelist.isEmpty())
            scorelist.add(score);
        else if (scorelist.get(0) < score)
            scorelist.add(0, score);
        else if (scorelist.get(scorelist.size() - 1) > score)
            scorelist.add(scorelist.size(), score);
        else {
            int i = 0;
            while (scorelist.get(i) > score)
                i++;
            scorelist.add(i, score);
        }

    }

    private static void insertPairList(double score, int id1, int id2) {
        // TODO Auto-generated method stub
        ArrayList pair = new ArrayList();
        if (id1 > id2) {
            pair.add(0, id2);
            pair.add(1, id1);
        } else {
            pair.add(0, id1);
            pair.add(1, id2);
        }

        if (!pairlist.containsKey(score)) {
            HashSet set = new HashSet();
            set.add(pair);
            pairlist.put(score, set);
        } else {
            HashSet set = pairlist.get(score);
            set.add(pair);
            pairlist.put(score, set);
        }
    }
}
