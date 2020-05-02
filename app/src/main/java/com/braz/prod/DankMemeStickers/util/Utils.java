package com.braz.prod.DankMemeStickers.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.braz.prod.DankMemeStickers.Interfaces.Callback;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getPath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath() + "/ThugLifeCreator";
    }

    public static String getTimeStamp() {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());
        return format;
    }

    public static int getScreenWidth(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getDensity(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


    public static void loadImage(Context context, ImageView dawg, Callback callback) {
        try {
            final Bitmap bitmap = BitmapFactory.decodeStream(context
                    .openFileInput("myImage"));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Picasso.get()
                    .load(ImageProcessingUtils.getImageUri(context, bitmap))
                    .fit().centerInside()
                    .into(dawg, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            callback.onFinished("");
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Matrix getMainImageMatrix(ImageView mainImage) {
        Drawable d = mainImage.getDrawable();
        RectF imageRectF = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        RectF viewRectF = new RectF(0, 0, mainImage.getWidth(), mainImage.getHeight());
        Matrix matrix = new Matrix();
        matrix.setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER);
        return matrix;
    }
}
