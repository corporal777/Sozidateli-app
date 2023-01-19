package com.example.util.rxtakephoto.rx_image_picker.scheduler

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class RxImagePickerTestSchedulers : IRxImagePickerSchedulers {

    override fun ui(): Scheduler {
        return Schedulers.io()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}