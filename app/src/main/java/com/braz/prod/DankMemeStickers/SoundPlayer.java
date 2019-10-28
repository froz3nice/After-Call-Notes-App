package com.braz.prod.DankMemeStickers;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
    private MediaPlayer mPlayer;
    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
    }

    public void resumePlayer() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.seekTo(mediaPlayerLength);
            mPlayer.start();
        }
    }

    public void playSound(int thugLifeSound) {
        stopPlayer();
        mPlayer = MediaPlayer.create(context, thugLifeSound);
        mPlayer.start();
    }

    public void stopPlayer(){
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    Integer mediaPlayerLength = 0;

    public void pausePlayer() {
        if (mPlayer != null) {
            mPlayer.pause();
            mediaPlayerLength = mPlayer.getCurrentPosition();
        }
    }
}
