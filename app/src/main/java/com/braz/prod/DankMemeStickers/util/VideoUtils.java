package com.braz.prod.DankMemeStickers.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT;

public class VideoUtils {
    public static Integer getTotalVideoMillis(Context context, Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);
        String mVideoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Integer timeInMillis = Integer.parseInt(mVideoDuration);
        return timeInMillis;
    }

    public static Integer getVideoHeight(String path) throws IllegalArgumentException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return Integer.valueOf(retriever.extractMetadata(METADATA_KEY_VIDEO_HEIGHT));
    }

    public static String getTimeString(Integer totalMillis, boolean isFFmpedValue) {
        long hours = totalMillis / 3600000;
        long seconds = totalMillis / 1000;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis);
        long second = (seconds > 60) ? seconds - (60 * minutes) : seconds;

        Log.d("millis total", String.valueOf(totalMillis));
        Log.d("sec total", String.valueOf(seconds));

        if (hours > 0 || isFFmpedValue) {
            long millis = totalMillis;
            if (totalMillis > 1000)
                millis = totalMillis % (seconds * 1000);

            return String.format(Locale.getDefault(), "%02d:%02d:%02d.%d",
                    hours,
                    minutes,
                    second,
                    millis);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, second);
        }
    }
}
