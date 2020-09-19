package org.music.clustering;

import java.util.ArrayList;

public class AbstractOverlap extends AbstractCorrelationClustering {

    public AbstractOverlap() {
    }

    ;
	
	
	/*
	public static void process(){
		para = 1;
		
		double minobj = Double.valueOf(Math.pow(records.size(), 2));
		for(int count = 0; count<para; count++){
			
			clusterinvertedList = new HashMap();
			clusters = new HashMap();
			
			System.out.println();
			ArrayList keys = new ArrayList(blockinvertedList.keySet());
			
			Collections.shuffle(keys);
			
			for(int i=0; i<keys.size(); i++){
				int id1 = (Integer) keys.get(i);
				ArrayList blocklist1	= blockinvertedList.get(id1);
				
				System.out.println(i+"----- proceed record \t"+id1+"\t"+blocklist1);
				
				int cluster1 = -1; 
				if(clusterinvertedList.containsKey(id1))
					cluster1 = clusterinvertedList.get(id1);
				if(cluster1==-1){
					if(i>0){
						
						cluster1 = getOptCluster(id1, blocklist1);
					}
				}
				
				updateClusters(cluster1, id1);
			}	
			
			double objvalue = getOBJFunction();
			System.out.println("++++++ object value \t"+objvalue);
			if(objvalue<minobj){
				minobj = objvalue;
				bestinvertedList = clusterinvertedList;
				bestclusters = clusters;
			}
			
			Evaluation evaluation = new Evaluation();
			//System.out.println("-----------VOTE correlation cluster quality -------------");
			evaluation.ClusterEvaluation(records, clusterinvertedList);
			
		}
		
		Evaluation evaluation = new Evaluation();
		System.out.println("-----------final VOTE correlation cluster quality -------------");
		evaluation.ClusterEvaluation(records, bestinvertedList);
		
	}*/

    public int getOptCluster(int id, ArrayList blocklist, ArrayList keys) {
        // TODO Auto-generated method stub
        System.out.println("start overlap");
        return -1;
    }

}
