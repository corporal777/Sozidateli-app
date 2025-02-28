package com.example.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataArgsSearchType(
        var array: MutableList<SearchTypeEvent>,
        var isOrganization: Boolean
) : Parcelable