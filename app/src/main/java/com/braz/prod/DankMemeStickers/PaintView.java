package com.braz.prod.DankMemeStickers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by juseris on 6/8/2017.
 */
public class PaintView extends View {

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;
    private final EmbossMaskFilter mEmboss;
    private final BlurMaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint mPaint;
    private boolean callOnDraw;
    private Integer lineID;
    private Integer rememberLineId;

    public PaintView(Context c) {
        super(c);
        context = c;
        callOnDraw = true;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        this.setDrawingCacheEnabled(true);
    }

    public void changeColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    public void clearCanvas(){
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    public void stopOnDrawCall(){
        //callOnDraw = false;
        mPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(callOnDraw) {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        //showDialog();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        Intent intent = new Intent();
        intent.setAction("com.invisible.components");
        getContext().sendBroadcast(intent);

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        //mPaint.setMaskFilter(null);
        Intent intent = new Intent();
        intent.setAction("com.visible.components");
        getContext().sendBroadcast(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(callOnDraw) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        }else {
            Intent intent = new Intent();
            intent.setAction("com.remove.borders");
            getContext().sendBroadcast(intent);
        }
        return true;
    }
}


      /*  @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            mPaint.setXfermode(null);
            mPaint.setAlpha(0xFF);

            switch (item.getItemId()) {
                case COLOR_MENU_ID:
                    new ColorPickerDialog(this, this, mPaint.getColor()).show();
                    return true;
                case ERASE_MENU_ID:
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    mPaint.setAlpha(0x80);
                    return true;
                case SRCATOP_MENU_ID:
                    mPaint.setXfermode(new PorterDuffXfermode(
                            PorterDuff.Mode.SRC_ATOP));
                    mPaint.setAlpha(0x80);
                    return true;
            }
        }*/

