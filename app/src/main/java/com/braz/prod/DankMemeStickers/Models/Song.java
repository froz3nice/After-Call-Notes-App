package com.braz.prod.DankMemeStickers.Models;

public class Song {
    private Integer res;
    private String name;

    public Integer getRes() {
        return res;
    }

    public void setRes(Integer res) {
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Song() {
    }

    public Song(Integer res, String name) {
        this.res = res;
        this.name = name;
    }
}
