package com.example.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class User(
        @PrimaryKey
        var id: String = "",
        var name: String = "",
        var image: String? = "",
        var status: String? = null,
        @Ignore
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
): Parcelable