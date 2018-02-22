package com.braz.prod.DankMemeStickers.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.braz.prod.DankMemeStickers.IOnFocusListenable;
import com.braz.prod.DankMemeStickers.PicTags;
import com.braz.prod.DankMemeStickers.PlayActivityFeatures;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.util.ProcessImage;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;

public class PlayActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener  {
    private static final java.lang.String COLOR_DIALOG = "Color_dialog";
    private static final String COLOR_DIALOG_FOR_PENCIL = "Pencil_dialog";
    private static final int REQUEST_CODE = 1080;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    ImageView  dawg;
    Context context;
    ArrayList<Integer> imageList;

    Receiver receiver;
    RemoveComponentsReceiver removeComponentsReceiver;
    AddComponentsReceiver addComponentsReceiver;
    RemoveBordersReceiver removeReceiver;
    ArrayList<String> ownerIds;
    private boolean isPremium = false;
    private AdView mAdView;
    private PicTags tags;
    PlayActivityFeatures paf;
    private int REQUEST_PERMISSIONS = 0x256a;
    private MediaRecorder mMediaRecorder = null;
    private MediaProjectionManager mProjectionManager;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;

    private int counterVideos = 0;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private int mScreenDensity;
    private String TAG = "TAG_VIDEOS";
    private static boolean recordMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = this;
        if(PreferenceManager.getDefaultSharedPreferences(context).getString("PREMIUM","").equals(getString(R.string.premium))){
            isPremium = true;
        }else{
            isPremium = false;
        }
        tags = new PicTags(isPremium);
        imageList = tags.setImageDrawables();
        ownerIds = tags.setOwnerIds();
        paf = new PlayActivityFeatures(context,PlayActivity.this,isPremium,imageList,ownerIds);
        initAd();
        dawg = (ImageView) findViewById(R.id.chosen);

        paf.clearBordersListener();
        loadMainImage();
        initReceivers();
        if(isPremium){
            mAdView.setVisibility(View.INVISIBLE);
        }
        IntentFilter filter = new IntentFilter("com.remove.borders");
        removeReceiver = new RemoveBordersReceiver();
        this.registerReceiver(removeReceiver, filter);

        PreferenceManager.getDefaultSharedPreferences(context).getInt(TAG,0);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
        final ImageView mToggleButton = (ImageView) findViewById(R.id.record);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordMode = true;
                cl++;
                if(cl % 2 == 0) {
                    try {
                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        stopScreenSharing();
                        notifyMediaScannerService(context,Environment.getExternalStorageDirectory().getAbsolutePath() + "/DankMemeStickers");
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }else{
                    if (ContextCompat.checkSelfPermission(PlayActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                            .checkSelfPermission(PlayActivity.this,
                                    Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(PlayActivity.this,
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                REQUEST_PERMISSIONS);
                        cl--;
                    } else {
                        initRecorder();
                        shareScreen();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "Timer went off!");
                                stopScreenSharing();
                            }
                        }, 10000);

                    }
                }
            }
        });

    }
    private void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                stopScreenSharing();
                mMediaRecorder = null;
                notifyMediaScannerService(context,Environment.getExternalStorageDirectory().getAbsolutePath() + "/DankMemeStickers");
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        cl = 0;
    }

    int cl = 0;
    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();

    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("PlayActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    @NonNull
    private String getRandomString() {
        String SALTCHARS = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 15) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    private void initRecorder() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ThugLifeCreator";
            File dir = new File(dirPath);
            if(!dir.exists())
                dir.mkdir();
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/ThugLifeCreator/"+getRandomString()+".mp4");
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder = null;
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
            mMediaProjection = null;
            stopScreenSharing();
            notifyMediaScannerService(context,Environment.getExternalStorageDirectory().getAbsolutePath() + "/ThugLifeCreator");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void initReceivers(){
        IntentFilter filter = new IntentFilter("com.remove.btn");
        receiver = new Receiver();
        this.registerReceiver(receiver, filter);

        IntentFilter filter2 = new IntentFilter("com.invisible.components");
        removeComponentsReceiver = new RemoveComponentsReceiver();
        this.registerReceiver(removeComponentsReceiver, filter2);

        IntentFilter filter3 = new IntentFilter("com.visible.components");
        addComponentsReceiver = new AddComponentsReceiver();
        this.registerReceiver(addComponentsReceiver, filter3);
    }

    private void addAd(){
        if(!isPremium){
            mAdView.setVisibility(View.VISIBLE);
        }
    }

    private void loadMainImage(){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context
                    .openFileInput("myImage"));
            Picasso.with(context)
                    .load(ProcessImage.getImageUri(context,bitmap))
                    .fit().centerInside()
                    .into(dawg, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //do smth when picture is loaded successfully
                            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < children.length; i++) {
                                    new File(dir, children[i]).delete();
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            //do smth when there is picture loading error
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(removeReceiver);
        this.unregisterReceiver(removeComponentsReceiver);
        this.unregisterReceiver(addComponentsReceiver);
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    private class RemoveComponentsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            paf.removeComponents();
            mAdView.setVisibility(View.GONE);
        }
    }

    private class AddComponentsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            addAd();
            paf.addComponents();
        }
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            paf.setPlayButtonVisibility();
        }
    }

    private class RemoveBordersReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            paf.removeBorders();
        }
    }

    private void initAd(){
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paf.pausePlayer();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        paf.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if(which == BUTTON_POSITIVE && COLOR_DIALOG.equals(dialogTag)){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt("color",extras.getInt(SimpleColorDialog.COLOR)).apply();
            paf.addText();
            Toast.makeText(context, "kobra", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(which == BUTTON_POSITIVE && COLOR_DIALOG_FOR_PENCIL.equals(dialogTag)){
            paf.changePencilColor(extras.getInt(SimpleColorDialog.COLOR));
            Toast.makeText(context, "asilas", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
