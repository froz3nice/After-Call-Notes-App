package com.braz.prod.DankMemeStickers.util.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.braz.prod.DankMemeStickers.Activities.Gallery.AlbumActivity;
import com.braz.prod.DankMemeStickers.Activities.Gallery.Function;
import com.braz.prod.DankMemeStickers.Activities.Gallery.GaleryPreview;
import com.braz.prod.DankMemeStickers.Activities.Play.PlayActivity;
import com.braz.prod.DankMemeStickers.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

public class CustomProgressBar {

    public static CustomProgressBar customProgress = null;
    private Dialog mDialog;
    TextView progressText;
    ProgressBar mProgressBar;
    AdView mAdView;
    Button viewVideo;
    PlayActivity activity;
    public static CustomProgressBar getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgressBar();
        }
        return customProgress;
    }

    public void showProgress(PlayActivity context, String message, boolean cancelable, boolean isPremium) {
        mDialog = new Dialog(context);
        // no tile for the dialog
        activity = context;
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_bar);
        mProgressBar = (ProgressBar) mDialog.findViewById(R.id.progress_bar);
        viewVideo = (Button) mDialog.findViewById(R.id.view_video);

        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        progressText = (TextView) mDialog.findViewById(R.id.progress_text);
        mAdView = mDialog.findViewById(R.id.adView);
        if (!isPremium) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        // you can change or add this line according to your need
        mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationX", 0, 25, 0);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setStartDelay(500);
        animator.setDuration(1500);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    public void changeText(String progress) {
        progressText.setText(progress);
    }

    public void hideProgress(String f, boolean b) {
        viewVideo.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        progressText.setText("Video Done!");
        viewVideo.setOnClickListener(view -> {
            Intent intent = new Intent(activity, GaleryPreview.class);
            intent.putExtra("path", f);
            intent.putExtra("media_type", "video");
            activity.startActivity(intent);
            trulyHide();
        });
        if (!b) {
            trulyHide();
        }
    }

    void trulyHide() {

        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }
}