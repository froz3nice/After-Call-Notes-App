package com.braz.prod.DankMemeStickers.Activities.Play;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.braz.prod.DankMemeStickers.Activities.ActivityInterfaces.ActivityCallback;
import com.braz.prod.DankMemeStickers.Models.ImageModel;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerImageView;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerView;
import com.braz.prod.DankMemeStickers.util.ImageProcessingUtils;
import com.facebook.common.util.UriUtil;

import java.io.File;

public class PlayFunctions {
    public static void removeBorders(ViewGroup layout, ActivityCallback callback) {
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                child.setControlItemsHidden(true);
            }
        }
        callback.hideListView();
    }
    public static void addSticker(ImageModel image, Context context, ViewGroup layout, ActivityCallback callback) {
        Resources r = context.getResources();
        StickerImageView sticker;
        if(image.getType() == ImageModel.Type.gif){
            sticker = new StickerImageView(context,callback,true);
            sticker.setGif(image.getRes());
        }else{
            sticker = new StickerImageView(context,callback,false);
            int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
            sticker.setImageBitmap(ImageProcessingUtils.decodeSampledBitmapFromResource(context.getResources(), image.getRes(), dim, dim));
        }

        sticker.setOwnerId(image.getOwnerId());
        sticker.setControlItemsHidden(false);
        removeBorders(layout,callback);
        layout.addView(sticker);
        callback.onStickerAdded();
    }

    public static void changeStickersVisibility(ViewGroup layout, int visibility) {
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                child.setVisibility(visibility);
            }
        }
    }

    public static Pair<StickerImageView,StickerImageView> initJoint_Glasses(ViewGroup layout) {
        StickerImageView joint = null;
        StickerImageView glasses = null;
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                if (child.getOwnerId().equals("joint")) {
                    joint = (StickerImageView) viewGroup.getChildAt(i);
                }
                if (child.getOwnerId().equals("glasses")) {
                    glasses = (StickerImageView) viewGroup.getChildAt(i);
                }
            }
        }
        return new Pair<>(joint,glasses);
    }


    public static void shareIt(Context activity, String file,String path,String type) {
        //sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        Uri uri = Uri.fromFile(new File(path, file));
        if(path == ""){
            uri = Uri.fromFile(new File(file));
        }
        sharingIntent.setType(type);

        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public static Uri getDrawableUri(int res){
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(res))
                .build();
        return uri;
    }

    public static boolean checkJointGlassesExist(ConstraintLayout layout) {
        ViewGroup viewGroup = layout;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof StickerView) {
                StickerView child = (StickerView) viewGroup.getChildAt(i);
                if (child.getOwnerId().equals("joint")) {
                    return true;
                }
                if (child.getOwnerId().equals("glasses")) {
                    return true;
                }
            }
        }
        return false;
    }
}
