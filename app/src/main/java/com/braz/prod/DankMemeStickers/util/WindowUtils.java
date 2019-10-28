package com.braz.prod.DankMemeStickers.util;

import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.WindowManager;

public class WindowUtils {
    public static Pair<Integer,Integer> getScreenwh(WindowManager windowManager){
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        return new Pair<Integer,Integer>(metrics.widthPixels,metrics.heightPixels);
    }
}
