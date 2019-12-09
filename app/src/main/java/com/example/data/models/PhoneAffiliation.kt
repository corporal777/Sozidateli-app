package com.example.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhoneAffiliation(
        val phone: String,
        val affiliation: String?
) : Parcelable {

    fun getAffiliationString(): String {
        return if (!affiliation.isNullOrBlank()) "${affiliation}: $phone" else phone
    }
}