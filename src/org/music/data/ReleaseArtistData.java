package org.music.data;

public class ReleaseArtistData {

    public int id;
    public String name;
    public String role;
    public int releaseId;

    public ReleaseArtistData() {

    }

    public ReleaseArtistData(int id, int releaseId, String name, String role) {
        this.id = id;
        this.releaseId = releaseId;
        this.name = name;
        this.role = role;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public int getReleaseId() {
        return releaseId;
    }


    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
    }


    public ReleaseArtistData(int id, String name) {
        this.id = id;
        this.name = name;
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


}
