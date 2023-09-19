package com.example.ui.gallery.cropImage.cropHelper.animation;

public interface SimpleValueAnimatorListener {
    void onAnimationStarted();

    void onAnimationUpdated(float scale);

    void onAnimationFinished();
}