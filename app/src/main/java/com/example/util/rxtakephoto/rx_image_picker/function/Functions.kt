package com.example.util.rxtakephoto.rx_image_picker.function

import android.net.Uri
import com.example.util.rxtakephoto.rx_image_picker.entity.Result
import kotlin.jvm.JvmName

fun parseResultNoExtraData(uri: Uri): Result {
    return Result.Builder(uri).build()
}