package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class Optional<out T>(
    val value: T? = null
)

fun <T> T?.asOptional() = Optional(this)


@Parcelize
data class Argument<out T>(
    val value: @RawValue T? = null
) : Parcelable

fun <T> T.asArgument() = Argument(this)