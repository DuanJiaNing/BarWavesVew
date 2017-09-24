package com.duan.barwavesvew.controller;

/**
 * Created by DuanJiaNing on 2017/4/1.
 */

public class SongInfo {

    private String name;

    private String songPath;

    private String artPicPath;

    public void setName(String name) {
        this.name = name;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public void setArtPicPath(String artPicPath) {
        this.artPicPath = artPicPath;
    }

    public String getName() {
        return name;
    }

    public String getSongPath() {
        return songPath;
    }

    public String getArtPicPath() {
        return artPicPath;
    }
}
