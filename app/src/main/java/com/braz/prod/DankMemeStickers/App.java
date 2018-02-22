package com.braz.prod.DankMemeStickers;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.drawee.backends.pipeline.Fresco;


/**
 * Created by juseris on 6/1/2017.
 */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
