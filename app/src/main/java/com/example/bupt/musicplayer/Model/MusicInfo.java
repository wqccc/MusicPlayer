package com.example.bupt.musicplayer.Model;

public class MusicInfo {
    private long id;
    private String title;
    private String artist;
    private long duration;
    private long size;
    private String url;
    private long album;
    private int isMusic;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getAlbum(){return album;}
    public void setAlbum(long album){this.album=album;}
    public int getIsMusic() {
        return isMusic;
    }
    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }
}

