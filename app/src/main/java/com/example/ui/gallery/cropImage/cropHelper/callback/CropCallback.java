package com.example.ui.gallery.cropImage.cropHelper.callback;

import android.graphics.Bitmap;

public interface CropCallback extends Callback {
    void onSuccess(Bitmap cropped);
}