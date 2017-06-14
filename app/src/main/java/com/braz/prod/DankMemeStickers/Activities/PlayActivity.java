package com.braz.prod.DankMemeStickers.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.braz.prod.DankMemeStickers.PaintView;
import com.bumptech.glide.Glide;
import com.braz.prod.DankMemeStickers.CustomAdapter;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerImageView;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerTextView;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerView;
import com.braz.prod.DankMemeStickers.util.ProcessImage;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;

public class PlayActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener  {

    private static final java.lang.String COLOR_DIALOG = "Color_dialog";
    private static final String COLOR_DIALOG_FOR_PENCIL = "Pencil_dialog";
    private ImageView listViewSwitch;
    ImageView  dawg;
    ImageView snoopGif;
    ImageView chosenPepe;
    ImageView save;
    ImageView play, musicButton;
    int yOriginalJoint = 50*-1 ,xOriginalJoint = 50*-1 ;
    int yOriginalGlasses = 50*-1 ,xOriginalGlasses = 50*-1 ;
    float yFixedJoint = 0 ,xFixedJoint = 0 ;
    float yFixedGlasses = 0 ,xFixedGlasses = 0 ;
    FrameLayout layout;
    Context context;
    private int xSnoopOriginal = 0;
    private int ySnoopOriginal = 0;
    MediaPlayer mPlayer;
    boolean changedOnce;
    ListView listView;
    ArrayList<Integer> imageList;
    StickerImageView joint,glasses,pepe,trollFace,alone,angryF;
    StickerImageView happyPepe,megusta,peter_parker,savage_sponge;
    private StickerImageView wtf,cj,big_smoke;
    private StickerImageView cage,doge,pink_guy,vsauce;
    StickerTextView text;
    MediaPlayer soundPlayer;
    ImageView txt;
    Receiver receiver;
    RemoveBordersReceiver removeReceiver;
    ArrayList<String> ownerIds;
    private ImageView pencil;
    private boolean isDrawingMode = false;
    PaintView painting;
    private ImageView palette;
    private ImageView eraser;
    private boolean isPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = getApplicationContext();
        if(PreferenceManager.getDefaultSharedPreferences(context).getString("PREMIUM","").equals(getString(R.string.premium))){
            isPremium = true;
        }else{
            isPremium = false;
        }
        initAllUIElements();
        setImageDrawables();
        initListView();
        clearBordersListener();
        changedOnce = false;
        initListViewVisibilityListener();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
            }
        });
        setOwnerIds();
        loadMainImage();
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextInputPrompt();
            }
        });
        receiverForPlayButtonVisibility();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joint_glasses_Movement();
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(soundPlayer != null) {
                    soundPlayer.start();
                }else{
                    Toast.makeText(context,"feels bad man, no sounds for selected meme :( ",Toast.LENGTH_SHORT).show();
                }

            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDrawingMode = !isDrawingMode;
                if(isDrawingMode) {
                    pencil.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.exit));
                    palette.setVisibility(View.VISIBLE);
                    eraser.setVisibility(View.VISIBLE);
                    addDrawingMode();
                }else{
                    pencil.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pencil));
                    palette.setVisibility(View.GONE);
                    eraser.setVisibility(View.GONE);
                    disableDrawingMode();
                }
            }
        });
        palette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleColorDialog.build()
                        .title(R.string.pick_a_color)
                        .allowCustom(true)
                        .show(PlayActivity.this, COLOR_DIALOG_FOR_PENCIL);
            }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePainting();
            }
        });
        IntentFilter filter = new IntentFilter("com.remove.borders");
        removeReceiver = new RemoveBordersReceiver();
        this.registerReceiver(removeReceiver, filter);
    }
    private void removePainting(){
        ViewGroup viewGroup = (ViewGroup) layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.clearCanvas();
            }
        }
    }

    private void disableDrawingMode(){
        ViewGroup viewGroup = (ViewGroup) layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.stopOnDrawCall();
            }
        }
        layout.bringToFront();
        txt.bringToFront();
        listViewSwitch.bringToFront();
        save.bringToFront();
        musicButton.bringToFront();
        if(checkJoint_Glasses()){
            play.bringToFront();
        }
    }


    private void addDrawingMode(){
        hideListView();
        removeBorders();
        painting = new PaintView(context);
        Resources r = context.getResources();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, r.getDisplayMetrics());
        painting.setPadding(0,0,0,dim);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dim;
        layout.addView(painting,params);
        painting.bringToFront();
    }

    private void receiverForPlayButtonVisibility(){
        IntentFilter filter = new IntentFilter("com.remove.btn");
        receiver = new Receiver();
        this.registerReceiver(receiver, filter);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to save your dank creation?");
        builder.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                initSaveOnClickListener();
            }
        });
        builder.setNegativeButton("nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void initSaveOnClickListener(){
        save.setVisibility(View.GONE);
        txt.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        palette.setVisibility(View.GONE);
        eraser.setVisibility(View.GONE);
        pencil.setVisibility(View.GONE);
        musicButton.setVisibility(View.GONE);
        removeBorders();
        listViewSwitch.setVisibility(View.GONE);
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        int cnt = PreferenceManager.getDefaultSharedPreferences(context).getInt("counter",0) + 1;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("counter",cnt).apply();
        store(getScreenShot(rootView),String.valueOf(cnt));

        Toast.makeText(context,"Dank image saved!",Toast.LENGTH_SHORT).show();
        save.setVisibility(View.VISIBLE);
        listViewSwitch.setVisibility(View.VISIBLE);
        if(joint.getParent() == layout && glasses.getParent() == layout){
            play.setVisibility(View.VISIBLE);
        }
        if(isPremium){
            pencil.setVisibility(View.VISIBLE);
        }
        musicButton.setVisibility(View.VISIBLE);
        txt.setVisibility(View.VISIBLE);
    }


    private void loadMainImage(){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context
                    .openFileInput("myImage"));
            Picasso.with(context)
                    .load(getImageUri(context,bitmap))
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
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(removeReceiver);
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if(play != null) {
                play.setVisibility(View.GONE);
            }
        }
    }
    private class RemoveBordersReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            removeBorders();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DankMemeStickers";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaScannerConnection.scanFile(this, new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOwnerIds(){
        ownerIds = new ArrayList<>();

        ownerIds.add("pepe");
        ownerIds.add("joint");
        ownerIds.add("glasses");
        ownerIds.add("pepe_crocodile");
        ownerIds.add("pepe_dickbutt");
        ownerIds.add("alone");
        ownerIds.add("angryf");
        ownerIds.add("happy_pepe");
        ownerIds.add("trollFace");
        ownerIds.add("megusta");
        ownerIds.add("peter_parker");
        ownerIds.add("savage_sponge");
        ownerIds.add("cj");
        ownerIds.add("big_smoke");
        ownerIds.add("cannabis");
        ownerIds.add("can420");
        ownerIds.add("cig");
        ownerIds.add("cage");
        ownerIds.add("doge");
        ownerIds.add("pink_guy");
        ownerIds.add("vsauce");
        ownerIds.add("crying_jordan");
        ownerIds.add("dickbutt");
        ownerIds.add("feels_bad");
        ownerIds.add("feels_why_u_no");
        ownerIds.add("spaidermem");
        ownerIds.add("squid");
        ownerIds.add("suit_pepe");
        ownerIds.add("baby_face");
        ownerIds.add("guy_with_hat");
        ownerIds.add("kim_chon_un");
        ownerIds.add("bush");
        if(isPremium) {
            ownerIds.add("wtf2");
            ownerIds.add("wtf3");
            ownerIds.add("cone_head");
            ownerIds.add("doge2");
            ownerIds.add("donald");
            ownerIds.add("hide_the_pain_harold");
            ownerIds.add("illuminati_eye");
            ownerIds.add("jap_feels_guy");
            ownerIds.add("red__memes_pepe");
            ownerIds.add("feels_bad_man_pepe");
            ownerIds.add("lemme_smash");
            ownerIds.add("pathetic");
            ownerIds.add("pepe_gun");
            ownerIds.add("pikachu");
            ownerIds.add("screaming_pepe");
            ownerIds.add("tap_head");
            ownerIds.add("thug_life_hat");
            ownerIds.add("becky_profile");
            ownerIds.add("chain");
        }
    }

    private void setImageDrawables(){
        imageList = new ArrayList<>();

        imageList.add(R.drawable.pepe);
        imageList.add(R.drawable.joint);
        imageList.add(R.drawable.right);
        imageList.add(R.drawable.pepe_crocodile);
        imageList.add(R.drawable.pepe_dickbutt);
        imageList.add(R.drawable.alone);
        imageList.add(R.drawable.angryf);
        imageList.add(R.drawable.happy_pepe);
        imageList.add(R.drawable.trollface);
        imageList.add(R.drawable.megusta);
        imageList.add(R.drawable.peter_parker);
        imageList.add(R.drawable.savage_sponge);
        imageList.add(R.drawable.cj);
        imageList.add(R.drawable.big_smoke);
        imageList.add(R.drawable.cannabis);
        imageList.add(R.drawable.can420);
        imageList.add(R.drawable.cig);
        imageList.add(R.drawable.cage);
        imageList.add(R.drawable.doge);
        imageList.add(R.drawable.pink_guy);
        imageList.add(R.drawable.vsauce);
        imageList.add(R.drawable.crying_jordan);
        imageList.add(R.drawable.dickbutt);
        imageList.add(R.drawable.feels_bad);
        imageList.add(R.drawable.feels_why_u_no);
        imageList.add(R.drawable.spaidermem);
        imageList.add(R.drawable.squid);
        imageList.add(R.drawable.suit_pepe);
        imageList.add(R.drawable.baby_face);
        imageList.add(R.drawable.guy_with_hat);
        imageList.add(R.drawable.kim_chon_un);
        imageList.add(R.drawable.bush);
        if(isPremium) {
            imageList.add(R.drawable.wtf2);
            imageList.add(R.drawable.wtf3);
            imageList.add(R.drawable.cone_head);
            imageList.add(R.drawable.doge2);
            imageList.add(R.drawable.donald);
            imageList.add(R.drawable.hide_the_pain_harold);
            imageList.add(R.drawable.illuminati_eye);
            imageList.add(R.drawable.jap_feels_guy);
            imageList.add(R.drawable.red__memes_pepe);
            imageList.add(R.drawable.feels_bad_man_pepe);
            imageList.add(R.drawable.lemme_smash);
            imageList.add(R.drawable.pathetic);
            imageList.add(R.drawable.pepe_gun);
            imageList.add(R.drawable.pikachu);
            imageList.add(R.drawable.screaming_pepe);
            imageList.add(R.drawable.tap_head);
            imageList.add(R.drawable.thug_life_hat);
            imageList.add(R.drawable.becky_profile);
            imageList.add(R.drawable.chain);
        }
    }

    private void initListView(){

        CustomAdapter adapter = new CustomAdapter(imageList,context);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addSticker(imageList.get(position),ownerIds.get(position));
            }
        });
    }

    private void addText(String txt){
        removeBorders();
        text = new StickerTextView(PlayActivity.this,true);
        text.setOwnerId("text");
        text.setText(txt);
        layout.addView(text);
    }

    private boolean checkJoint_Glasses(){
        boolean jointExists = false;
        boolean glassesExist = false;

        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if(viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                if (child.getOwnerId().equals("joint")) {
                    jointExists = true;
                }
                if (child.getOwnerId().equals("glasses")) {
                    glassesExist = true;
                }
            }
        }
        return jointExists && glassesExist;
    }


    private void addSticker(int drawable,String ownerId){
        Resources r = context.getResources();

        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
        StickerImageView sticker = new StickerImageView(PlayActivity.this);
        sticker.setImageBitmap(
                ProcessImage.decodeSampledBitmapFromResource(getResources(), drawable, dim, dim));
        sticker.setOwnerId(ownerId);
        removeBorders();
        layout.addView(sticker);
        sticker.setControlItemsHidden(false);
        if(checkJoint_Glasses()){
           play.setVisibility(View.VISIBLE);
            play.bringToFront();
        }
        musicButton.setVisibility(View.VISIBLE);
        musicButton.bringToFront();
        setSounds(ownerId);
    }


    private void setSounds(String name){
        switch(name){
            case "vsauce":
                soundPlayer = MediaPlayer.create(context, R.raw.hey_vsauce);
                break;
            case "trollFace":
                soundPlayer = MediaPlayer.create(context, R.raw.trololo);
                break;
            case "big_smoke":
                Random rnd = new Random();
                if(rnd.nextInt(2) == 0) {
                    soundPlayer = MediaPlayer.create(context, R.raw.train_cj);
                }else {
                    soundPlayer = MediaPlayer.create(context, R.raw.big_smoke_order);
                }
                break;
            case "pink_guy":
                soundPlayer = MediaPlayer.create(context, R.raw.its_time_to_stop_cutted);
                break;
            case "illuminati_eye":
                soundPlayer = MediaPlayer.create(context, R.raw.illuminati_song);
                break;
            case "lemme_smash":
                soundPlayer = MediaPlayer.create(context, R.raw.lemme_smash);
                break;
            case "becky_profile":
                soundPlayer = MediaPlayer.create(context, R.raw.u_wan_sum_fuk);
                break;
            case "pepe_crocodile":
                soundPlayer = MediaPlayer.create(context, R.raw.first_stars);
                break;
            case "pepe_dickbutt":
                soundPlayer = MediaPlayer.create(context, R.raw.second_stars);
                break;
            case "pepe_gun":
                soundPlayer = MediaPlayer.create(context, R.raw.drop_stars);
                break;
            case "pepe":
                soundPlayer = MediaPlayer.create(context, R.raw.best_cry_ever);
                break;
            case "screaming_pepe":
                soundPlayer = MediaPlayer.create(context, R.raw.heyey);
                break;
            default: break;
        }
    }

    private void removeBorders(){
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                child.setControlItemsHidden(true);
            }
        }
        hideListView();
    }

    public void hideListView(){
        listViewVisible = false;
        listView.setVisibility(View.GONE);
        listViewSwitch.setVisibility(View.VISIBLE);
    }

    private void animateObject(StickerImageView img,float x,float y,long duration){
        ObjectAnimator animX = ObjectAnimator.ofFloat(img, "x", x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(img, "y", y);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(duration);
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
    }

    private void animateSnoopDogg(){
        ObjectAnimator animX = ObjectAnimator.ofFloat(snoopGif, "x", 1000);
        ObjectAnimator animY = ObjectAnimator.ofFloat(snoopGif, "y", ySnoopOriginal);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(14000);
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
    }

    private void clearBordersListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBorders();
            }
        });
    }

    String textInput = "";
    private void showTextInputPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        builder.setTitle("Type some dank text");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textInput = input.getText().toString();
                addText(textInput);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Choose color", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textInput = input.getText().toString();
                SimpleColorDialog.build()
                        .title(R.string.pick_a_color)
                        .colorPreset(Color.WHITE)
                        .allowCustom(false)
                        .show(PlayActivity.this, COLOR_DIALOG);
                //dialog.cancel();
            }
        });

        builder.show();
    }

    boolean wasSnoopInit = false;

    private void joint_glasses_Movement(){
        if(!wasSnoopInit) {
            Glide.with(this).load(getDrawableToUri(context, R.drawable.snoop)).asGif().into(snoopGif);
            wasSnoopInit = true;
        }
        hideListView();
        xFixedGlasses = PreferenceManager.getDefaultSharedPreferences(context).getFloat("xFixedGlasses",10);
        yFixedGlasses = PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedGlasses",10);
        xFixedJoint = PreferenceManager.getDefaultSharedPreferences(context).getFloat("xFixedJoint",10);
        yFixedJoint = PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedJoint",10);

        removeBorders();
        snoopGif.setVisibility(View.VISIBLE);
        snoopGif.bringToFront();
        snoopGif.setX(0);
        snoopGif.setX(xSnoopOriginal-200);
        snoopGif.setY(ySnoopOriginal);

        initJoint_Glasses();
        glasses.setX(xOriginalGlasses);
        glasses.setY(yOriginalGlasses);
        joint.setY(yOriginalJoint);
        joint.setX(xOriginalJoint);
        animateObject(joint,xFixedJoint,yFixedJoint,3000);
        animateObject(glasses,xFixedGlasses,yFixedGlasses,3000);

        animateSnoopDogg();
        playSound();
    }
    private void initJoint_Glasses(){
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if(viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                if (child.getOwnerId().equals("joint")) {
                    joint = (StickerImageView) viewGroup.getChildAt(i);
                }
                if (child.getOwnerId().equals("glasses")) {
                    glasses = (StickerImageView) viewGroup.getChildAt(i);
                }
            }
        }
    }

    private void playSound(){
        Random rnd = new Random();
        int i = rnd.nextInt(3);
        if(mPlayer != null){
            mPlayer.stop();
        }
        mPlayer = MediaPlayer.create(this, R.raw.gta_sa);
        switch (i) {
            case 0 :
                mPlayer = MediaPlayer.create(this, R.raw.fuck_da_police);
                break;
            case 1 :
                mPlayer = MediaPlayer.create(this, R.raw.gta_sa);
                break;
            case 2 :
                mPlayer = MediaPlayer.create(this, R.raw.madafaka);
                break;
        }
        mPlayer.start();
    }

    public final Uri getDrawableToUri(@NonNull Context context,
                                      @AnyRes int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
    }

    private void initAllUIElements(){
        save = (ImageView)findViewById(R.id.save);
        listView = (ListView)findViewById(R.id.listView);
        dawg = (ImageView) findViewById(R.id.chosen);
        listViewSwitch = (ImageView) findViewById(R.id.pepe);
        chosenPepe = (ImageView) findViewById(R.id.chosenPepe);
        layout = (FrameLayout)findViewById(R.id.activity_play);
        snoopGif = (ImageView)findViewById(R.id.snoop);
        play = (ImageView)findViewById(R.id.play);
        musicButton = (ImageView)findViewById(R.id.cluckinBell);
        joint = new StickerImageView(PlayActivity.this);
        glasses = new StickerImageView(PlayActivity.this);
        text = new StickerTextView(PlayActivity.this);
        pencil = (ImageView)findViewById(R.id.pencil);
        if(isPremium){
            pencil.setVisibility(View.VISIBLE);
        }
        txt = (ImageView)findViewById(R.id.txt);
        palette = (ImageView)findViewById(R.id.palette);
        eraser = (ImageView)findViewById(R.id.eraser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer != null){
            mPlayer.stop();
        }
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        if(!changedOnce) {
            int[] snoopLocation = new int[2];
            snoopGif.getLocationOnScreen(snoopLocation);
            ySnoopOriginal = snoopLocation[1];
            xSnoopOriginal = snoopLocation[0];
            Log.d("asilo berete y", String.valueOf(ySnoopOriginal));
            Log.d("asilo berete x", String.valueOf(xSnoopOriginal));
            changedOnce = true;
        }
    }

    boolean listViewVisible = false;

    private void initListViewVisibilityListener(){
        listViewSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewVisible = !listViewVisible;
                if(listViewVisible){
                    listView.setVisibility(View.VISIBLE);
                    listView.bringToFront();
                    listViewSwitch.setVisibility(View.GONE);
                }else{
                    listView.setVisibility(View.GONE);
                    listViewSwitch.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if(which == BUTTON_POSITIVE && COLOR_DIALOG.equals(dialogTag)){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putInt("color",extras.getInt(SimpleColorDialog.COLOR)).apply();
            addText(textInput);
            return true;
        }
        if(which == BUTTON_POSITIVE && COLOR_DIALOG_FOR_PENCIL.equals(dialogTag)){
            changePencilColor(extras.getInt(SimpleColorDialog.COLOR));
            return true;
        }
        return false;
    }

    private void changePencilColor(int color){
        ViewGroup viewGroup =  layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.changeColor(color);
            }
        }
    }
}
