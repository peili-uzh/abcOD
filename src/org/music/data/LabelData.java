package org.music.data;

import java.util.ArrayList;

public class LabelData {

    public int id;
    public String name;
    public String release;
    public String country;
    public String date;
    public String artist;
    public String styles;
    public int cluster_id;
    public ArrayList genres;
    public ArrayList style;
    public String profile;
    public String sublabels;
    public String urls;
    public String contact;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSublabels() {
        return sublabels;
    }

    public void setSublabels(String sublabels) {
        this.sublabels = sublabels;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    public ArrayList getGenres() {
        return genres;
    }

    public void setGenres(ArrayList genres) {
        this.genres = genres;
    }

    public ArrayList getStyle() {
        return style;
    }

    public void setStyle(ArrayList style) {
        this.style = style;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
