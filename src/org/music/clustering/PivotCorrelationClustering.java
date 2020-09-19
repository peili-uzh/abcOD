package org.music.clustering;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.*;
import java.util.Map.Entry;

public class PivotCorrelationClustering extends AbstractCorrelationClustering {

    public PivotCorrelationClustering(ArrayList records,
                                      HashMap<Integer, Block> blocks) {
        super(records, blocks, blockinvertedList);
        // TODO Auto-generated constructor stub
    }

    private static ArrayList unactivelist;

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
                    //System.out.println("\t ++++ process record \t"+id1);

                    if (!unactivelist.contains(id1)) {

                        addNewCluster(id1);
                        unactivelist.add(id1);

                        for (int j = i + 1; j < worklist.size(); j++) {
                            int id2 = (Integer) worklist.get(j);
                            //System.out.println("\t\t compare w record \t"+id2);
                            double cost = getABSWeight(id1, id2);
                            if (cost > 0) {
                                unactivelist.add(id2);
                                updateSingleCluster(id2, id1);
                            }

                        }
                    }

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
            //System.out.println("-----------first correlation cluster quality -------------");
            evaluation.ClusterEvaluation(records, clusterinvertedList);
        }

        Evaluation evaluation = new Evaluation();
        System.out.println("-----------final first correlation cluster quality -------------");
        evaluation.ClusterEvaluation(records, bestinvertedList);

    }


    protected static void getInitialClusters(ArrayList entities) {
        // TODO Auto-generated method stub
        initialClusters = new HashSet();
        worklist = new ArrayList();
        unactivelist = new ArrayList();

        HashSet set = new HashSet();
        for (int i = 0; i < entities.size(); i++) {
            int cluster = -1;
            int id = (Integer) entities.get(i);
            if (clusterinvertedList.containsKey(id))
                cluster = clusterinvertedList.get(id);
            if (cluster == -1)
                worklist.add(id);
            else {
                initialClusters.add(cluster);
                unactivelist.add(id);
            }

        }
    }

}
