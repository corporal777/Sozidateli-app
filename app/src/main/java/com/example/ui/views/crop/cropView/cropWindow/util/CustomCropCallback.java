package com.example.ui.views.crop.cropView.cropWindow.util;

import android.graphics.Bitmap;

public interface CustomCropCallback {
    void onSuccess(Bitmap cropped);
    void onError(Throwable error);
}
