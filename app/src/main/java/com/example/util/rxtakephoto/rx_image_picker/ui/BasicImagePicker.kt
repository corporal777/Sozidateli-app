package com.example.util.rxtakephoto.rx_image_picker.ui

import android.content.Context
import com.example.util.rxtakephoto.rx_image_picker.entity.sources.Camera
import com.example.util.rxtakephoto.rx_image_picker.entity.sources.Gallery
import io.reactivex.Observable
import com.example.util.rxtakephoto.rx_image_picker.entity.Result

interface BasicImagePicker {

    @Gallery
    fun openGallery(context: Context): Observable<Result>

    @Camera
    fun openCamera(context: Context): Observable<Result>
}