package com.braz.prod.DankMemeStickers.Activities.ActivityInterfaces;

public interface ActivityCallback {
    void hideListView();

    void onStickerAdded();
    void onStickerRemoved();

    void playSound();
}
