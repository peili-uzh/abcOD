package org.music.clustering;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.*;
import java.util.Map.Entry;

public class HACProcesser extends AbstractHAC {

    public HACProcesser(ArrayList records, HashMap<Integer, Block> blocks) {

        this.records = records;
        this.blocks = blocks;


    }

    public void process(HAC hac) {

        para = 0.1;

        while (para <= 1) {

            //System.out.println();
            clusterinvertedList = new HashMap();
            clusters = new HashMap();

            for (Entry<Integer, Block> e : blocks.entrySet()) {
                int block_id = e.getKey();
                Block block = e.getValue();
                validset = new HashSet();
                scorelist = new LinkedList();
                pairlist = new HashMap();
                pairset = new HashMap();
                //System.out.println("+++ process block \t"+block_id+"\t"+block.getEntities());

                ArrayList entities = block.getEntities();
                for (int i = 0; i < entities.size(); i++) {
                    int id1 = (Integer) entities.get(i);
                    int cluster1 = initialCluster(id1);
                    validset.add(cluster1);
                    for (int j = i + 1; j < entities.size(); j++) {
                        int id2 = (Integer) entities.get(j);

                        if (id1 != id2) {
                            int cluster2 = initialCluster(id2);
                            ArrayList pair = createPair(cluster1, cluster2);
                            if (pair != null) {
                                if (!pairset.containsKey(pair)) {
                                    double score = hac.ClusterSim(cluster1, cluster2);
                                    //System.out.println(id1+"\t"+id2+"\t"+score);
                                    insertPair(pair, score);
                                    insertScoreList(score);
                                }
                            }


                        }
                    }
                }
                //System.out.println(scorelist);
                double max = 0;
                if (!scorelist.isEmpty())
                    max = scorelist.get(0);

                //System.out.println(max+"\t"+para);

                while (max >= para) {
                    double maxscore = scorelist.get(0);
                    HashSet set = pairlist.get(maxscore);
                    ArrayList pair = (ArrayList) set.iterator().next();
                    int cluster1 = (Integer) pair.get(0);
                    int cluster2 = (Integer) pair.get(1);
                    if (validset.contains(cluster1) && validset.contains(cluster2)) {
                        //System.out.println("merge \t"+pair+"\t"+maxscore);

                        updateMultiClusters(cluster1, cluster2);
                        //System.out.println("cluster \t"+cluster1+"\t"+clusters.get(cluster1).getEntities());
                        validset.remove(cluster2);

                        updatePairScore(cluster1, cluster2, hac);
                    }

                    max = 0;
                    if (!scorelist.isEmpty())
                        max = scorelist.get(0);
                }

            }

            Evaluation evaluation = new Evaluation();
            //System.out.println(para+"\t ----------- single link HAC cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);

            para += 0.1;

        }


    }

    protected void updatePairScore(int cluster1, int cluster2, HAC hac) {
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
                score = hac.ClusterSim(cluster1, cluster0);
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

}
