package com.example.ui.views.crop.cropHelper.animation;

public interface SimpleValueAnimatorListener {
    void onAnimationStarted();

    void onAnimationUpdated(float scale);

    void onAnimationFinished();
}