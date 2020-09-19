package org.music.data;

import java.util.ArrayList;

public class Cluster {

    protected int cluster_id;
    protected ArrayList entities;

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    public ArrayList getEntities() {
        return entities;
    }

    public void setEntities(ArrayList entities) {
        this.entities = entities;
    }

    public Cluster(int cluster_id, ArrayList entities) {
        this.cluster_id = cluster_id;
        this.entities = entities;
    }

    public Cluster() {
    }

    ;
}
