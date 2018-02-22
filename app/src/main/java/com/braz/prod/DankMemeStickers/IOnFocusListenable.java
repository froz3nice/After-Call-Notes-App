package com.braz.prod.DankMemeStickers;

/**
 * Created by Juozas on 2018.02.19.
 */

public interface IOnFocusListenable {
    void onWindowFocusChanged(boolean hasFocus);
    void pausePlayer();
    void setPlayButtonVisibility();
}
