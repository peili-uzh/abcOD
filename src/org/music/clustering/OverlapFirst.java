package org.music.clustering;

import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class OverlapFirst extends AbstractOverlap {

    public OverlapFirst() {
    }

    public int getOptCluster(int id1, ArrayList blocklist1, ArrayList keys) {

        //System.out.println("start overlap FIRST");

        int cluster1 = -1;

        int maxindex = -1;
        HashSet checkedset = new HashSet();
        for (int j = 0; j < blocklist1.size(); j++) {
            int block1 = (Integer) blocklist1.get(j);
            ArrayList entities = blocks.get(block1).getEntities();
            //System.out.println("\t ----- procedd block \t"+block1+"\t"+entities);
            for (int n = 0; n < entities.size(); n++) {
                int id2 = (Integer) entities.get(n);

                if (!checkedset.contains(id2)) {
                    int cluster2 = -1;
                    if (clusterinvertedList.containsKey(id2))
                        cluster2 = clusterinvertedList.get(id2);
                    //System.out.println("\t\t ----- compare w record \t"+id2+" in cluster \t"+cluster2);
                    if (cluster2 != -1) {
                        double cost = getABSWeight(id1, id2);
                        if (cost > 0) {
                            int m = keys.indexOf(id2);
                            if (m > maxindex) {
                                maxindex = m;
                                cluster1 = cluster2;
                            }

                            //System.out.println("\t\t\t index of "+id2+" = "+m+"\t in cluster \t"+cluster1);
                        }

                    }
                }

            }

        }
        //System.out.println("\t\t add to cluster \t"+cluster1+"\t w max index \t"+maxindex);

        return cluster1;
    }

    public static void process() {

        para = 10;
        double minobj = Double.valueOf(Math.pow(records.size(), 2));
        for (int count = 0; count < para; count++) {

            clusterinvertedList = new HashMap();
            clusters = new HashMap();

            System.out.println();
            ArrayList keys = new ArrayList(blockinvertedList.keySet());

            Collections.shuffle(keys);

            for (int i = 0; i < keys.size(); i++) {
                int id1 = (Integer) keys.get(i);
                ArrayList blocklist1 = blockinvertedList.get(id1);

                //System.out.println(i+"----- proceed record \t"+id1+"\t"+blocklist1);

                //System.out.println("+++ process block \t"+block_id+"\t"+block.getEntities());
                int cluster1 = -1;
                if (clusterinvertedList.containsKey(id1))
                    cluster1 = clusterinvertedList.get(id1);
                if (cluster1 == -1) {
                    if (i > 0) {

                        int maxindex = -1;
                        HashSet checkedset = new HashSet();
                        for (int j = 0; j < blocklist1.size(); j++) {
                            int block1 = (Integer) blocklist1.get(j);
                            ArrayList entities = blocks.get(block1).getEntities();
                            //System.out.println("\t ----- procedd block \t"+block1+"\t"+entities);
                            for (int n = 0; n < entities.size(); n++) {
                                int id2 = (Integer) entities.get(n);

                                if (!checkedset.contains(id2)) {
                                    int cluster2 = -1;
                                    if (clusterinvertedList.containsKey(id2))
                                        cluster2 = clusterinvertedList.get(id2);
                                    //System.out.println("\t\t ----- compare w record \t"+id2+" in cluster \t"+cluster2);
                                    if (cluster2 != -1) {
                                        double cost = getABSWeight(id1, id2);
                                        if (cost > 0) {
                                            int m = keys.indexOf(id2);
                                            if (m > maxindex) {
                                                maxindex = m;
                                                cluster1 = cluster2;
                                            }

                                            //System.out.println("\t\t\t index of "+id2+" = "+m+"\t in cluster \t"+cluster1);
                                        }

                                    }
                                }

                            }

                        }
                        //System.out.println("\t\t add to cluster \t"+cluster1+"\t w max index \t"+maxindex);
                    }

                }


                updateClusters(cluster1, id1);


            }

            double objvalue = getOBJFunction();
            System.out.println("++++++ object value \t" + objvalue);
            if (objvalue < minobj) {
                minobj = objvalue;
                bestinvertedList = clusterinvertedList;
                bestclusters = clusters;
            }


            Evaluation evaluation = new Evaluation();
            //System.out.println("-----------first correlation cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);
        }

        Evaluation evaluation = new Evaluation();
        System.out.println("-----------final first correlation cluster quality -------------");
        evaluation.ClusterEvaluation(records, bestinvertedList);

    }


}
