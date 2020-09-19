package org.music.block;

import org.music.clustering.*;
import org.music.data.Block;
import org.music.evaluation.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockTest {

    private static Evaluation evaluation;


    public static void main(String[] args) throws Exception {

        String sql = "select id, cluster_id, name, profile, real_name, tracks, source from artist_merged where id in (776820, 917481) and name<>'' and profile<>'' and real_name<>'' and tracks<>'' " +
                "order by name limit 100";
        String sql1 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where name<>'' and  real_name<>'' and cluster_id in (24, 4872, 5597) and id not in (629599) order by cluster_id";

        String sql2 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where  name like 'john %' and id not in (975373) and  real_name <>''  order by name";

        String sql3 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where  name <> '' and  real_name <>'' and cluster_id<310 order by real_name,id limit 1000";

        //String sql5 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where  id in (878246, 878247, 878245, 878250, 878248, 878249, 742024, 878244, 878251, 1000513, 1000515, 1000517, 1000518, 885422, 885423) order by real_name limit 15";

        String sql4 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where  (name like 'michael%' or  real_name like 'michael%') and name <> '' and  real_name <>''  order by name, id";

        String sql6 = "select id, cluster_id, name, profile, real_name, tracks, source , original_id from artist_merged where  (name like 'john%') and  real_name <>''  order by cluster_id";

        //System.out.println(sql);

        ArrayList records = new ArrayList();
        //getRecords(records, sql3);

        String blockKey = "real_name";


        double min = 0.9;//5;//0.1;
        double max = min + 0.1;
        double th1 = 0.9;
        double th2 = max;//th1;//min;//

        ERProcessor erProcessor = new ERProcessor(sql4, blockKey, th1);
        records = erProcessor.getRecords();
        System.out.println(records.size());


        while (min <= th1) {
            while (max <= th2) {
                System.out.println("\r\n____________ \t TH1= " + min + ", TH2=" + max);

                HashMap<Integer, Block> blocks = new HashMap();
                HashMap<Integer, ArrayList> invertedList = new HashMap();
				
				/*
				 * baseline: add all records togehter
				 
				Baseline baseline = new Baseline(records);
				baseline.process();
				blocks = baseline.getBlocks();
				invertedList = baseline.getInvertedList();
				*/

                /*
                 * canopy
                 */
                Canopy canopy = new Canopy();
                canopy.Process(records, blockKey, min, max);
                blocks = canopy.getBlocks();
                invertedList = canopy.getInvertedList();
				 	 
				
				/*
				 *  sliding window
				
				//System.out.println("blocking method: \t slidingwindow");
				SlidingWindow slide;
				slide = new SlidingWindow(records, blocks, invertedList, blockKey, min);
				 */
				
				/*
				 * standard blocking approach 
				 *
				
				StandardBlocking standBlock;
				
				HashMap<Integer, String> invertedList = new HashMap();
				
				standBlock = new StandardBlocking(records, blocks, invertedList, blockKey, min);
				 */

                /*
                 * CLUSTERING
                 */
                Partitioning part = new Partitioning(records, blocks);
                //part.process();

                Center center = new Center(records, blocks);
                //center.process();

                Merge merge = new Merge(records, blocks);
                //merge.process();

                HACProcesser hacProcessor = new HACProcesser(records, blocks);
                SingleLinkHAC single = new SingleLinkHAC();
                GroupAvgHAC group = new GroupAvgHAC();
                CompleteLinkHAC complete = new CompleteLinkHAC();
                hacProcessor.process(complete);


                BestCorrelationClustering best = new BestCorrelationClustering(records, blocks);
                //best.process();

                FirstCorrelationClustering first = new FirstCorrelationClustering(records, blocks);
                //first.process();

                VoteCorrelationClustering vote = new VoteCorrelationClustering(records, blocks);
                //vote.process();

                PivotCorrelationClustering pivot = new PivotCorrelationClustering(records, blocks);
                //pivot.process();


                //OverlapFirst overfirst = new OverlapFirst(records, blocks, invertedList);
                //overfirst.process();

                //OverlapBest overbest = new OverlapBest(records, blocks, invertedList);
                //overbest.process();
				
				/*
				 * 
					
				ClusteringTest test = new ClusteringTest(records, blocks, invertedList);
				OverlapVote overvote = new OverlapVote();
				OverlapFirst overfirst = new OverlapFirst();
				OverlapBest overbest = new OverlapBest();
				//test.process(overvote);
				
				OverlapPivot overpivot = new OverlapPivot(records, blocks, invertedList);
				//overpivot.process();
				 */

                //OverlapVote overvote = new OverlapVote(records, blocks, invertedList);
                //proceedCorrelationClustering(overvote);
                //overvote.process();

                max += 1;
            }
            min += 1;
            max = min + 0.1;
            th2 = max;

        }

        Canopy canopy = new Canopy();
        //erProcessor.process(canopy);//(records, blockKey, 1, canopy);

        //Evaluation.BaseEvaluate(records);
		
		/*
		 * baseline: add all records togehter
		 
		Baseline baseline = new Baseline(records);
		baseline.process();
		blocks = baseline.getBlocks();
		HashMap<Integer, ArrayList> invertedList = new HashMap();
		invertedList = baseline.getInvertedList();
		*/
		
		/*
		 * standard blocking approach 
		 * 
		
		StandardBlocking standBlock;
		
		HashMap<Integer, String> invertedList = new HashMap();
		
		standBlock = new StandardBlocking(records, blocks, invertedList, blockKey);
		*/
		
		/*
		 *  sliding window
		
		//System.out.println("blocking method: \t slidingwindow");
		HashMap<Integer, ArrayList> invertedList = new HashMap();
		SlidingWindow slide;
		slide = new SlidingWindow(records, blocks, invertedList, blockKey);
		 */
		
		/*
		 * canopy: false positive can be caused by non-transitive clusters (e.g., sim(a, b)=.8, sim(a,c)=.8, sim(b,c)=.4, b and c are false positive)
		 
		HashMap<Integer, ArrayList> invertedList = new HashMap();
		Canopy canopy = new Canopy(records, blockKey);
		canopy.Process();
		blocks = canopy.getBlocks();
		invertedList = canopy.getInvertedList();
		 */
		 
		
		/*
		 * bigramindexing causes false negative when duplicate strings are of different lengths
		 
		
		BigramIndexing bigramindexing = new BigramIndexing(records, blockKey);
		//bigramindexing.process();
		HashMap<Integer, ArrayList> invertedList = new HashMap();
		blocks = bigramindexing.getBlocks();
		invertedList = bigramindexing.getInvertedList();
		*/
		
		/*
		String tempb = "";
		for (Map.Entry<Integer, Block> item : blocks.entrySet()){
			int id = item.getKey();
			Block block = item.getValue();
			tempb += block.getEntities().toString();
			//System.out.println(id+"\t has entities: \t"+block.getEntities());
		}
		//System.out.println(tempb);
		
		for (Map.Entry<Integer, ArrayList> list : invertedList.entrySet()){
			int id = list.getKey();
			ArrayList set = list.getValue();
			//if(id==11){
				ArtistData artist = (ArtistData) records.get(id);
				System.out.println(id+"\t"+artist.getId()+"\t"+artist.getReal_name()+"\t is in blocks: \t"+set);
			//}
		}*/
		
		/*
		 * clustering
		 
		Partitioning part = new Partitioning(records, blocks);
		//part.process();
		
		Center center = new Center(records, blocks);
		//center.process();
		
		Merge merge = new Merge(records, blocks);
		//merge.process();
		
		HACProcesser hacProcessor = new HACProcesser(records, blocks);
		SingleLinkHAC single = new SingleLinkHAC();
		GroupAvgHAC group = new GroupAvgHAC();
		CompleteLinkHAC complete = new CompleteLinkHAC();
		//hacProcessor.process(single);
		
		
		BestCorrelationClustering best = new BestCorrelationClustering(records, blocks);
		//best.process();
		
		FirstCorrelationClustering first = new FirstCorrelationClustering(records, blocks);
		//first.process();
		
		VoteCorrelationClustering vote = new VoteCorrelationClustering(records, blocks);
		//vote.process();
		
		PivotCorrelationClustering pivot = new PivotCorrelationClustering(records, blocks);
		//pivot.process();
		
		
		//OverlapFirst overfirst = new OverlapFirst(records, blocks, invertedList);
		//overfirst.process();
		
		//OverlapBest overbest = new OverlapBest(records, blocks, invertedList);
		//overbest.process();
		*/
		/*
		 * 
		 	
		ClusteringTest test = new ClusteringTest(records, blocks, invertedList);
		OverlapVote overvote = new OverlapVote();
		OverlapFirst overfirst = new OverlapFirst();
		OverlapBest overbest = new OverlapBest();
		//test.process(overvote);
		
		OverlapPivot overpivot = new OverlapPivot(records, blocks, invertedList);
		//overpivot.process();
			
		
		//OverlapVote overvote = new OverlapVote(records, blocks, invertedList);
		//proceedCorrelationClustering(overvote);
		//overvote.process();
		*/
		/*
		HashMap<Integer, Integer> clusterinvertedList = new HashMap();
		clusterinvertedList = test.getBestinvertedList();
		
		HashMap<Integer, Cluster> clusters = new HashMap();
		clusters = test.getBestclusters();
		
		String tempc = "";
		for (Map.Entry<Integer, Cluster> item : clusters.entrySet()){
			Integer id = item.getKey();
			Cluster cluster = item.getValue();
			tempc += cluster.getEntities().toString()+", ";
			//System.out.println(id+"\t"+cluster.getCluster_id()+"\t has entities: \t"+cluster.getEntities());
		}
		//System.out.println(tempc);
		
		for (Map.Entry<Integer, Integer> list : clusterinvertedList.entrySet()){
			int id = list.getKey();
			int set = list.getValue();
			//if(id==11){
				ArtistData artist = (ArtistData) records.get(id);
				//System.out.println(id+"\t"+artist.getId()+"\t"+artist.getReal_name()+"\t is in cluster: \t"+set);
			//}
		}
		*/
    }


}
