package com.braz.prod.DankMemeStickers.Activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.braz.prod.DankMemeStickers.Activities.Base.BaseActivity;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.VideoMaker.VideoMaker;
import com.braz.prod.DankMemeStickers.VideoMaker.VideoMakerCallback;
import com.braz.prod.DankMemeStickers.VideoTrimmer.HgLVideoTrimmer;

import static com.braz.prod.DankMemeStickers.util.VideoUtils.getTimeString;
import static com.braz.prod.DankMemeStickers.util.VideoUtils.getTotalVideoMillis;

public class VideoMakerActivity extends BaseActivity implements VideoMakerCallback {

    private static final int SECOND = 1000;
    private VideoMaker videoMaker;
    private HgLVideoTrimmer mVideoTrimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_maker);
        videoMaker = new VideoMaker(this,getWindowManager());
        Uri videoUri = Uri.parse(getIntent().getStringExtra("video_uri"));

        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            try {
                //mVideoTrimmer.setMaxDuration(maxDuration);
                mVideoTrimmer.setMaxDuration(getTotalVideoMillis(this, videoUri));
                mVideoTrimmer.setVideoMakerListener(this);
                //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
                mVideoTrimmer.setVideoURI(videoUri);
                mVideoTrimmer.setVideoInformationVisibility(true);
            }catch (RuntimeException e){
                Toast.makeText(this,"Unable to process video",Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoTrimmer.stopProgressBar();
    }

    @Override
    public void onSuccess(String videoPath) {
        refreshGalery(videoPath, this);
        runOnUiThread(() -> {

            videoMaker.loadLastFrameOfVideo(videoPath, new VideoMakerCallback() {
                @Override
                public void onSuccess(String file) {
                    VideoMakerActivity.this.finish();
                    Toast.makeText(VideoMakerActivity.this, "Video trimmed!", Toast.LENGTH_SHORT).show();
                    startPlayActivity(file,"uploadPhoto",videoPath,duration);
                }

                @Override
                public void videoDuration(Integer duration) {

                }

                @Override
                public void onError() { }
            });

        });
    }

    @Override
    public void onError() {
        //stopProgressBar();
    }
    Integer duration = 7;
    @Override
    public void videoDuration(Integer dur) {
        duration = dur;
    }

}
