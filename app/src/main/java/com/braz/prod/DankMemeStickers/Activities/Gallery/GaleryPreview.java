package com.braz.prod.DankMemeStickers.Activities.Gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.braz.prod.DankMemeStickers.Activities.Play.PlayFunctions;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.util.StorageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class GaleryPreview extends AppCompatActivity {
    ImageView GalleryPreviewImg;
    String path;
    String mediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_preview);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        mediaType = intent.getStringExtra("media_type");
        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        VideoView videoView = (VideoView) findViewById(R.id.video_preview);
        Log.d("path", path);
        setupImageVideo(videoView);
        setupToolbar();
    }

    void setupImageVideo(VideoView videoView) {
        if (mediaType.equals("video")) {
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoPath(path);
            videoView.start();
        } else {
            GalleryPreviewImg.setVisibility(View.VISIBLE);
            Picasso.get().load(new File(path)).fit().centerInside()// Uri of the picture
                    .into(GalleryPreviewImg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void setupToolbar() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.delete) {
            StorageUtils.deleteFile(path, this);
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } else if (item.getItemId() == R.id.share) {
            if (mediaType == "video") {
                PlayFunctions.shareIt(this, path, "", "video/mp4");
            } else {
                PlayFunctions.shareIt(this, path, "", "image/*");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
