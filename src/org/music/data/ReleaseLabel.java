package org.music.data;

import java.util.ArrayList;

public class ReleaseLabel implements Comparable<ReleaseLabel> {

    public int id;
    public String release;
    public String genres;
    public String styles;
    public ArrayList genreslist;
    public ArrayList styleslist;
    public String country;
    public int date;
    public ArrayList artist;
    public ArrayList extra_artist;
    public String strartist;
    public String strextrartist;
    public String label;
    public String catno;
    public String format;
    public String qty;
    public String format_description;
    public int cluster_id;
    public int partition_id;
    public int new_date;
    public String new_country;
    public String new_label;
    public String new_release;
    public String new_catno;
    public String new_format;
    public int ground_truth;
    public int blockID;
    public Integer orderID;

    @Override
    public int compareTo(ReleaseLabel otherEntity) {
        return orderID.compareTo(otherEntity.orderID);
    }

    public int getBlockID() {
        return blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getGround_truth() {
        return ground_truth;
    }

    public void setGround_truth(int ground_truth) {
        this.ground_truth = ground_truth;
    }

    public int getNew_date() {
        return new_date;
    }

    public void setNew_date(int new_date) {
        this.new_date = new_date;
    }

    public String getNew_country() {
        return new_country;
    }

    public void setNew_country(String new_country) {
        this.new_country = new_country;
    }

    public String getNew_label() {
        return new_label;
    }

    public void setNew_label(String new_label) {
        this.new_label = new_label;
    }

    public String getNew_release() {
        return new_release;
    }

    public void setNew_release(String new_release) {
        this.new_release = new_release;
    }

    public String getNew_catno() {
        return new_catno;
    }

    public void setNew_catno(String new_catno) {
        this.new_catno = new_catno;
    }

    public String getNew_format() {
        return new_format;
    }

    public void setNew_format(String new_format) {
        this.new_format = new_format;
    }

    public int getPartition_id() {
        return partition_id;
    }

    public void setPartition_id(int partition_id) {
        this.partition_id = partition_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList getGenreslist() {
        return genreslist;
    }

    public void setGenreslist(ArrayList genreslist) {
        this.genreslist = genreslist;
    }

    public ArrayList getStyleslist() {
        return styleslist;
    }

    public void setStyleslist(ArrayList styleslist) {
        this.styleslist = styleslist;
    }

    public String getStrartist() {
        return strartist;
    }

    public void setStrartist(String strartist) {
        this.strartist = strartist;
    }

    public String getStrextrartist() {
        return strextrartist;
    }

    public void setStrextrartist(String strextrartist) {
        this.strextrartist = strextrartist;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCatno() {
        return catno;
    }

    public void setCatno(String catno) {
        this.catno = catno;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getFormat_description() {
        return format_description;
    }

    public void setFormat_description(String format_description) {
        this.format_description = format_description;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    public ArrayList getArtist() {
        return artist;
    }

    public void setArtist(ArrayList artist) {
        this.artist = artist;
    }

    public ArrayList getExtra_artist() {
        return extra_artist;
    }

    public void setExtra_artist(ArrayList extra_artist) {
        this.extra_artist = extra_artist;
    }


}
