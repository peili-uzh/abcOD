package org.music.data;

public class TrackArtistData {

    public int trackId;
    public int artistId;
    public String role;
    public String name;

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrackArtistData(int trackId, int artistId, String role, String name) {
        this.trackId = trackId;
        this.artistId = artistId;
        this.role = role;
        this.name = name;
    }

}
