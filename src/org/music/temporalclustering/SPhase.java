package org.music.temporalclustering;

import org.music.data.ArtistData;
import org.music.data.LabelData;
import org.music.data.ReleaseLabel;
import org.music.evaluation.Evaluation;
import org.music.similarity.RecordSimilarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SPhase extends Cluster {

    public SPhase() {
    }

    ;

    public void process(ArrayList artists, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist, RecordSimilarity recordSim) {

        this.artists = artists;
        this.blocks = blocks;
        this.blockinvertedlist = blockinvertedlist;
        this.recordSim = recordSim;
        this.th = 0.4;

        clusters = new LinkedHashMap<Integer, ArrayList>();
        clusterlist = new HashMap<Integer, Integer>();
        clusterinvertedlist = new HashMap<Integer, ArrayList>();
        clustersign = new HashMap<Integer, ArrayList>();

        int count = 0;
        int invalidcount = 0;

        System.out.println("S-Phase:");


        for (int i = 0; i < artists.size(); i++) {
            ArtistData artist1 = (ArtistData) artists.get(i);
            //int id1 = artist1.getId();
            String name1 = artist1.getName();
            String yr1 = artist1.getDate();
            int clusterid1 = artist1.getCluster_id();
            ArrayList blocks1 = blockinvertedlist.get(i);
            //System.out.println(i+"\t"+artist1.getId()+"\t"+artist1.getName()+"\t"+blocks1);
            //System.out.println("****\t"+i+"\t"+name1+"\t"+yr1+"\t"+clusterid1);
            //System.out.println("*****"+i+"\t"+artist1.getId()+"\t"+name1+"\t"+artist1.getRelease()+"\t"+artist1.getGenres()+"\t"+artist1.getStyle()+"\t"+artist1.getCountry()+"\t"+artist1.getDate()+"\t"+artist1.getCluster_id());

            /*
             * if first, create new cluster
             */
            if (clusters.isEmpty()) {
                updateClusters(i, i);
                clusterlist.put(i, i);
                updateClusterInvertedList(i, i);
                //System.out.println("create new cluster \t"+i+"\t for record \t"+i);
            } else {
                boolean unmatched = true;
                double maxscore = 0;
                int maxcluster = 0;

                for (Map.Entry<Integer, ArrayList> e : clusters.entrySet()) {
                    ArrayList records = e.getValue();
                    int clusterid = e.getKey();
                    ArrayList blocks2 = clusterinvertedlist.get(clusterid);
                    /*
                     * check if share any blockid
                     */
                    boolean overlap = checkOverlap(blocks1, blocks2);
                    if (overlap) {
                        double score = recordClusterSimilarity(i, records, "artist");
                        //System.out.println("\t compare w cluster \t"+clusterid+"\t"+score);

                        count += records.size();

                        if (score > maxscore) {
                            maxscore = score;
                            maxcluster = clusterid;
                        }

                        if (score < th)
                            invalidcount += records.size();
                    }
                }

                /*
                 * add record int maxcluster if maxscore > th
                 */
                if (maxscore >= th) {
                    unmatched = false;
                    //System.out.println("\t add "+i+" into cluster "+maxcluster);
                    updateClusters(i, maxcluster);
                    updateClusterInvertedList(i, maxcluster);
                    clusterlist.put(i, maxcluster);
                }

                if (unmatched) {
                    updateClusters(i, i);
                    clusterlist.put(i, i);
                    updateClusterInvertedList(i, i);
                    //System.out.println("\t create new cluster \t"+i+"\t for record \t"+i);
                }
            }
        }

        System.out.println("++++ # comparisons \t" + count);
        System.out.println("++++ # un-necessary comparisons \t" + invalidcount);

        //setCluster(clusterlist);

        Evaluation evaluation = new Evaluation();
        evaluation.ClusterEvaluation(artists, clusterlist);
    }

    public void processEntity(String entityType, ArrayList dataset, LinkedHashMap<Integer, ArrayList> blocks, HashMap<Integer, ArrayList> blockinvertedlist, RecordSimilarity recordSim) {

        if (entityType.equalsIgnoreCase("label"))
            this.labels = dataset;
        if (entityType.equalsIgnoreCase("releaselabel"))
            this.relabels = dataset;
        this.blocks = blocks;
        this.blockinvertedlist = blockinvertedlist;
        this.recordSim = recordSim;
        this.th = 0.6;

        clusters = new LinkedHashMap<Integer, ArrayList>();
        clusterlist = new HashMap<Integer, Integer>();
        clusterinvertedlist = new HashMap<Integer, ArrayList>();
        clustersign = new HashMap<Integer, ArrayList>();

        int count = 0;
        int invalidcount = 0;

        System.out.println("S-Phase:");


        for (int i = 0; i < dataset.size(); i++) {
            if (entityType.equals("label")) {
                LabelData label1 = (LabelData) dataset.get(i);
                String name1 = label1.getName();
                String yr1 = label1.getDate();
                int clusterid1 = label1.getCluster_id();
                System.out.println(i + "\t" + label1.getId() + "\t" + label1.getName() + "\t");

            } else if (entityType.equalsIgnoreCase("releaselabel")) {
                ReleaseLabel relabel1 = (ReleaseLabel) dataset.get(i);
                String label1 = relabel1.getLabel();
                int yr1 = relabel1.getDate();
                int cluster_id1 = relabel1.getCluster_id();
                System.out.println(i + "\t" + relabel1.getId() + "\t" + label1 + "\t" + cluster_id1);

            }
            //int id1 = artist1.getId();
            ArrayList blocks1 = blockinvertedlist.get(i);

            //System.out.println("****\t"+i+"\t"+name1+"\t"+yr1+"\t"+clusterid1);
            //System.out.println("*****"+i+"\t"+artist1.getId()+"\t"+name1+"\t"+artist1.getRelease()+"\t"+artist1.getGenres()+"\t"+artist1.getStyle()+"\t"+artist1.getCountry()+"\t"+artist1.getDate()+"\t"+artist1.getCluster_id());

            /*
             * if first, create new cluster
             */
            if (clusters.isEmpty()) {
                updateClusters(i, i);
                clusterlist.put(i, i);
                updateClusterInvertedList(i, i);
                //System.out.println("create new cluster \t"+i+"\t for record \t"+i);
            } else {
                boolean unmatched = true;
                double maxscore = 0;
                int maxcluster = 0;

                for (Map.Entry<Integer, ArrayList> e : clusters.entrySet()) {
                    ArrayList records = e.getValue();
                    int clusterid = e.getKey();
                    ArrayList blocks2 = clusterinvertedlist.get(clusterid);
                    /*
                     * check if share any blockid
                     */
                    boolean overlap = checkOverlap(blocks1, blocks2);
                    if (overlap) {
                        double score = recordClusterSimilarity(i, records, entityType);
                        //System.out.println("\t compare w cluster \t"+clusterid+"\t"+score);

                        count += records.size();

                        if (score > maxscore) {
                            maxscore = score;
                            maxcluster = clusterid;
                        }

                        if (score < th)
                            invalidcount += records.size();
                    }
                }

                /*
                 * add record int maxcluster if maxscore > th
                 */
                if (maxscore >= th) {
                    unmatched = false;
                    //System.out.println("\t add "+i+" into cluster "+maxcluster);
                    updateClusters(i, maxcluster);
                    updateClusterInvertedList(i, maxcluster);
                    clusterlist.put(i, maxcluster);
                }

                if (unmatched) {
                    updateClusters(i, i);
                    clusterlist.put(i, i);
                    updateClusterInvertedList(i, i);
                    //System.out.println("\t create new cluster \t"+i+"\t for record \t"+i);
                }
            }
        }

        System.out.println("++++ # comparisons \t" + count);
        System.out.println("++++ # un-necessary comparisons \t" + invalidcount);

        //setCluster(clusterlist);

        Evaluation evaluation = new Evaluation();

        evaluation.EntityEvaluation(entityType, dataset, clusterlist);
    }

    private double recordClusterSimilarity(int id1, ArrayList records, String entityType) {
        // TODO Auto-generated method stub
        double sumscore = 0;
        double avgscore = 0;

        if (entityType.equalsIgnoreCase("artist")) {
            ArtistData artist1 = (ArtistData) artists.get(id1);
            for (int i = 0; i < records.size(); i++) {
                int id2 = (Integer) records.get(i);
                ArtistData artist2 = (ArtistData) artists.get(id2);
                //System.out.println("\t\t compare "+id1+" with "+id2);
                double score = recordSim.agreeSimilarity(artist1, artist2);
                //System.out.println("\t\t record sim \t"+id1+"\t"+id2+"\t"+score+"\t"+artist1.getCluster_id()+"\t"+artist2.getCluster_id());
                sumscore += score;
            }
        } else if (entityType.equalsIgnoreCase("label")) {
            LabelData label1 = (LabelData) labels.get(id1);
            for (int i = 0; i < records.size(); i++) {
                int id2 = (Integer) records.get(i);
                LabelData label2 = (LabelData) labels.get(id2);
                //System.out.println("\t\t compare "+id1+" with "+id2);
                double score = recordSim.labelAgreeSimilarity(label1, label2);
                //score = recordSim.labelTimelessSimilarity(label1, label2);
                //System.out.println("\t\t record sim \t"+id1+"\t"+id2+"\t"+score+"\t"+label1.getCluster_id()+"\t"+label2.getCluster_id());
                sumscore += score;
            }
        } else if (entityType.equalsIgnoreCase("releaselabel")) {
            ReleaseLabel relabel1 = (ReleaseLabel) relabels.get(id1);
            for (int i = 0; i < records.size(); i++) {
                int id2 = (Integer) records.get(i);
                ReleaseLabel relabel2 = (ReleaseLabel) relabels.get(id2);
                double score = recordSim.relabelAgreeSimilarity(relabel1, relabel2);
                System.out.println("\t\t record sim \t" + id1 + "\t" + id2 + "\t" + score + "\t" + relabel1.getCluster_id() + "\t" + relabel2.getCluster_id());
                sumscore += score;
            }
        }


        avgscore = sumscore / Double.valueOf(records.size());

        return avgscore;
    }


}
