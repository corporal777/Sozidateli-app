package com.example.data.models

import android.os.Parcelable
import com.example.util.LOREM
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subevent(
        var id: String,
        var time: String,
        var tags: List<String>,
        var isSpeaker: Boolean,
        var isInSchedule: Boolean,
        var title: String,
        var description: String = LOREM.substring(0, 150),
        var location: String = "Главный зал Павильон 1"
) : Parcelable