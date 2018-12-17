package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var id: String = "",
        var name: String = "",
        var image: String? = "",
        var status: String? = null,
        var currentEvent: Event? = null,
        var email: String? = null,
        var phone: String? = null,
        var birthday: Long = 0,
        var city: String? = null,
        var sn: String? = null,
        var educations: String? = null,
        var info: String? = "",
        var subscribed: Boolean = false,
        var startEducate: Long = 0,
        var endEducate: Long = 0,
        var speciality: String = "",
        var institution: String = ""
) : Parcelable