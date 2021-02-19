package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhoneAffiliation(
        val phone: String,
        val affiliation: String?,
        val additional: String?
) : Parcelable {

    fun getAffiliationString(): String {
        val add = if (!additional.isNullOrBlank()) additional else ""
        return if (!affiliation.isNullOrBlank()) "${affiliation}: $phone $add" else "$phone $add"
    }
}