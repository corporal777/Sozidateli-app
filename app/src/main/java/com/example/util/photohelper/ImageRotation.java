package com.example.util.photohelper;

import android.net.Uri;

/**
 * Created by stanl on 20.12.2017.
 */

class ImageRotation {
    private final String path;
    private final Uri uri;
    private final int rotation;

    ImageRotation(String path, Uri uri, int rotation) {
        this.path = path;
        this.uri = uri;
        this.rotation = rotation;
    }

    String getPath() {
        return path;
    }

    Uri getUri() {
        return uri;
    }

    int getRotation() {
        return rotation;
    }
}
