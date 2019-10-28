package com.braz.prod.DankMemeStickers.Activities.Base;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.braz.prod.DankMemeStickers.Activities.Play.PlayActivity;
import com.braz.prod.DankMemeStickers.Activities.VideoMakerActivity;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    protected void refreshGalery(String path) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
    }

    public void startPlayActivity(String path, String imageType, String videoPath) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("image_uri", path);
        intent.putExtra("image_type", imageType);
        intent.putExtra("video_path", videoPath);

        startActivity(intent);
    }

    public void startVideoMakerActivity(String videoUri) {
        Intent intent = new Intent(this, VideoMakerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("video_uri", videoUri);
        startActivity(intent);
    }

    protected void hideSystemUI() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    protected void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public float getStatusBarHeight() {
        float statusBarHeight = 0f;
        float navBarHeight = 0f;

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        /*int resourceId2 = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId2 > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId2);
        }*/
        return statusBarHeight;
    }

}