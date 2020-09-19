package org.music.clustering;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.*;
import java.util.Map.Entry;

public class BestCorrelationClustering extends AbstractCorrelationClustering {

    public BestCorrelationClustering(ArrayList records, HashMap<Integer, Block> blocks) {
        super(records, blocks, blockinvertedList);
        // TODO Auto-generated constructor stub
    }

    private static String greedy = "BEST";

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
                    int cluster1 = compareToClusters(id1, initialClusters);
                    updateClusters(cluster1, id1);

                }
            }

            double objvalue = getOBJFunction();
            System.out.println("++++++ object value \t" + objvalue);
            if (objvalue < minobj) {
                minobj = objvalue;
                bestinvertedList = clusterinvertedList;
                bestclusters = clusters;
            }

            Evaluation evaluation = new Evaluation();
            System.out.println("-----------" + greedy + " correlation cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);

        }

        Evaluation evaluation = new Evaluation();
        System.out.println("-----------final best correlation cluster quality -------------");
        evaluation.ClusterEvaluation(records, bestinvertedList);

    }


    private static int compareToClusters(int id1, HashSet initialClusters) {
        // TODO Auto-generated method stub
        //System.out.println("\t ++++ process record \t"+id1);

        int cluster1 = -1;
        if (!initialClusters.isEmpty()) {
            double maxquality = 0;
            int maxcluster = -1;

            Iterator it = initialClusters.iterator();
            while (it.hasNext()) {
                int cluster2 = (Integer) it.next();
                //System.out.println("\t\t with cluster \t"+cluster2+"\t"+clusters.get(cluster2).getEntities());
                double quality = computeClusterCost(id1, cluster2);
                if (greedy == "BEST") {
                    if (quality > maxquality) {
                        maxquality = quality;
                        maxcluster = cluster2;
                    }
                }
            }

            if (maxquality > 0) {
                cluster1 = maxcluster;
                //System.out.println("\t\t max cluster "+cluster1+"\t"+maxquality);
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

            //System.out.println("\t\t\t"+id1+"\t"+id2+"\t"+"\t"+cost);
            if (cost > quality)
                quality = cost;
        }

        return quality;
    }


}
