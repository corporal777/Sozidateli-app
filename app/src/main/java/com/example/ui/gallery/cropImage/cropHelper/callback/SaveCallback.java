package com.example.ui.gallery.cropImage.cropHelper.callback;

import android.net.Uri;

public interface SaveCallback extends Callback {
    void onSuccess(Uri uri);
}
