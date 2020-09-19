package org.music.data;

import java.util.ArrayList;

public class ArtistData {

    // id, release, genres, style, country, date, name, cluster_id
    public int id;
    public int orginial_id;
    public String name;
    public String real_name;
    public String profile;
    public String urls;
    public String alias;
    public String name_variation;
    public String source;
    public ArrayList tracks;
    public int cluster_id;
    public String release;
    public ArrayList genres;
    public ArrayList style;
    public String country;
    public String date;


    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    public int getOrginial_id() {
        return orginial_id;
    }

    public void setOrginial_id(int orginial_id) {
        this.orginial_id = orginial_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList releases;


    public ArtistData() {

    }

    public ArtistData(int id, String name, String real_name, String profile, String urls, String alias, String name_variation) {
        this.id = id;
        this.name = name;
        this.real_name = real_name;
        this.profile = profile;
        this.urls = urls;
        this.alias = alias;
        this.name_variation = name_variation;
    }

    public ArtistData(int id, String name, String real_name, String profile, String urls, String alias, String name_variation, ArrayList tracks) {
        this.id = id;
        this.name = name;
        this.real_name = real_name;
        this.profile = profile;
        this.urls = urls;
        this.alias = alias;
        this.name_variation = name_variation;
        this.tracks = tracks;
    }

    //id, cluster_id, name, profile, real_name, tracks, source
    public ArtistData(int id, int cluster_id, String name, String profile, String real_name, ArrayList tracks, String source) {
        this.id = id;
        this.cluster_id = cluster_id;
        this.name = name;
        this.real_name = real_name;
        this.profile = profile;
        this.tracks = tracks;
        this.source = source;
    }

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

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName_variation() {
        return name_variation;
    }

    public void setName_variation(String name_variation) {
        this.name_variation = name_variation;
    }

    public ArrayList getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList tracks) {
        this.tracks = tracks;
    }

    public ArrayList getReleases() {
        return releases;
    }

    public void setReleases(ArrayList releases) {
        this.releases = releases;
    }


}
