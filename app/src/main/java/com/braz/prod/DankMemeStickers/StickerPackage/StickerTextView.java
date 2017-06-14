package com.braz.prod.DankMemeStickers.StickerPackage;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.braz.prod.DankMemeStickers.util.AutoResizeTextView;

/**
 * Created by juseris on 6/2/2017.
 */

public class StickerTextView extends StickerView{
    private AutoResizeTextView tv_main;
    public StickerTextView(Context context) {
        super(context);
    }
    public StickerTextView(Context context,boolean isText) {
        super(context,isText);
        isTextView = isText;
    }

    public StickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public StickerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getMainView() {
        if(tv_main != null)
            return tv_main;

        tv_main = new AutoResizeTextView(getContext());
        //tv_main.setTextSize(22);
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("color",0) != 0) {
            tv_main.setTextColor(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("color",0));
        }else{
            tv_main.setTextColor(Color.WHITE);
        }
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt("color",0).apply();
        tv_main.setGravity(Gravity.CENTER);
        tv_main.setTextSize(400);
        tv_main.setShadowLayer(4, 0, 0, Color.BLACK);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        tv_main.setLayoutParams(params);
        if(getImageViewFlip()!=null)
            getImageViewFlip().setVisibility(View.GONE);
        return tv_main;
    }

    public void setText(String text){
        if(tv_main!=null)
            tv_main.setText(text);
    }

    public String getText(){
        if(tv_main!=null)
            return tv_main.getText().toString();

        return null;
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    @Override
    protected void onScaling(boolean scaleUp) {
        super.onScaling(scaleUp);
    }
}