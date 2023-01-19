package com.example.util.rxtakephoto.rx_image_picker.ui

import androidx.annotation.IdRes
import io.reactivex.Observable
import com.example.util.rxtakephoto.rx_image_picker.entity.Result

interface ICustomPickerView {

    fun display(fragmentActivity: androidx.fragment.app.FragmentActivity,
                @IdRes viewContainer: Int,
                configuration: ICustomPickerConfiguration?)

    fun pickImage(): Observable<Result>
}