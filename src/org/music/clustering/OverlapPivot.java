package org.music.clustering;

import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OverlapPivot extends AbstractCorrelationClustering {

    public OverlapPivot(ArrayList records, HashMap<Integer, Block> blocks, HashMap<Integer, ArrayList> blockinvertedList) {
        super(records, blocks, blockinvertedList);
        // TODO Auto-generated constructor stub
        this.blockinvertedList = blockinvertedList;
    }

    private static ArrayList unactivelist;

    public void process() {

        para = 5;
        double minobj = Double.valueOf(Math.pow(records.size(), 2));
        for (int count = 0; count < para; count++) {

            clusterinvertedList = new HashMap();
            clusters = new HashMap();

            System.out.println();
            ArrayList keys = new ArrayList(blockinvertedList.keySet());

            Collections.shuffle(keys);

            unactivelist = new ArrayList();

            for (int i = 0; i < keys.size(); i++) {
                int id1 = (Integer) keys.get(i);
                ArrayList blocklist = blockinvertedList.get(id1);
                //System.out.println(i+"----- proceed record \t"+id1+"\t"+blocklist);
                if (!unactivelist.contains(id1)) {

                    addNewCluster(id1);
                    unactivelist.add(id1);
                    for (int j = 0; j < blocklist.size(); j++) {
                        int blockid = (Integer) blocklist.get(j);
                        ArrayList entities = blocks.get(blockid).getEntities();
                        //System.out.println("\t ----- procedd block \t"+blockid+"\t"+entities);
                        for (int n = 0; n < entities.size(); n++) {
                            int id2 = (Integer) entities.get(n);
                            if (!unactivelist.contains(id2)) {
                                //System.out.println("\t\t ----- compare w record \t"+id2);
                                double cost = getABSWeight(id1, id2);
                                if (cost > 0) {
                                    unactivelist.add(id2);
                                    updateSingleCluster(id2, id1);
                                }
                            }
                        }
                    }
                }
            }

            double objvalue = getOBJFunction();
            System.out.println(count + "\t ++++++ PIPVOT object value \t" + objvalue);
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
        System.out.println("-----------final PIVOT correlation cluster quality -------------");
        evaluation.ClusterEvaluation(records, bestinvertedList);
    }

}
