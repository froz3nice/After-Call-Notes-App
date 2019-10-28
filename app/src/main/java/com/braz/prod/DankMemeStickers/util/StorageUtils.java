package com.braz.prod.DankMemeStickers.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;

import com.braz.prod.DankMemeStickers.Interfaces.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.braz.prod.DankMemeStickers.util.Utils.getPath;

public class StorageUtils {
    public static void store(Bitmap bm, String fileName, Callback activity) {
        final String dirPath = getPath();
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 95, fOut);
            fOut.flush();
            fOut.close();
            activity.onFinished(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createImageFromBitmap(Context context,Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static void deleteFile(String path, Context context) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path},
                    null, (path1, uri) -> {
                        if (uri != null) {
                            context.getContentResolver().delete(uri, null, null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getTempMp3Path() {
        return getPath() + "/temp_song.mp3";
    }

    public static void writeMp3ToStorage(Context context, int res) {
        try {
            InputStream in = context.getResources().openRawResource(res);
            FileOutputStream out = null;
            out = new FileOutputStream(getTempMp3Path());
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
