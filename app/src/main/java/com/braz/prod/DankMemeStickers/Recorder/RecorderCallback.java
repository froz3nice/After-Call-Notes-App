package com.braz.prod.DankMemeStickers.Recorder;

public interface RecorderCallback {
    void onStoppedRecording(String path);
    void startScreenCaptureIntent();
    void onRecordingTimeEnded();
}
