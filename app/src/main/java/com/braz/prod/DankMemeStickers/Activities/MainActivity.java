package com.braz.prod.DankMemeStickers.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.util.IabHelper;
import com.braz.prod.DankMemeStickers.util.IabResult;
import com.braz.prod.DankMemeStickers.util.Inventory;
import com.braz.prod.DankMemeStickers.util.ProcessImage;
import com.braz.prod.DankMemeStickers.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.braz.prod.DankMemeStickers.util.ProcessImage.rotateImageIfRequired;

public class MainActivity extends AppCompatActivity {
    private static final int IMG_REQUEST_CODE = 188;
    private static final int CAMERA_REQUEST = 420;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0x005;
    private static final String SKU_REMOVE_ADS = "upgrade.to.pro.meme.stickers";//upgrade.to.pro.meme.stickers
    private static final int RC_REQUEST = 0x0123;
    private ImageView selector,camera;
    private Context context;
    private AdView mAdView;
    private LinearLayout adContainer;
    ImageView upgrade;
    // Listener that's called when we finish querying the items and
    // subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            //Toast.makeText(context,"premium",Toast.LENGTH_SHORT).show();

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                return;
            }

            Purchase removeAdsPurchase = inventory.getPurchase(SKU_REMOVE_ADS);

            if (removeAdsPurchase != null) {
                removeAds();
            }
        }
    };


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                return;
            }

            if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
                // bought the premium upgrade!
                Toast.makeText(context,"welcome to premium, m8 :)",Toast.LENGTH_SHORT).show();
                removeAds();

            }
        }
    };

    private void removeAds() {
        adContainer.setVisibility(View.INVISIBLE);
        //upgrade.setVisibility(View.GONE);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("PREMIUM",getString(R.string.premium)).apply();
        upgrade.setImageResource(R.drawable.premium);
        Resources r = context.getResources();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        upgrade.setMaxWidth(dim);
        upgrade.setClickable(false);
        //premium = (ImageView)findViewById(R.id.premium);
        //premium.setVisibility(View.VISIBLE);
    }

    // Called by button press
    private void buyProUpgrade()  {
        try {
            mHelper.launchPurchaseFlow(this, SKU_REMOVE_ADS, RC_REQUEST,
                    mPurchaseFinishedListener, "payLoad");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper mHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        camera = (ImageView) findViewById(R.id.photo);
        selector = (ImageView)findViewById(R.id.choose);
        upgrade = (ImageView)findViewById(R.id.upgrade);
        adContainer = (LinearLayout) findViewById(R.id.adsContainer);
        MobileAds.initialize(this, getString(R.string.admob_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("9B4E29286B48BF606EC7E4767C788368")
                .build();
        //adRequest.isTestDevice(context);
        mAdView.loadAd(adRequest);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if(!hasPermissions(context,PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_WRITE_EXTERNAL_STORAGE);
            }else{
                initChoosePicButton();
                initCameraButton();
                setUpgradeButton();
            }
        }else{
            initChoosePicButton();
            initCameraButton();
            setUpgradeButton();
        }

        mHelper = new IabHelper(this, getString(R.string.billingKey));
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (mHelper == null)
                    return;
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void setUpgradeButton() {
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpgradeDialog();
            }
        });
    }

    private void showUpgradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You want Dank meme faces, drawing mode, removed ads and some meme sounds? Then click Yeah m8 :)");

        builder.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                buyProUpgrade();
            }
        });
        builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
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
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initChoosePicButton();
                    initCameraButton();
                    setUpgradeButton();
                } else {
                    Toast.makeText(context,"permission denied",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    private void initChoosePicButton(){
        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMG_REQUEST_CODE);
            }
        });
    }

    private void initCameraButton(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBackCamera();
            }
        });
    }

    private String pictureImagePath = "";
    Uri outputFileUri;

    private void openBackCamera() {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/lopas.jpg";
        File file = new File(pictureImagePath);
       // outputFileUri = Uri.fromFile(file);
        outputFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (cameraIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mHelper == null)
            return ;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            if (requestCode == IMG_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bmp = ProcessImage.getCorrectlyOrientedImage(context,selectedImage);
                        createImageFromBitmap(bmp);
                        Intent intent = new Intent(this,PlayActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                File imgFile = new  File(pictureImagePath);
                if(imgFile.exists()) {
                    try {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        myBitmap = rotateImageIfRequired(myBitmap,context,imgFile.getAbsolutePath());
                        createImageFromBitmap(myBitmap);
                        Intent intent = new Intent(this, PlayActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return;
        }

    }
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
}
