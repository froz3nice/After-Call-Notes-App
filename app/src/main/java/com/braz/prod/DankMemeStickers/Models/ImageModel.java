package com.braz.prod.DankMemeStickers.Models;

public class ImageModel {
    public enum Type{
        gif,image
    }
    Integer res;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ImageModel(Integer res, Type type, String ownerId) {
        this.res = res;
        this.ownerId = ownerId;
        this.type = type;
    }

    String ownerId;
    Type type;

    public ImageModel(){}

    public Integer getRes() {
        return res;
    }

    public void setRes(Integer res) {
        this.res = res;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
