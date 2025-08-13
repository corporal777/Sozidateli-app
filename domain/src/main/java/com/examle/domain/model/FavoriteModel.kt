package com.examle.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoriteModel(
    val id: Long? = null,
    val user: Int? = null
) : Parcelable