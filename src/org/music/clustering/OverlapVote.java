package org.music.clustering;

import java.util.ArrayList;
import java.util.HashSet;

public class OverlapVote extends AbstractOverlap {

    public OverlapVote() {
    }


    public int getOptCluster(int id1, ArrayList blocklist1, ArrayList keys) {
        // TODO Auto-generated method stub

        //System.out.println("start overlap VOTE");
        int cluster1 = -1;

        double maxcost = 0;
        HashSet checkedset = new HashSet();
        HashSet checkedcluster = new HashSet();

        for (int j = 0; j < blocklist1.size(); j++) {
            int block1 = (Integer) blocklist1.get(j);
            ArrayList entities = blocks.get(block1).getEntities();
            //System.out.println("\t ----- procedd block \t"+block1+"\t"+entities);
            for (int n = 0; n < entities.size(); n++) {
                int id2 = (Integer) entities.get(n);

                if (!checkedset.contains(id2)) {
                    checkedset.add(id2);
                    int cluster2 = -1;
                    if (clusterinvertedList.containsKey(id2))
                        cluster2 = clusterinvertedList.get(id2);
                    //System.out.println("\t\t ----- compare w record \t"+id2+" in cluster \t"+cluster2);
                    if (!checkedcluster.contains(cluster2)) {
                        checkedcluster.add(cluster2);
                        if (cluster2 != -1) {
                            double cost = computeClusterCost(id1, cluster2);
                            if (cost > maxcost) {
                                maxcost = cost;
                                cluster1 = cluster2;
                                //System.out.println("\t\t\t maxcost in cluster \t"+cluster1);
                            }

                        }
                    }
                }

            }
        }

        return cluster1;
    }


    private static double computeClusterCost(int id1, int cluster2) {
        // TODO Auto-generated method stub
        double quality = 0;
        //ArtistData a1 = (ArtistData) records.get(id1);

        for (int i = 0; i < clusters.get(cluster2).getEntities().size(); i++) {
            int id2 = (Integer) clusters.get(cluster2).getEntities().get(i);
            //ArtistData a2 = (ArtistData) records.get(id2);
            //double cost = getNodeCost(a1, a2);
            double cost = getABSWeight(id1, id2);

            quality += cost;


        }

        //System.out.println("\t\t\t"+id1+"\t"+cluster2+"\t"+quality);

        return quality;
    }
}
