package com.braz.prod.DankMemeStickers.Recorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import com.braz.prod.DankMemeStickers.RecordingTimer;
import com.braz.prod.DankMemeStickers.util.Utils;

import java.io.IOException;

import static com.braz.prod.DankMemeStickers.util.Utils.getDensity;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenHeight;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenWidth;

public class VideoRecorder {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 69;
    private final RecordingTimer timer;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private boolean isRecording = false;
    private String fileName = "";
    private Integer width = 0;
    private Integer height = 0;
    public MediaProjection getmMediaProjection() {
        return mMediaProjection;
    }

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    RecorderCallback callback;

    public VideoRecorder(RecorderCallback callback,
                         MediaProjectionManager manager,
                         WindowManager windowManager) {
        this.callback = callback;
        // Record to the external cache directory for visibility
        this.mProjectionManager = manager;
        mScreenDensity = getDensity(windowManager);
        mMediaRecorder = new MediaRecorder();
        timer = new RecordingTimer();
        width = getScreenWidth(windowManager);
        height = getScreenHeight(windowManager);//metrics.heightPixels;
    }


    public void shareScreen(RecorderCallback callback) {
        if (mMediaProjection == null) {
            callback.startScreenCaptureIntent();
            return;
        }

        try {
            mVirtualDisplay = createVirtualDisplay();
            mMediaRecorder.start();
            Log.v(TAG, "Recording started");
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }


    private void stopScreenSharing() {
        if (mVirtualDisplay == null) return;

        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();

    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                width, height, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    public String getFileName() {
        return fileName;
    }

    public void initRecorder(Context context) {
        try {
            fileName = Utils.getPath(context) + "/" + Utils.getTimeStamp() + ".mp4";
            Log.d("new file name", fileName);
            mMediaRecorder = new MediaRecorder();
            // mMediaRecorder.setAudioSource(MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(fileName);
            mMediaRecorder.setVideoSize(width, height);
            mMediaRecorder.setVideoEncodingBitRate(2000000);
            mMediaRecorder.setVideoFrameRate(35);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //  mMediaRecorder.setAudioEncodingBitRate(128000);
            // mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setOrientationHint(0);
            mMediaRecorder.prepare();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(Context context) {
        if (mMediaRecorder == null || mMediaProjection == null) return;
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            Log.v(TAG, "Recording Stopped");
            mMediaProjection = null;
            stopScreenSharing();
        } catch (RuntimeException e) {
            e.printStackTrace();
            mMediaProjection = null;
            mMediaRecorder.reset();
            stopScreenSharing();
        }
        callback.onStoppedRecording(fileName);
    }

    public void startVideo(Intent data, int resultCode) {
        new Handler().postDelayed(() -> {

            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            try {
                mVirtualDisplay = createVirtualDisplay();
                mMediaRecorder.start();
                timer.scheduleTimer(callback);
                Log.v(TAG, "Recording started");
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }, 200);

    }

    private void destroyMediaProjection() {
        if (mMediaProjection == null) return;

        mMediaProjection.stop();

        mMediaProjection = null;

        Log.i(TAG, "MediaProjection Stopped");
    }
}

