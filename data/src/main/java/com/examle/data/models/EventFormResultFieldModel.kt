package com.examle.data.models

import android.os.Parcelable
import com.google.gson.JsonElement
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class EventFormResultFieldModel(
    val id: Int? = null,
    val value: @RawValue JsonElement? = null,
    var fields : @RawValue JsonElement? = null
) : Parcelable