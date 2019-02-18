package com.example.util.photohelper

import android.content.Context
import com.qingmei2.rximagepicker.entity.sources.Camera
import com.qingmei2.rximagepicker.entity.sources.Gallery
import com.qingmei2.rximagepicker.entity.Result
import io.reactivex.Flowable
import io.reactivex.Observable

interface MyImagePicker{
    @Gallery    // open gallery
    fun openGallery(context: Context): Observable<Result>

    @Camera     // take photos
    fun openCamera(context: Context): Observable<Result>
}