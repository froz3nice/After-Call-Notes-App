package com.braz.prod.DankMemeStickers;

import android.app.Application;
import android.os.StrictMode;

import com.bumptech.glide.request.target.ViewTarget;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by juseris on 6/1/2017.
 */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);//tag
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
