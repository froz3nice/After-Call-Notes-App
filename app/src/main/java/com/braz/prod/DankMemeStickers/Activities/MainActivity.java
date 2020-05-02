package com.braz.prod.DankMemeStickers.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.braz.prod.DankMemeStickers.Activities.Base.BaseActivity;
import com.braz.prod.DankMemeStickers.Activities.Gallery.AlbumActivity;
import com.braz.prod.DankMemeStickers.Permissions.PermissionsCallback;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.util.PurchaseUtils.IabHelper;
import com.braz.prod.DankMemeStickers.util.PurchaseUtils.MainPurchases;
import com.braz.prod.DankMemeStickers.util.StorageUtils;
import com.braz.prod.DankMemeStickers.util.Utils;
import com.braz.prod.DankMemeStickers.util.view.CustomProgressBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;

import static com.braz.prod.DankMemeStickers.Permissions.Permissions.REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.braz.prod.DankMemeStickers.Permissions.Permissions.hasPermissions;
import static com.braz.prod.DankMemeStickers.util.DialogUtils.showUpgradeDialog;
import static com.braz.prod.DankMemeStickers.util.ImageProcessingUtils.getScreenShot;
import static com.braz.prod.DankMemeStickers.util.Utils.getTimeStamp;

public class MainActivity extends BaseActivity {
    private static final int IMG_REQUEST_CODE = 189;
    private static final int VIDEO_REQUEST_CODE = 188;
    private static final int CAMERA_REQUEST_PHOTO = 420;
    private static final int CAMERA_REQUEST_VIDEO = 421;

    private Button take_photo, uploadPhoto, takeVideo, uploadVideo, gallery;
    private Context context;
    private AdView mAdView;
    private LinearLayout adContainer;
    ImageView upgrade;
    private MainPurchases purchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        uploadPhoto = findViewById(R.id.upload_photo);
        take_photo = findViewById(R.id.take_photo);
        uploadVideo = findViewById(R.id.upload_video);
        takeVideo = findViewById(R.id.take_video);
        gallery = findViewById(R.id.gallery);

        upgrade = findViewById(R.id.upgrade);
        adContainer = findViewById(R.id.adsContainer);
        MobileAds.initialize(this, getString(R.string.admob_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("9B4E29286B48BF606EC7E4767C788368")
                .build();
        //adRequest.isTestDevice(context);
        mAdView.loadAd(adRequest);
        askReadWritePermissions(this, this::initButtons);
        purchases = new MainPurchases(this);
    }

    public void removeAds() {
        adContainer.setVisibility(View.INVISIBLE);
        //upgrade.setVisibility(View.GONE);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("PREMIUM", getString(R.string.premium)).apply();
        upgrade.setImageResource(R.drawable.premium);
        Resources r = context.getResources();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        upgrade.setMaxWidth(dim);
        upgrade.setClickable(false);
        //premium = (ImageView)findViewById(R.id.premium);
        //premium.setVisibility(View.VISIBLE);
    }

    public void askReadWritePermissions(Activity context, PermissionsCallback callback) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(context, PERMISSIONS)) {
                ActivityCompat.requestPermissions(context, PERMISSIONS, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                callback.onPermissionsAccepted();
            }
        } else {
            callback.onPermissionsAccepted();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (purchases.getmHelper() != null) {
            try {
                purchases.getmHelper().dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            purchases.setmHelper(null);
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initButtons();
                } else {
                    Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void initButtons() {
        StorageUtils.makeFolder(this);
        upgrade.setOnClickListener(view -> showUpgradeDialog(this, () -> purchases.buyProUpgrade()));
        gallery.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("name", "ThugLifeCreator");
            startActivity(intent);
        });

        take_photo.setOnClickListener(v -> {
            openBackCamera(android.provider.MediaStore.ACTION_IMAGE_CAPTURE, CAMERA_REQUEST_PHOTO, ".jpg");
        });

        uploadPhoto.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, IMG_REQUEST_CODE);
        });
        takeVideo.setOnClickListener(v -> {
            openBackCamera(MediaStore.ACTION_VIDEO_CAPTURE, CAMERA_REQUEST_VIDEO, ".mp4");
        });
        uploadVideo.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, VIDEO_REQUEST_CODE);
        });
    }

    private String mediaPath = "";
    Uri outputFileUri;

    private void openBackCamera(String mediaMode, Integer requestCode, String mediaTypeExtension) {
        mediaPath = Utils.getPath(this) + "/temp" + mediaTypeExtension;
        File file = new File(mediaPath);
        // outputFileUri = Uri.fromFile(file);
        outputFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent cameraIntent = new Intent(mediaMode);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (purchases.getmHelper() == null)
            return;

        // Pass on the activity result to the helper for handling
        if (!purchases.getmHelper().handleActivityResult(requestCode, resultCode, data)) {
            if (resultCode != Activity.RESULT_OK) return;

            if (requestCode == IMG_REQUEST_CODE) {
                if (data != null) {
                    startPlayActivity(data.getData().toString(),"gallery","");
                }
            }
            if (requestCode == CAMERA_REQUEST_PHOTO) {
                File imgFile = new File(mediaPath);
                if (imgFile.exists()) {
                    startPlayActivity(imgFile.getPath(),"uploadPhoto","");
                }
            }
            if (requestCode == CAMERA_REQUEST_VIDEO) {
                File imgFile = new File(mediaPath);
                if (imgFile.exists()) {
                    startVideoMakerActivity(imgFile.getPath());
                }
            }
            if (requestCode == VIDEO_REQUEST_CODE) {
                if (data != null) {
                    startVideoMakerActivity(data.getData().toString());
                }
            }
        } else {
            return;
        }
    }


}
