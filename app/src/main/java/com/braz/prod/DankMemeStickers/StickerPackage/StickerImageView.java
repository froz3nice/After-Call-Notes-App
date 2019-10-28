package com.braz.prod.DankMemeStickers.StickerPackage;

/**
 * Created by juseris on 6/1/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.braz.prod.DankMemeStickers.Activities.ActivityInterfaces.ActivityCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import static com.braz.prod.DankMemeStickers.Activities.Play.PlayFunctions.getDrawableUri;


public class StickerImageView extends StickerView {

    private ImageView iv_main;
    private SimpleDraweeView gif;

    public StickerImageView(Context context, ActivityCallback callback, boolean isGif) {
        super(context, callback, isGif);
        this.isGif = isGif;
    }

    public StickerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View getMainView() {
        if (this.iv_main == null) {
            if (isGif) {
                this.iv_main = new SimpleDraweeView(getContext());
            } else {
                this.iv_main = new ImageView(getContext());
                this.iv_main.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        return iv_main;
    }

    public void setImageBitmap(Bitmap bmp) {
        this.iv_main.setImageBitmap(bmp);
    }

    public void setGif(int res) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(getDrawableUri(res))
                .setAutoPlayAnimations(true)
                .build();
        if(iv_main instanceof SimpleDraweeView){
            ((SimpleDraweeView)iv_main).setController(controller);
        }
    }

    public void setImageResource(int res_id) {
        this.iv_main.setImageResource(res_id);
    }

    public void setImageDrawable(Drawable drawable) {
        this.iv_main.setImageDrawable(drawable);
    }

    public Bitmap getImageBitmap() {
        return ((BitmapDrawable) this.iv_main.getDrawable()).getBitmap();
    }

}
