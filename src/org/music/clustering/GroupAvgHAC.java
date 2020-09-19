package org.music.clustering;

import org.music.data.ArtistData;

import java.util.HashSet;
import java.util.Iterator;

public class GroupAvgHAC extends HAC {

    public GroupAvgHAC() {
    }

    ;

    protected double ClusterSim(int cluster1, int cluster2) {
        // TODO Auto-generated method stub
        double sum = 0;
        int count = 0;
        HashSet set1 = new HashSet(clusters.get(cluster1).getEntities());
        HashSet set2 = new HashSet(clusters.get(cluster2).getEntities());

        Iterator it1 = set1.iterator();


        while (it1.hasNext()) {
            int id1 = (Integer) it1.next();
            ArtistData a1 = (ArtistData) records.get(id1);
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                int id2 = (Integer) it2.next();
                ArtistData a2 = (ArtistData) records.get(id2);
                double localscore = sim.recordSimilarity(a1, a2);
                sum += localscore;
                count += 1;

            }
        }

        double score = sum / Double.valueOf(count);
        return score;
    }

}
