package com.braz.prod.DankMemeStickers;

import android.os.CountDownTimer;
import android.os.Looper;

import com.braz.prod.DankMemeStickers.Recorder.RecorderCallback;

import java.util.TimerTask;
import java.util.logging.Handler;

public class RecordingTimer {
    public void scheduleTimer(RecorderCallback callback){

        new CountDownTimer(12000, 3000) {

            public void onTick(long millisUntilFinished) { }

            public void onFinish() {
                callback.onRecordingTimeEnded();
            }

        }.start();
    }

}
