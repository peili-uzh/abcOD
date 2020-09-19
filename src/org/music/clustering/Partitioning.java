package org.music.clustering;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.data.Cluster;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Partitioning extends AbstractClustering {

    private static int seq = 0;

    public Partitioning(ArrayList records, HashMap<Integer, Block> blocks) {
        this.records = records;
        this.blocks = blocks;

    }

    public static void process() {
        para = 0;

        while (para <= 1) {

            System.out.println();

            clusterinvertedList = new HashMap();
            clusters = new HashMap();

            for (Entry<Integer, Block> e : blocks.entrySet()) {
                int block_id = e.getKey();
                Block block = e.getValue();
                ArrayList entities = block.getEntities();
                //System.out.println(block_id+"\t"+entities);
                for (int i = 0; i < entities.size(); i++) {
                    int id1 = (Integer) entities.get(i);
                    int clusterID1 = 0;
                    for (int j = i + 1; j < entities.size(); j++) {

                        if (clusterinvertedList.containsKey(id1))
                            clusterID1 = clusterinvertedList.get(id1);
                        //System.out.println("record "+id1+"\t is in cluster \t"+clusterID1);

                        int id2 = (Integer) entities.get(j);
                        int clusterID2 = 0;
                        if (clusterinvertedList.containsKey(id2))
                            clusterID2 = clusterinvertedList.get(id2);
                        //System.out.println("record "+id2+"\t is in cluster \t"+clusterID2);

                        if (id1 != id2) {
                            if (clusterID1 != clusterID2 || clusterID1 == 0) {
                                ArtistData a1 = (ArtistData) records.get(id1);
                                ArtistData a2 = (ArtistData) records.get(id2);
                                double score = sim.recordSimilarity(a1, a2);
                                //System.out.println(a1.getReal_name()+"\t v.s. \t"+a2.getReal_name()+"\t"+score);
                                if (score >= para)
                                    updateCluster(clusterinvertedList, clusterID1, clusterID2, clusters, id1, id2);
                                else {
                                    if (clusterID1 == 0)
                                        addNewCluster(id1);
                                    if (clusterID2 == 0)
                                        addNewCluster(id2);
                                }

                            } else {
                                //System.out.println(id1+"\t v.s. \t"+id2+"\t"+clusterID1+"\t"+clusterID2);
                            }

                        }
                    }
                }
            }

            Evaluation evaluation = new Evaluation();
            System.out.println(para + "\t ----------- partitioning cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);
            para += 0.1;
        }

    }

    private static void updateCluster(
            HashMap<Integer, Integer> clusterinvertedList, int clusterID1,
            int clusterID2, HashMap<Integer, Cluster> clusters, int id1, int id2) {
        // TODO Auto-generated method stub
        // update clusters
        ArrayList entities2 = new ArrayList();
        Cluster cluster2;
        if (clusterID1 == 0 && clusterID2 == 0) {

            addNewCluster(id1);
            clusterID1 = clusterinvertedList.get(id1);
            updateSingleCluster(id2, clusterID1);


        } else if (clusterID1 != 0 && clusterID2 == 0) {

            updateSingleCluster(id2, clusterID1);

        } else if (clusterID1 == 0 && clusterID2 != 0) {

            updateSingleCluster(id1, clusterID2);
        } else {
            updateMultiClusters(clusterID1, clusterID2);
        }


    }


    protected static void addNewCluster(int id1) {
        // TODO Auto-generated method stub
        seq += 1;
        int clusterID1 = seq;
        //System.out.println("add new cluster \t"+clusterID1+"\t"+id1);
        ArrayList entities1 = new ArrayList();

        entities1.add(id1);
        Cluster cluster1 = new Cluster(clusterID1, entities1);
        clusters.put(clusterID1, cluster1);
        //System.out.println("cluster size \t"+clusters.size());

        clusterinvertedList.put(id1, clusterID1);
    }


}
