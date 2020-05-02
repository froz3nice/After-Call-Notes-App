package com.braz.prod.DankMemeStickers.Activities.Play;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.braz.prod.DankMemeStickers.Activities.ActivityInterfaces.ActivityCallback;
import com.braz.prod.DankMemeStickers.Activities.Base.BaseActivity;
import com.braz.prod.DankMemeStickers.Activities.Gallery.AlbumActivity;
import com.braz.prod.DankMemeStickers.Activities.Play.Interfaces.ViewInteractionsListener;
import com.braz.prod.DankMemeStickers.Activities.SettingsActivity;
import com.braz.prod.DankMemeStickers.Animation.Animator;
import com.braz.prod.DankMemeStickers.Interfaces.DialogListener;
import com.braz.prod.DankMemeStickers.Interfaces.IOnFocusListenable;
import com.braz.prod.DankMemeStickers.Models.DataProvider;
import com.braz.prod.DankMemeStickers.Models.ImageModel;
import com.braz.prod.DankMemeStickers.Models.Song;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.Recorder.RecorderCallback;
import com.braz.prod.DankMemeStickers.Recorder.VideoRecorder;
import com.braz.prod.DankMemeStickers.SoundPlayer;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerTextView;
import com.braz.prod.DankMemeStickers.VideoMaker.VideoMaker;
import com.braz.prod.DankMemeStickers.VideoMaker.VideoProgressListener;
import com.braz.prod.DankMemeStickers.util.DialogUtils;
import com.braz.prod.DankMemeStickers.util.ImageProcessingUtils;
import com.braz.prod.DankMemeStickers.util.StorageUtils;
import com.braz.prod.DankMemeStickers.util.Utils;
import com.braz.prod.DankMemeStickers.util.view.CustomProgressBar;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.braz.prod.DankMemeStickers.Activities.Play.PlayFunctions.getDrawableUri;
import static com.braz.prod.DankMemeStickers.Recorder.VideoRecorder.REQUEST_CODE;
import static com.braz.prod.DankMemeStickers.util.ImageProcessingUtils.getScreenShot;
import static com.braz.prod.DankMemeStickers.util.ImageProcessingUtils.rotateImageIfRequired;
import static com.braz.prod.DankMemeStickers.util.Utils.getMainImageMatrix;
import static com.braz.prod.DankMemeStickers.util.Utils.getPath;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenHeight;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenWidth;
import static com.braz.prod.DankMemeStickers.util.Utils.getTimeStamp;

public class PlayActivity extends BaseActivity implements IOnFocusListenable, ActivityCallback, RecorderCallback {
    ImageView mainImage;
    Context context;
    ArrayList<ImageModel> imageList;
    private boolean changedOnce;
    private ListView listView;
    ConstraintLayout layout;
    int yOriginalJoint = 50 * -1, xOriginalJoint = 50 * -1;
    int yOriginalGlasses = 50 * -1, xOriginalGlasses = 50 * -1;
    float yFixedJoint = 0, xFixedJoint = 0;
    float yFixedGlasses = 0, xFixedGlasses = 0;
    StickerTextView text;
    SimpleDraweeView snoopGif;
    BottomNavigationView bottomNavigationView;
    private boolean isPremium = false;
    private AdView mAdView;
    private Animator animator;
    boolean wasSnoopInit = false;
    VideoRecorder videoRecorder;
    MediaProjectionManager mProjectionManager;
    VideoMaker videoMaker;
    String videoPath = "";
    float statusBarHeight = 0f;
    private ImageView zoomImg;
    private ImageView zoomPlus;
    private ImageView zoomMinus;
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mainImage = findViewById(R.id.chosen);
        context = this;
        videoMaker = new VideoMaker(context, getWindowManager());
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        videoRecorder = new VideoRecorder(this, mProjectionManager, getWindowManager());
        videoPath = getIntent().getStringExtra("video_path");
        isPremium = PreferenceManager.getDefaultSharedPreferences(context).getString("PREMIUM", "").equals(getString(R.string.premium));
        imageList = DataProvider.getImageDrawables();
        initAd();
        layout = findViewById(R.id.activity_play);
        text = new StickerTextView(this);
        snoopGif = this.findViewById(R.id.snoop);
        listView = this.findViewById(R.id.listView);
        changedOnce = false;
        soundPlayer = new SoundPlayer(this);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(getDrawableUri(R.drawable.snoop))
                .setAutoPlayAnimations(true)
                .build();
        snoopGif.setController(controller);
        snoopGif.bringToFront();

        animator = new Animator(context, getWindowManager(), layout, yOriginalJoint, xOriginalJoint,
                yOriginalGlasses, xOriginalGlasses, yFixedJoint, xFixedJoint,
                yFixedGlasses, xFixedGlasses, snoopGif, wasSnoopInit);

        initListView();
        setupHint();
        initBottomNavView();
        handleMainImage();
        if (isPremium) {
            mAdView.setVisibility(View.GONE);
        }
        showSystemUI();
        setUpZoom();
        setUpImageFilters();
        setUpSongSelect();
    }

    Song song;

    private void setUpSongSelect() {
        song = DataProvider.getSongs().get(0);
//        btnSongSelect = findViewById(R.id.btn_song_select);
//        findViewById(R.id.btn_song_select).setOnClickListener(view -> {
//
//        });
    }

    private void setUpImageFilters() {
//        filterToggle = findViewById(R.id.filter_toggle);
//        filterToggle.setOnClickListener(view -> {
//
//        });
    }

    float zoomPivotX = 1f, zoomPivotY = 1f;

    private void setUpZoom() {
        zoomPlus = findViewById(R.id.zoom_plus);
        zoomMinus = findViewById(R.id.zoom_minus);
        zoomImg = findViewById(R.id.zoom_img);

        zoomPivotX = getScreenWidth(getWindowManager()) / 2;
        zoomPivotY = getScreenHeight(getWindowManager()) / 2;
        ZoomDragListener touchListener = new ZoomDragListener((x, y) -> {
            zoomPivotX = x;
            zoomPivotY = y;
        });

        zoomImg.setOnTouchListener(touchListener);

        zoomPlus.setOnClickListener(view -> {
            zoomValue += 0.4f;
            zoomImg.setVisibility(View.GONE);
            setImageScale();
            Log.d("zoomValue", zoomValue + "");
            // animator.zoomDrag(zoomValue,mainImage,zoomPivotX,zoomPivotY);
        });
        zoomMinus.setOnClickListener(view -> {
            if (zoomValue > 1.3f) {
                zoomValue -= 0.4f;
                Log.d("zoomValue", zoomValue + "");
                setImageScale();
                if (zoomValue < 1.4)
                    zoomImg.setVisibility(View.VISIBLE);
                // animator.zoomDrag(zoomValue,mainImage,zoomPivotX,zoomPivotY);
            }
        });
    }

    void setImageScale() {
        mainImage.setPivotX(zoomPivotX);
        mainImage.setPivotY(zoomPivotY);
        mainImage.setScaleX(zoomValue);
        mainImage.setScaleY(zoomValue);
    }


    View hintView;

    private void setupHint() {
        hintView = findViewById(R.id.hint_view);

        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("openCount",
                PreferenceManager.getDefaultSharedPreferences(this).getInt("openCout", 0) + 1).apply();
        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("openCout", 0) > 2) {
            hintView.setVisibility(View.GONE);
        }
        hintView.setOnTouchListener((view, motionEvent) -> {
            hintView.setVisibility(View.GONE);
            speedDialView.setVisibility(View.VISIBLE);
           // zoomToggle.setVisibility(View.VISIBLE);
//            filterToggle.setVisibility(View.VISIBLE);
//            btnSongSelect.setVisibility(View.VISIBLE);
            return false;
        });
        TextView textView2 = hintView.findViewById(R.id.hint_gallery);

        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/font_shadow.ttf");
        textView2.setTypeface(typeFace);
    }

    private void initListView() {
        StickersAdapter adapter = new StickersAdapter(imageList, context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) ->
                PlayFunctions.addSticker(imageList.get(position), this, layout, this));
    }

    private void handleMainImage() {
        Uri imageUri = Uri.parse(getIntent().getStringExtra("image_uri"));
        String imgType = getIntent().getStringExtra("image_type");
        Log.d("image uri", imageUri.toString());
        if (imgType.equals("gallery")) {
            Log.d("galerry", imageUri.toString());
            try {
                Bitmap bmp = ImageProcessingUtils.getCorrectlyOrientedImage(context, imageUri);
                StorageUtils.createImageFromBitmap(context, bmp);
                loadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("camera", imageUri.toString());
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(imageUri.toString());
                myBitmap = rotateImageIfRequired(myBitmap, context, imageUri.toString());
                StorageUtils.createImageFromBitmap(this, myBitmap);
                loadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    float zoomValue = 1f;

    void loadImage() {
        Utils.loadImage(this, mainImage, p -> {
            mainImage.setImageMatrix(getMainImageMatrix(mainImage));
            zoomListener = new OnZoomTouchListener(this,
                    getMainImageMatrix(mainImage),
                    mainImage.getWidth(), (zoomValue, pivotX, pivotY) -> {
                //this.zoomValue = zoomValue;
                this.zoomPivotX = pivotX;
                this.zoomPivotY = pivotY;
            }, new ViewInteractionsListener() {
                @Override
                public void onStopRecording() {
                    stopRecording();
                }

                @Override
                public void onRemoveBorders() {
                    removeBorders();
                }
            });

            mainImage.setOnTouchListener(zoomListener);
        });
    }

    void removeBorders() {
        PlayFunctions.removeBorders(layout, this);
    }

    OnZoomTouchListener zoomListener;

    private void addAd() {
        if (!isPremium) {
            mAdView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }

    }

    @Override
    public void onStoppedRecording(String path) {
        refreshGalery(path, PlayActivity.this);
    }

    @Override
    public void startScreenCaptureIntent() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    @Override
    public void onRecordingTimeEnded() {
        stopRecording();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED || data == null) return;

        if (requestCode != REQUEST_CODE) {
            String TAG = "TAG_VIDEOS";
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("starting video", "yess nig");
        hideSystemUI();
        startThugLife(statusBarHeight);
        removeComponents();
        videoRecorder.startVideo(data, resultCode);
        isRecording = true;
    }

    private void startThugLife(float offset) {
        mainImage.setScaleX(1f);
        mainImage.setScaleY(1f);
        hideZoomOptions();
        hideButtons();
        hintView.setVisibility(View.GONE);
        animator.startAllAnimations(PlayActivity.this, offset, zoomValue, zoomPivotX, zoomPivotY, mainImage);
    }

    private void initAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mAdView != null) mAdView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        soundPlayer.resumePlayer();
        if (mAdView != null) mAdView.resume();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!changedOnce) {
            int[] snoopLocation = new int[2];
            snoopGif.getLocationOnScreen(snoopLocation);
            int ySnoopOriginal = snoopLocation[1];
            int xSnoopOriginal = snoopLocation[0];
            animator.setSnoopCoordinates(xSnoopOriginal, ySnoopOriginal);
            changedOnce = true;
            statusBarHeight = getStatusBarHeight();
        }
    }
    boolean filterPressed = false;
    boolean zoomPressed = false;

    SpeedDialView speedDialView;
    @SuppressLint("ClickableViewAccessibility")
    private void initBottomNavView() {
        bottomNavigationView = findViewById(R.id.bottom_nav);

        speedDialView  = findViewById(R.id.speedDial);
                speedDialView.addActionItem(
                        new SpeedDialActionItem.Builder(R.id.action_zoom, R.drawable.zoom_img)
                                .create());
        speedDialView.inflate(R.menu.fab_menu);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()){
                    case R.id.action_apply_filter :{
                        if (filterPressed) {
                            mainImage.clearColorFilter();
                        } else {
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setSaturation(0);
                            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                            mainImage.setColorFilter(filter);
                        }
                        filterPressed = !filterPressed;
                        break;
                    }
                    case R.id.action_rate_app :{
                        launchMarket();
                        break;
                    }
                    case R.id.action_zoom :{
                        if(!zoomPressed) showZoomOptions();
                        else hideZoomOptions();
                        break;
                    }
                    case R.id.action_select_song :{
                        DialogUtils.showSelectSong(PlayActivity.this, s -> {
                            song = s;
                        });
                        break;
                    }
                    case R.id.action_settings :{
                        startActivity(new Intent(PlayActivity.this, SettingsActivity.class));
                        break;
                    }
                }
                return false;
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_new_sticker:
                    listViewVisible = !listViewVisible;
                    if (listViewVisible) {
                        listView.setVisibility(View.VISIBLE);
                        listView.bringToFront();
                    } else {
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case R.id.action_save:
                    DialogUtils.showSaveDialog(context, () -> {
                        startSaving();
                        Toast.makeText(context, "Dank image saved!", Toast.LENGTH_SHORT).show();
                    });
                    break;
//                case R.id.action_share:
//                    startSaving();
//                    PlayFunctions.shareIt(context, file, Utils.getPath(this), "image/*");
//                    break;
                case R.id.action_photo_lib:
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("name", "ThugLifeCreator");
                    startActivity(intent);
                    break;
                case R.id.action_snoop:
                    DialogUtils.showRecordDialog(this, new DialogListener() {
                        @Override
                        public void savePressed() {
                            initRecording(PlayActivity.this);
                        }

                        @Override
                        public void cancelPressed() {
                            startThugLife(0f);
                        }
                    });
                    break;
            }
            return true;
        });
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void initRecording(Context context) {
        videoRecorder.initRecorder(context);
        videoRecorder.shareScreen(this);
        //isRecording = true;
    }

    boolean isRecording = false;
    CustomProgressBar dialog;

    void stopRecording() {
        Log.d("isRecording", String.valueOf(isRecording));
        if (isRecording) {
            videoRecorder.stopRecording(this);
            Toast.makeText(PlayActivity.this, "Video recording finished!", Toast.LENGTH_SHORT).show();
            mergeVideoWithAudio();
            showSystemUI();
            bringBackAllUi();
            isRecording = false;
            zoomListener.restoreZoom(mainImage);
        }
    }

    void mergeVideoWithAudio() {
        videoMaker.mergeAudioWithVideo(videoRecorder.getFileName(), song.getRes(),
                this::prepareMergeVideoDialog);
    }

    void prepareMergeVideoDialog(String path) {
        runOnUiThread(() -> {
            refreshGalery(path, PlayActivity.this);
            Log.d("file1 name", path);
            Log.d("file2 name", videoPath);
            if (videoPath.isEmpty()) return;

            if(!this.isFinishing())
            DialogUtils.showMakeVideoDialog(PlayActivity.this, () -> {
                doVideoConcatenating(path);
                dialog = CustomProgressBar.getInstance();
                dialog.showProgress(context, "0 %", false);
                dialog.setProgress(0);
            });
        });
    }

    private void doVideoConcatenating(String thugLifeVideoPath) {

        videoMaker.concatenate(thugLifeVideoPath, videoPath, new VideoProgressListener() {
            @Override
            public void onFinished(String file) {
                refreshGalery(file, PlayActivity.this);

                dialog.hideProgress();
                Toast.makeText(PlayActivity.this, "Video prepared!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(float percent) {
                Log.d("OnProgress ", String.valueOf(percent));
                dialog.setProgress(percent);
                dialog.changeText(percent);
            }

            @Override
            public void onFailed() {
                Toast.makeText(PlayActivity.this, "Video preparation failed :(", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    dialog.hideProgress();
                }, 200);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("OnStop", "called");
        stopRecording();
    }


    String file = "";
    void startSaving() {
        removeComponents();
        View rootView = getWindow().getDecorView().getRootView();
        file = getTimeStamp() + ".jpg";

        StorageUtils.store(this, getScreenShot(rootView), file, path -> {

        });
        bringBackAllUi();
    }


    void bringBackAllUi() {
        if (!isPremium) mAdView.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        speedDialView.setVisibility(View.VISIBLE);
        listView.bringToFront();
    }

    public void removeComponents() {
        mAdView.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        speedDialView.setVisibility(View.GONE);
        PlayFunctions.removeBorders(layout, this);
        hideZoomOptions();
        hideButtons();
    }

    private void hideButtons() {
        //zoomToggle.setVisibility(View.GONE);
//        filterToggle.setVisibility(View.GONE);
//        btnSongSelect.setVisibility(View.GONE);
    }

    private void showZoomOptions() {
        zoomMinus.setVisibility(View.VISIBLE);
        zoomPlus.setVisibility(View.VISIBLE);
        zoomImg.setVisibility(View.VISIBLE);
    }

    private void hideZoomOptions() {
        zoomMinus.setVisibility(View.INVISIBLE);
        zoomPlus.setVisibility(View.INVISIBLE);
        zoomImg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideListView() {
        listViewVisible = false;
        listView.setVisibility(View.GONE);
    }

    boolean listViewVisible = false;

    @Override
    public void playSound() {
        soundPlayer.playSound(song.getRes());
    }

    @Override
    public void pausePlayer() {
        soundPlayer.pausePlayer();
    }

    @Override
    public void onStickerAdded() {
    }

    @Override
    public void onStickerRemoved() {
    }

}
