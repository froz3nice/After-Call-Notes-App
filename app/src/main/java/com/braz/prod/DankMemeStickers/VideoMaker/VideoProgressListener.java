package com.braz.prod.DankMemeStickers.VideoMaker;

public interface VideoProgressListener {
    void onFinished(String file);
    void onProgress(String progess);
    void onFailed();
}
