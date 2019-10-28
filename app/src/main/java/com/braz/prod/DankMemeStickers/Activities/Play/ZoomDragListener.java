package com.braz.prod.DankMemeStickers.Activities.Play;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.braz.prod.DankMemeStickers.Activities.Play.Interfaces.OnActionUpListener;

public class ZoomDragListener implements View.OnTouchListener {

    public ZoomDragListener(OnActionUpListener listener) {
        this.listener = listener;
    }

    OnActionUpListener listener;
    float dX, dY;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();

                break;
            case MotionEvent.ACTION_UP:
                float centerX = event.getRawX() ;
                float centerY = event.getRawY() - (view.getHeight() / 2);

                Log.d("centerX", event.getRawX() + "");
                Log.d("centerY", event.getRawY() + "");

                listener.onActionUp(centerX, centerY);
            default:
                return false;
        }
        return true;
    }
}
