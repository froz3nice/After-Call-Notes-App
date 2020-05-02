package com.braz.prod.DankMemeStickers.Animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.braz.prod.DankMemeStickers.Activities.ActivityInterfaces.ActivityCallback;
import com.braz.prod.DankMemeStickers.Activities.Play.PlayFunctions;
import com.braz.prod.DankMemeStickers.StickerPackage.StickerImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import static com.braz.prod.DankMemeStickers.util.Utils.getScreenHeight;
import static com.braz.prod.DankMemeStickers.util.Utils.getScreenWidth;

public class Animator {

    private final SharedPreferences prefs;
    Context context;
    ConstraintLayout layout;
    WindowManager windowManager;
    int yOriginalJoint = 50 * -1, xOriginalJoint = 50 * -1;
    int yOriginalGlasses = 50 * -1, xOriginalGlasses = 50 * -1;
    float yFixedJoint = 0, xFixedJoint = 0;
    float yFixedGlasses = 0, xFixedGlasses = 0;
    private int xSnoopOriginal = 0;
    private int ySnoopOriginal = 0;
    SimpleDraweeView snoopGif;
    boolean wasSnoopInit = false;

    public Animator(Context context, WindowManager windowManager,
                    ConstraintLayout layout, int yOriginalJoint,
                    int xOriginalJoint, int yOriginalGlasses,
                    int xOriginalGlasses, float yFixedJoint,
                    float xFixedJoint, float yFixedGlasses,
                    float xFixedGlasses, SimpleDraweeView snoopGif,
                    boolean wasSnoopInit) {
        this.windowManager = windowManager;
        this.context = context;
        this.layout = layout;
        this.yOriginalJoint = yOriginalJoint;
        this.xOriginalJoint = xOriginalJoint;
        this.yOriginalGlasses = yOriginalGlasses;
        this.xOriginalGlasses = xOriginalGlasses;
        this.yFixedJoint = yFixedJoint;
        this.xFixedJoint = xFixedJoint;
        this.yFixedGlasses = yFixedGlasses;
        this.xFixedGlasses = xFixedGlasses;
        this.snoopGif = snoopGif;
        this.wasSnoopInit = wasSnoopInit;
        prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSnoopCoordinates(int xSnoopOriginal, int ySnoopOriginal) {
        this.xSnoopOriginal = xSnoopOriginal;
        this.ySnoopOriginal = ySnoopOriginal;
    }

    private void animateSnoopDogg(SimpleDraweeView snoopGif, Integer ySnoopOriginal) {
        if(!prefs.getBoolean("s_snoop",true))snoopGif.setVisibility(View.INVISIBLE);
        ObjectAnimator animX = ObjectAnimator.ofFloat(snoopGif, "x", 1600);
        ObjectAnimator animY = ObjectAnimator.ofFloat(snoopGif, "y", ySnoopOriginal);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(20000);
        animSetXY.playTogether(animX, animY);
        animSetXY.start();
    }

    public void startAllAnimations(ActivityCallback callback, float offset,
                                   float zoomValue, float pivotX, float pivotY, ImageView mainImage) {
        if (!wasSnoopInit) {
            wasSnoopInit = true;
        }
        Log.d("zoomValue anim", zoomValue + "");

        callback.hideListView();
        if(zoomValue != 1f){
            PlayFunctions.changeStickersVisibility(layout,View.INVISIBLE);
        }
        StickerImageView glasses = PlayFunctions.initJoint_Glasses(layout).second;
        StickerImageView joint = PlayFunctions.initJoint_Glasses(layout).first;
        if (glasses != null) {
            glasses.setVisibility(View.INVISIBLE);
            glasses.setX(xOriginalGlasses);
            glasses.setY(yOriginalGlasses);
        }
        if (joint != null) {
            joint.setVisibility(View.INVISIBLE);
            joint.setY(yOriginalJoint);
            joint.setX(xOriginalJoint);
        }

        PlayFunctions.removeBorders(layout, callback);

        snoopGif.setVisibility(View.VISIBLE);
        snoopGif.bringToFront();
        snoopGif.setX(0);
        snoopGif.setX(xSnoopOriginal - 200);
        snoopGif.setY(ySnoopOriginal + offset);

        if (zoomValue == 1f) {
            startJointGlassesAnim(offset, glasses, joint);
        } else {
            zoom(zoomValue, mainImage, pivotX, pivotY, () -> {
                PlayFunctions.changeStickersVisibility(layout,View.VISIBLE);
                startJointGlassesAnim(offset, glasses, joint);
            });
        }
        animateSnoopDogg(snoopGif, ySnoopOriginal);
        callback.playSound();
    }

    private void startJointGlassesAnim(float offset, StickerImageView glasses, StickerImageView joint) {
        xFixedGlasses = PreferenceManager.getDefaultSharedPreferences(context).getFloat("xFixedGlasses", 10);
        yFixedGlasses = PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedGlasses", 10) - offset;
        xFixedJoint = PreferenceManager.getDefaultSharedPreferences(context).getFloat("xFixedJoint", 10);
        yFixedJoint = PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedJoint", 10) - offset;

        Log.d("glassesY", String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedGlasses", 10)));
        Log.d("jointY", String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getFloat("yFixedJoint", 10)));
        Log.d("glassesYAfter", String.valueOf(yFixedGlasses));
        Log.d("jointYAfter", String.valueOf(yFixedJoint));
        if (glasses != null) {
            boolean spin = prefs.getBoolean("s_glasses",true);
            animateObject(glasses, xFixedGlasses, yFixedGlasses, 3000,spin);
        }
        if (joint != null) {
            boolean spin = prefs.getBoolean("s_joint",true);

            animateObject(joint, xFixedJoint, yFixedJoint, 3000,spin);
        }
    }


    public void animateObject(StickerImageView img, float x, float y, long duration, boolean spin) {
        img.setVisibility(View.VISIBLE);
        ObjectAnimator animX = ObjectAnimator.ofFloat(img, "x", x);
        ObjectAnimator animY = ObjectAnimator.ofFloat(img, "y", y);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(img,
                "rotation", img.getRotation(), img.getRotation() + 720f);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.setDuration(duration);


        if (spin) {
            animSetXY.playTogether(animX, animY, rotation);
        } else {
            animSetXY.playTogether(animX, animY);
        }
        animSetXY.start();

    }

    public void zoomDrag(float zoomValue, View mainImage, float pivotX, float pivotY) {
        ScaleAnimation scale = new ScaleAnimation(1f, zoomValue, 1f, zoomValue,
                Animation.RELATIVE_TO_PARENT, pivotX / getScreenWidth(windowManager),
                Animation.RELATIVE_TO_PARENT, pivotY / getScreenHeight(windowManager));
        Log.d("zoomValue", zoomValue + "");
        ;
        Log.d("pivotX", pivotX + "");
        Log.d("pivotY", pivotY + "");

        // /start animation on egg image
        mainImage.startAnimation(scale);
        scale.setDuration(0);
        scale.setFillAfter(true);
    }

    public void zoom(float zoomValue, View mainImage, float pivotX, float pivotY, AnimationListener listener) {
        ScaleAnimation scale = new ScaleAnimation(1f, zoomValue, 1f, zoomValue, pivotX, pivotY);
        Log.d("zoomValue", zoomValue + "");
        ;
        Log.d("pivotX", pivotX + "");
        Log.d("pivotY", pivotY + "");

        // /start animation on egg image
        mainImage.startAnimation(scale);
        scale.setDuration(2000);
        scale.setFillAfter(true);
        scale.setInterpolator(new AccelerateInterpolator());
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onAnimationEnded();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
