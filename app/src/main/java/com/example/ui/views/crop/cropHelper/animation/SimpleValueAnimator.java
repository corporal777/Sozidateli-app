package com.example.ui.views.crop.cropHelper.animation;

@SuppressWarnings("unused") public interface SimpleValueAnimator {
    void startAnimation(long duration);

    void cancelAnimation();

    boolean isAnimationStarted();

    void addAnimatorListener(SimpleValueAnimatorListener animatorListener);
}
