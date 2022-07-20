package com.example.util;

import android.animation.Animator;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.R;
import com.example.ui.views.notifications.NotificationsViewContract;

public class TranslateAnimationUtil implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    public TranslateAnimationUtil(Context context, OnScrollingState scrollingState){
        gestureDetector = new GestureDetector(context, new SimpleGestureDetector(scrollingState));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private OnScrollingState scrollingState;

        public SimpleGestureDetector(OnScrollingState scrollingState) {
            this.scrollingState = scrollingState;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollingState.onScrollingOffset(distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public interface OnScrollingState{
        void onScrollingOffset(float dy);
    }
}
