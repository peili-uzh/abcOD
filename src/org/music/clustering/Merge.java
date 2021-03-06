package org.music.clustering;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.*;

public class Merge extends AbstractClustering {

    public Merge(ArrayList records, HashMap<Integer, Block> blocks) {
        this.records = records;
        this.blocks = blocks;

        this.pairlist = new HashMap();
        this.scorelist = new LinkedList();
    }

    public static void process() {
        para = 0.1;

        sortRecordPair();

        /*
         * print out socre list
         */
        while (para <= 1) {
            System.out.println();

            clusterinvertedList = new HashMap();
            clusters = new HashMap();
            int i = 0;
            while (i < scorelist.size()) {
                //for(int i=0; i<scorelist.size(); i++){
                double score = scorelist.get(i);
                //System.out.println(score);
                if (pairlist.containsKey(score)) {
                    HashSet set = pairlist.get(score);
                    Iterator it = set.iterator();
                    while (it.hasNext()) {
                        ArrayList list = (ArrayList) it.next();
                        //System.out.println("\t"+list);
                        int id1 = (Integer) list.get(0);
                        int id2 = (Integer) list.get(1);

                        if (score >= para) {
                            if (!clusterinvertedList.containsKey(id2)) {
                                if (!clusterinvertedList.containsKey(id1))
                                    addNewCluster(id1);
                                int cluster1 = clusterinvertedList.get(id1);
                                if (cluster1 == id1)
                                    updateSingleCluster(id2, cluster1);
                                else {
                                    addNewCluster(id2);
                                    updateMultiClusters(id2, cluster1);
                                }

                            } else if (!clusterinvertedList.containsKey(id1)) {
                                int cluster2 = clusterinvertedList.get(id2);
                                if (cluster2 == id2)
                                    updateSingleCluster(id1, cluster2);
                                else {
                                    addNewCluster(id1);
                                    updateMultiClusters(id1, cluster2);
                                }

                            } else {
                                int cluster1 = clusterinvertedList.get(id1);
                                int cluster2 = clusterinvertedList.get(id2);
                                //System.out.println("clusters \t"+cluster1+"\t"+cluster2);
                                if (cluster1 != cluster2) {
                                    if (cluster1 == id1 || cluster2 == id2) {

                                        updateMultiClusters(cluster1, cluster2);
                                        //System.out.println("both records are in clusters \t"+id1+"\t"+clusterinvertedList.get(id1)+"\t"+id2+"\t"+clusterinvertedList.get(id2));

                                    }


                                }
                            }
                        } else {
                            if (!clusterinvertedList.containsKey(id1))
                                addNewCluster(id1);
                            if (!clusterinvertedList.containsKey(id2))
                                addNewCluster(id2);
                        }


                    }
                }

                i++;
            }

            Evaluation evaluation = new Evaluation();
            System.out.println(para + "\t ----------- merge cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);
            para += 0.1;
        }

    }
}
