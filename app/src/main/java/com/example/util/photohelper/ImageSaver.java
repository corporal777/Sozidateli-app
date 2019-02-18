package com.example.util.photohelper;



import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by stanl on 20.12.2017.
 */

class ImageSaver {

    private static ImageSaver instance;

    public static synchronized ImageSaver getInstance() {
        if (instance == null) {
            instance = new ImageSaver();
        }
        return instance;
    }

    private Map<String, ImageRotation> imageRotationMap = new HashMap<>();

    void saveImage(@NonNull String key, ImageRotation imageRotation) {
        imageRotationMap.put(key, imageRotation);
    }

    @Nullable
    ImageRotation getImageForKey(@NonNull String key) {
        return imageRotationMap.get(key);
    }

    ImageRotation removeImageForKey(@NonNull String key) {
        return imageRotationMap.remove(key);
    }
}
