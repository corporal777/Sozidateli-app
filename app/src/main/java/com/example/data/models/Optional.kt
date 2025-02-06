package com.example.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class Optional<out T>(
    val value: T? = null
)

fun <T> T?.asOptional() = Optional(this)


@Parcelize
data class Argument<out T>(
    val value: @RawValue T
) : Parcelable

fun <T> T.asArgument() = Argument(this)