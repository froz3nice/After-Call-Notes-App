package com.braz.prod.DankMemeStickers.util.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.braz.prod.DankMemeStickers.R;

public class CustomProgressBar {

    public static CustomProgressBar customProgress = null;
    private Dialog mDialog;
    TextView progressText;
    ProgressBar mProgressBar;

    public static CustomProgressBar getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgressBar();
        }
        return customProgress;
    }

    public void showProgress(Context context, String message, boolean cancelable) {
        mDialog = new Dialog(context);
        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.custom_progress_bar);
        mProgressBar = (ProgressBar) mDialog.findViewById(R.id.progress_bar);
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        progressText = (TextView) mDialog.findViewById(R.id.progress_text);
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        // you can change or add this line according to your need
        mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void setProgress(float status) {
        mProgressBar.setProgress((int)status);
    }

    public void changeText(float percent) {
        Log.d("OnProgress ",String.valueOf((int)percent));
        progressText.setText(String.format("%d",(int)percent)+ " %");
    }

    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }
}