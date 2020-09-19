package org.music.clustering;

import org.music.data.Cluster;

import java.util.*;

public class AbstractHAC extends AbstractClustering {


    protected static HashMap<ArrayList, Double> pairset;
    protected static HashSet validset;

    protected void updatePairScore(int cluster1, int cluster2) {
        // TODO Auto-generated method stub
        HashSet removeset = new HashSet();
        for (Map.Entry<ArrayList, Double> e : pairset.entrySet()) {
            ArrayList pair = e.getKey();
            double score = e.getValue();
            if (pair.contains(cluster2)) {
                removeset.add(pair);

                removePairFromList(pair, score);


            } else if (pair.contains(cluster1)) {
                int cluster0 = (Integer) pair.get(0);
                if (cluster0 == cluster1)
                    cluster0 = (Integer) pair.get(1);

                //System.out.println(pair+"\t"+"\t"+e.getValue());
                score = ClusterSim(cluster1, cluster0);
                if (score != e.getValue()) {

                    removePairFromList(pair, e.getValue());
                    insertPair(pair, score);
                    insertScoreList(score);


                }
            }
        }

        Iterator it = removeset.iterator();
        while (it.hasNext()) {
            ArrayList pair = (ArrayList) it.next();
            pairset.remove(pair);
        }
    }

    protected double ClusterSim(int cluster1, int cluster2) {
        return 0;
    }


    protected static void removePairFromList(ArrayList pair, double score) {
        // TODO Auto-generated method stub
        if (pairlist.containsKey(score)) {

            HashSet set = pairlist.get(score);
            if (set.contains(pair))
                set.remove(pair);
            //System.out.println("remove "+pair+" w score "+score);
            if (set.isEmpty()) {
                pairlist.remove(score);
                scorelist.remove(score);
            }

        }
    }

    protected static ArrayList createPair(int cluster1, int cluster2) {
        // TODO Auto-generated method stub
        if (cluster1 == cluster2)
            return null;
        else {
            ArrayList pair = new ArrayList();
            if (cluster1 > cluster2) {
                pair.add(0, cluster2);
                pair.add(1, cluster1);
            } else {
                pair.add(0, cluster1);
                pair.add(1, cluster2);
            }
            return pair;
        }

    }

    protected static void insertPair(ArrayList pair, double score) {
        // TODO Auto-generated method stub


        if (!pairlist.containsKey(score)) {
            HashSet set = new HashSet();
            set.add(pair);
            pairlist.put(score, set);
        } else {
            HashSet set = pairlist.get(score);
            set.add(pair);
            pairlist.put(score, set);
        }

        pairset.put(pair, score);
        //System.out.println("\t"+pair+"\t"+score);
    }

    protected static int initialCluster(int id1) {
        // TODO Auto-generated method stub
        int cluster1 = id1;
        if (clusterinvertedList.containsKey(id1))
            cluster1 = clusterinvertedList.get(id1);
        else {
            clusterinvertedList.put(id1, cluster1);
            ArrayList entities = new ArrayList();
            entities.add(id1);
            Cluster cluster = new Cluster(id1, entities);
            clusters.put(id1, cluster);
            //System.out.println("create new cluster for "+id1);
        }

        //System.out.println(clusterinvertedList.size()+"\t record "+id1+" is in cluster \t"+cluster1);
        return cluster1;
    }

}
