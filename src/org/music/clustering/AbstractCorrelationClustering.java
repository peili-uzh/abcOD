package org.music.clustering;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.data.Cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class AbstractCorrelationClustering extends AbstractClustering {

    protected static HashSet initialClusters;
    protected static ArrayList worklist;
    protected static String greedy;
    protected static HashMap<Integer, Integer> bestinvertedList;
    protected static HashMap<Integer, Cluster> bestclusters;

    public AbstractCorrelationClustering(ArrayList records, HashMap<Integer, Block> blocks, HashMap<Integer, ArrayList> blockinvertedList) {

        this.records = records;
        this.blocks = blocks;
        //this.clusterinvertedList = new HashMap();
        //this.clusters = new HashMap();
        bestinvertedList = new HashMap();
        bestclusters = new HashMap();
        this.blockinvertedList = blockinvertedList;

    }

    public AbstractCorrelationClustering() {
    }

    ;

    protected static void updateClusters(int cluster1, int id1) {
        // TODO Auto-generated method stub
        if (cluster1 < 0) {
            addNewCluster(id1);

            if (initialClusters != null) {
                initialClusters.add(id1);
            }


        } else {
            updateSingleCluster(id1, cluster1);
        }
    }

    protected static void getInitialClusters(ArrayList entities) {
        // TODO Auto-generated method stub
        initialClusters = new HashSet();
        worklist = new ArrayList();
        HashSet set = new HashSet();
        for (int i = 0; i < entities.size(); i++) {
            int cluster = -1;
            int id = (Integer) entities.get(i);
            if (clusterinvertedList.containsKey(id))
                cluster = clusterinvertedList.get(id);
            if (cluster == -1)
                worklist.add(id);
            else
                initialClusters.add(cluster);
        }
    }

    protected static double getNodeCost(ArtistData a1, ArtistData a2, int same) {
        // TODO Auto-generated method stub
        double score = sim.recordSimilarity(a1, a2);
        double cost = score;
        if (same == 1)
            cost = 1 - score;
        return cost;
    }

    protected static double getABSWeight(int id1, int id2) {
        // TODO Auto-generated method stub

        ArtistData a1 = (ArtistData) records.get(id1);
        ArtistData a2 = (ArtistData) records.get(id2);
        double score = sim.recordSimilarity(a1, a2);
        double cost = 2 * score - 1;
        //System.out.println("\t\t\t"+id1+"\t"+id2+"\t"+score+"\t"+cost);
        return cost;
    }

    protected static double getOBJFunction() {
        // TODO Auto-generated method stub
        double obj = 0;

        for (int i = 0; i < records.size(); i++) {
            ArtistData a1 = (ArtistData) records.get(i);
            int cluster1 = clusterinvertedList.get(i);
            for (int j = i + 1; j < records.size(); j++) {
                ArtistData a2 = (ArtistData) records.get(j);
                int cluster2 = clusterinvertedList.get(j);
                int same = 0;
                if (cluster2 == cluster1)
                    same = 1;
                double cost = getNodeCost(a1, a2, same);
                obj += cost;
            }
        }


        return obj;
    }

    public static HashMap<Integer, Integer> getBestinvertedList() {
        return bestinvertedList;
    }

    public static void setBestinvertedList(
            HashMap<Integer, Integer> bestinvertedList) {
        AbstractCorrelationClustering.bestinvertedList = bestinvertedList;
    }

    public static HashMap<Integer, Cluster> getBestclusters() {
        return bestclusters;
    }

    public static void setBestclusters(HashMap<Integer, Cluster> bestclusters) {
        AbstractCorrelationClustering.bestclusters = bestclusters;
    }


}
