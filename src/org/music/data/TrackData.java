package org.music.data;

public class TrackData {

    public int id;
    public int releaseId;
    public String title;
    public String duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(int releaseId) {
        this.releaseId = releaseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public TrackData(int id, int releaseId, String title, String duration) {
        this.id = id;
        this.releaseId = releaseId;
        this.title = title;
        this.duration = duration;
    }

}
