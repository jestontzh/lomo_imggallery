package com.example.lomoimagegallery;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnSwipeTouchListener implements View.OnTouchListener {

    private Context context;

    private final GestureDetector gestureDetector;
    public OnSwipeTouchListener(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    abstract void onSwipeLeft();

    abstract void onSwipeRight();

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 500;
        private static final int SWIPE_VELOCITY_THRESHOLD = 500;
        private static final String TAG = "OnSwipeTouchListener";

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = event2.getX() - event1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        Log.i(TAG, "Swiped Right");
                        onSwipeRight();
                    } else {
                        Log.i(TAG, "Swiped Left");
                        onSwipeLeft();
                    }
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
