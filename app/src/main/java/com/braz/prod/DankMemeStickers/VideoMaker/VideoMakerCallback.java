package com.braz.prod.DankMemeStickers.VideoMaker;

public interface VideoMakerCallback {
    void onSuccess(String file);
    void onError();
    void videoDuration(Integer duration);
}
