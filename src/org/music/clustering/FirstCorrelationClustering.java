package org.music.clustering;

import org.music.data.ArtistData;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class FirstCorrelationClustering extends AbstractCorrelationClustering {

    public FirstCorrelationClustering(ArrayList records,
                                      HashMap<Integer, Block> blocks) {
        super(records, blocks, blockinvertedList);
        // TODO Auto-generated constructor stub
    }

    public static void process() {

        para = 5;
        double minobj = Double.valueOf(Math.pow(records.size(), 2));
        for (int count = 0; count < para; count++) {

            clusterinvertedList = new HashMap();
            clusters = new HashMap();

            for (Entry<Integer, Block> e : blocks.entrySet()) {
                int block_id = e.getKey();
                Block block = e.getValue();
                scorelist = new LinkedList();
                pairlist = new HashMap();
                //System.out.println("+++ process block \t"+block_id+"\t"+block.getEntities());

                ArrayList entities = block.getEntities();
                getInitialClusters(entities);

                Collections.shuffle(worklist);
                for (int i = 0; i < worklist.size(); i++) {
                    //System.out.println(i+"\t"+worklist.get(i));
                    int id1 = (Integer) worklist.get(i);

                    int cluster1 = -1;
                    //System.out.println("\t ++++ process record \t"+id1);

                    if (i > 0) {
                        for (int j = i - 1; j >= 0; j--) {
                            int id2 = (Integer) worklist.get(j);
                            cluster1 = compareToClusters(id1, id2);
                            if (cluster1 != -1)
                                j = -1;
                        }
                    }


                    updateClusters(cluster1, id1);

                }
            }

            double objvalue = getOBJFunction();
            System.out.println(count + "\t ++++++ object value \t" + objvalue);
            if (objvalue < minobj) {
                minobj = objvalue;
                bestinvertedList = clusterinvertedList;
                bestclusters = clusters;
            }


            Evaluation evaluation = new Evaluation();
            System.out.println("\r\n -----------first correlation cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);
        }

        Evaluation evaluation = new Evaluation();
        System.out.println("-----------final first correlation cluster quality -------------");
        evaluation.ClusterEvaluation(records, bestinvertedList);

    }


    private static int compareToClusters(int id1, int id2) {
        // TODO Auto-generated method stub


        double cost = getABSWeight(id1, id2);
        //System.out.println("\t\t"+id1+"\t"+id2+"\t"+cost);

        int cluster1 = -1;

        if (cost > 0)
            cluster1 = clusterinvertedList.get(id2);


        return cluster1;
    }

    private static double computeLocalCost(int id1, int cluster2) {
        // TODO Auto-generated method stub
        double quality = 0;
        ArtistData a1 = (ArtistData) records.get(id1);

        for (int i = 0; i < clusters.get(cluster2).getEntities().size(); i++) {
            int id2 = (Integer) clusters.get(cluster2).getEntities().get(i);
            ArtistData a2 = (ArtistData) records.get(id2);
            //double cost = getNodeCost(a1, a2);
            double score = sim.recordSimilarity(a1, a2);
            double cost = 2 * score - 1;
            //System.out.println("\t\t\t"+id1+"\t"+id2+"\t"+score+"\t"+cost);
            if (cost > quality)
                quality = cost;
        }

        return quality;
    }


}
