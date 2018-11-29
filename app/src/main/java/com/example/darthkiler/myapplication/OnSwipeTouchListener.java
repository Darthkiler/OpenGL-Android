package com.example.darthkiler.myapplication;


import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleGestureDetector;

    public OnSwipeTouchListener (Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        scaleGestureDetector =new ScaleGestureDetector(ctx,new ScaleListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //return gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }


        return true;

    }

    private final class ScaleListener extends SimpleOnScaleGestureListener
    {

        float mInitialScaleSpan;
        public boolean onScaleBegin(ScaleGestureDetector d) {

            mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
            return true;
        }
        public boolean onScale(ScaleGestureDetector d) {
            float scale = Math.abs(d.getCurrentSpanX());

            zoom(mInitialScaleSpan-scale);

            return true;
        }
        public void onScaleEnd(ScaleGestureDetector d) {

        }
    }

    private final class GestureListener extends SimpleOnGestureListener {



        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return super.onSingleTapConfirmed(e);
        }



        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                 {
                    if (Math.abs(diffX) > 0 && Math.abs(distanceX) > 0) {
                        if (diffX > 0) {
                            onSwipeRight(distanceX,distanceY,e2.getX(),e1.getY());
                        } else {
                            onSwipeLeft(distanceX  ,distanceY,e2.getX(),e1.getY() );
                        }
                        result = true;
                    }
                }
                /*else if (Math.abs(diffY) > 0 && Math.abs(distanceY) > 100) {
                    if (diffY > 0) {
                        onSwipeBottom(diffY);
                    } else {
                        onSwipeTop(diffY);
                    }
                    result = true;
                }*/
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

    }



    public void onSwipeRight(float diffX,float diffY,float x,float y) {
    }

    public void onSwipeLeft(float diffX,float diffY,float x,float y) {
    }

    public void zoom(float diffX)
    {

    }


}
