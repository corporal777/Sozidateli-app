package com.example.data.models.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialRoles(
        var id: Int = -1,
        var begin: String? = null,
        var end: String? = null,
        var organization: String? = null,
        var specialty: String? = null,
        var position: String? = null,
        var description: String? = null,
        var role: String? = null,
        var name: String? = null
) : Parcelable {

    companion object {
        const val FIELD_BEGIN = "begin"
        const val FIELD_END = "end"
        const val FIELD_ORGANIZATION = "organization"
        const val FIELD_SPECIALITY = "specialty"
    }
}