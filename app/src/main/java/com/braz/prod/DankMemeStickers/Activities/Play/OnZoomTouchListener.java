package com.braz.prod.DankMemeStickers.Activities.Play;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.braz.prod.DankMemeStickers.Activities.Play.Interfaces.ViewInteractionsListener;
import com.braz.prod.DankMemeStickers.Activities.Play.Interfaces.ZoomCallback;

public class OnZoomTouchListener implements View.OnTouchListener {

    private ZoomCallback callback;
    private Matrix originalMatrix;
    private int imageWidth;
    private Matrix matrix;
    private Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF mid = new PointF();
    double oldDist = 1f;
    private String TAG = "TAG";
    double scale = 1f, lastScale = 1f;
    GestureDetector gestureDetector;
    ViewInteractionsListener listener;
    public OnZoomTouchListener(Context context, Matrix matrix, int imageWidth, ZoomCallback callback, ViewInteractionsListener listener ) {
        this.matrix = matrix;
        originalMatrix = new Matrix(matrix);
        this.imageWidth = imageWidth;
        this.callback = callback;
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                listener.onStopRecording();
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                listener.onStopRecording();
                return super.onSingleTapUp(e);
            }
        });
    }

    public void restoreZoom(ImageView v) {
        matrix = new Matrix(originalMatrix);
        v.setImageMatrix(matrix);
        v.invalidate();
    }
    private boolean isZoomable = true;
    public void disableZoom(){
        isZoomable = false;
    }

    public void enableZoom(){
        isZoomable = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        gestureDetector.onTouchEvent(event);
        listener.onRemoveBorders();
        if(isZoomable) return true;

        ImageView view = (ImageView) v;
        dumpEvent(event);
        float[] values = new float[9];
        matrix.getValues(values);
        float width = values[Matrix.MSCALE_X] * imageWidth;
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d("zoomValue",scale+"");
                if(lastScale != scale)
                    scale = lastScale * scale;
                lastScale = scale;

                if (width < imageWidth) {
                    restoreZoom((ImageView) v);
                    scale = 1;
                }

                Log.d("scale up value", scale+"");
                //restoreZoom((ImageView) v);
                callback.onZoomed((float) scale,mid.x,mid.y);
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    double newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale((float) scale, (float) scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }


    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    /**
     * Determine the space between the first two fingers
     */
    private double spacing(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        return Math.sqrt((x * x + y * y));
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
