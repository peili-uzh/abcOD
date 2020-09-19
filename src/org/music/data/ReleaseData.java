package org.music.data;

import java.util.ArrayList;

public class ReleaseData {

    public int id;
    public String title;
    public String genres;
    public String country;
    public String date;
    public String notes;
    public String styles;


    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStyles() {
        return styles;
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public ArrayList artists;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList getArtists() {
        return artists;
    }

    public void setArtists(ArrayList artists) {
        this.artists = artists;
    }


    public ReleaseData() {

    }

    public ReleaseData(int id, String title, String genres, String styles, String country, String date, String notes) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.styles = styles;
        this.country = country;
        this.date = date;
        this.notes = notes;
    }


}
