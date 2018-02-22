package com.braz.prod.DankMemeStickers;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
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

import com.braz.prod.DankMemeStickers.Activities.PlayActivity;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerImageView;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerTextView;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerView;
import com.braz.prod.DankMemeStickers.util.ProcessImage;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;

import static com.braz.prod.DankMemeStickers.util.ProcessImage.getScreenShot;

/**
 * Created by Juozas on 2018.02.19.
 */

public class PlayActivityFeatures implements IOnFocusListenable {
    private static final java.lang.String COLOR_DIALOG = "Color_dialog";
    private static final String COLOR_DIALOG_FOR_PENCIL = "Pencil_dialog";

    private boolean changedOnce;
    private ImageView chosenPepe;
    private ImageView save;
    private ImageView palette;
    private ImageView txt;
    private ImageView eraser;
    private ImageView pencil;
    private ImageView listViewSwitch;

    private ImageView play;
    private ImageView musicButton;
    private ListView listView;
    private StickerImageView joint;
    private StickerImageView glasses;
    Context context;
    PlayActivity activity;
    boolean isPremium;
    MediaPlayer soundPlayer;
    FrameLayout layout;
    MediaPlayer mPlayer;
    int yOriginalJoint = 50*-1 ,xOriginalJoint = 50*-1 ;
    int yOriginalGlasses = 50*-1 ,xOriginalGlasses = 50*-1 ;
    float yFixedJoint = 0 ,xFixedJoint = 0 ;
    float yFixedGlasses = 0 ,xFixedGlasses = 0 ;
    private int xSnoopOriginal = 0;
    private int ySnoopOriginal = 0;
    StickerTextView text;
    SimpleDraweeView snoopGif;
    private boolean isDrawingMode = false;
    private ArrayList<Integer> imageList;
    private ArrayList<String> ownerIds;


    public PlayActivityFeatures(Context context, PlayActivity activity, boolean isPremium,
                                ArrayList<Integer> imageList,ArrayList<String> ownerIds){
        this.context = context;
        this.isPremium = isPremium;
        this.activity = activity;
        this.imageList = imageList;
        this.ownerIds = ownerIds;
        layout = (FrameLayout)activity.findViewById(R.id.activity_play);
        play = (ImageView)activity.findViewById(R.id.play);
        musicButton = (ImageView)activity.findViewById(R.id.play_music);
        joint = new StickerImageView(activity);
        glasses = new StickerImageView(activity);
        text = new StickerTextView(activity);
        snoopGif = activity.findViewById(R.id.snoop);
        listView = (ListView)activity.findViewById(R.id.listView);
        listViewSwitch = (ImageView) activity.findViewById(R.id.listview_appear);
        save = (ImageView)activity.findViewById(R.id.save);
        chosenPepe = (ImageView) activity.findViewById(R.id.chosenPepe);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.snoop))
                .build();
        DraweeController controller =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true)
                        .build();
        snoopGif.setController(controller);
        snoopGif.bringToFront();
        pencil = (ImageView)activity.findViewById(R.id.pencil);
        txt = (ImageView)activity.findViewById(R.id.txt);
        changedOnce = false;

        if(isPremium){
            pencil.setVisibility(View.VISIBLE);
        }else{
            musicButton.setVisibility(View.GONE);
            txt.setVisibility(View.GONE);
        }
        palette = (ImageView)activity.findViewById(R.id.palette);
        eraser = (ImageView)activity.findViewById(R.id.eraser);
        initListView();
        setButtonListeners();
        initListViewVisibilityListener();
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

    public void setButtonListeners(){
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joint_glasses_Movement();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextInputPrompt();
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
                    palette.bringToFront();
                    eraser.bringToFront();
                    palette.setVisibility(View.VISIBLE);
                    eraser.setVisibility(View.VISIBLE);
                    addDrawingMode();
                    addComponents();
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
                        .show(activity, COLOR_DIALOG_FOR_PENCIL);
            }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePainting();
            }
        });

    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

    public void initSaveOnClickListener(){
        removeComponents();
        View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
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



    private void store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ThugLifeCreator";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaScannerConnection.scanFile(activity, new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeComponents(){
        save.setVisibility(View.GONE);
        txt.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        palette.setVisibility(View.GONE);
        eraser.setVisibility(View.GONE);
        pencil.setVisibility(View.GONE);
        musicButton.setVisibility(View.GONE);
        removeBorders();
        listViewSwitch.setVisibility(View.GONE);
    }

    int counter = 2;
    public void addSticker(int drawable,String ownerId){
        Resources r = context.getResources();

        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
        StickerImageView sticker = new StickerImageView(context);
        sticker.setImageBitmap(
                ProcessImage.decodeSampledBitmapFromResource(context.getResources(), drawable, dim, dim));
        sticker.setOwnerId(ownerId);
        removeBorders();
        layout.addView(sticker);
        sticker.setControlItemsHidden(false);
        if(counter > 0) {
            if (ownerId.equals("joint")) {
                Toast.makeText(context, "add some glassses, then press the star m8 ;)", Toast.LENGTH_LONG).show();
            } else if (ownerId.equals("glasses")) {
                Toast.makeText(context, "add the joint, press the star, blaze 420 ( ͡° ͜ʖ ͡°)", Toast.LENGTH_LONG).show();
            }
            counter--;
        }
        if(checkJoint_Glasses()){
            play.setVisibility(View.VISIBLE);
            play.bringToFront();
        }
        if(isPremium) {
            musicButton.setVisibility(View.VISIBLE);
            musicButton.bringToFront();
        }
        setSounds(ownerId);
    }


    public boolean checkJoint_Glasses(){
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

    public void removeBorders(){
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
        ObjectAnimator animX = ObjectAnimator.ofFloat(snoopGif, "x", 1600);
        ObjectAnimator animY = ObjectAnimator.ofFloat(snoopGif, "y", ySnoopOriginal);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(20000);
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
    }

    public void clearBordersListener(){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBorders();
            }
        });
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

    String textInput = "";
    public void showTextInputPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        builder.setTitle("Type some dank text");
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textInput = input.getText().toString();
                addText();
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
                        .show(activity, COLOR_DIALOG);
                //dialog.cancel();
            }
        });

        builder.show();
    }

    public void addComponents(){
        save.bringToFront();
        listViewSwitch.bringToFront();
        save.setVisibility(View.VISIBLE);
        listViewSwitch.setVisibility(View.VISIBLE);
        if(joint.getParent() == layout && glasses.getParent() == layout){
            play.setVisibility(View.VISIBLE);
            play.bringToFront();
        }
        if(isPremium){
            musicButton.setVisibility(View.VISIBLE);
            musicButton.bringToFront();
            pencil.setVisibility(View.VISIBLE);
            pencil.bringToFront();
            if(isDrawingMode) {
                palette.setVisibility(View.VISIBLE);
                eraser.setVisibility(View.VISIBLE);
                palette.bringToFront();
                eraser.bringToFront();
            }
        }

        txt.setVisibility(View.VISIBLE);
        txt.bringToFront();
    }

    public void addText(){
        removeBorders();
        text = new StickerTextView(activity,true);
        text.setOwnerId("text");
        text.setText(textInput);
        layout.addView(text);
    }

    boolean wasSnoopInit = false;

    public void joint_glasses_Movement(){
        if(!wasSnoopInit) {
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
    public void removePainting(){
        ViewGroup viewGroup = (ViewGroup) layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.clearCanvas();
            }
        }
    }

    public void disableDrawingMode(){
        ViewGroup viewGroup = (ViewGroup) layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.stopOnDrawCall();
            }
        }
        txt.bringToFront();
        listViewSwitch.bringToFront();
        save.bringToFront();
        musicButton.bringToFront();
        if(checkJoint_Glasses()){
            play.bringToFront();
        }
        pencil.bringToFront();
    }


    public void addDrawingMode(){
        hideListView();
        removeBorders();
        PaintView painting = new PaintView(context);
        Resources r = context.getResources();
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, r.getDisplayMetrics());
        painting.setPadding(0,0,0,dim);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        //params.bottomMargin = dim;
        layout.addView(painting,params);
        painting.bringToFront();
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
        mPlayer = MediaPlayer.create(activity, R.raw.gta_sa);
        switch (i) {
            case 0 :
                mPlayer = MediaPlayer.create(activity, R.raw.fuck_da_police);
                break;
            case 1 :
                mPlayer = MediaPlayer.create(activity, R.raw.gta_sa);
                break;
            case 2 :
                mPlayer = MediaPlayer.create(activity, R.raw.madafaka);
                break;
        }
        mPlayer.start();
    }


    public void changePencilColor(int color){
        ViewGroup viewGroup =  layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof PaintView) {
                PaintView child = (PaintView) viewGroup.getChildAt(i);
                child.changeColor(color);
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!changedOnce) {
            int[] snoopLocation = new int[2];
            snoopGif.getLocationOnScreen(snoopLocation);
            ySnoopOriginal = snoopLocation[1];
            xSnoopOriginal = snoopLocation[0];
            changedOnce = true;
        }
    }

    @Override
    public void pausePlayer() {
        if(mPlayer != null){
            mPlayer.stop();
        }
    }

    @Override
    public void setPlayButtonVisibility() {
        if(play != null) {
            play.setVisibility(View.GONE);
        }
    }
}
