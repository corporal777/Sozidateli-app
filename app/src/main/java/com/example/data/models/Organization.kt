package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organization(
        val id: String,
        val logo: String?,
        @SerializedName("bg_image")
        val background: String?,
        @SerializedName("bg_color")
        val backgroundColor: String?,
        val name: String,
        @SerializedName("small_description")
        val descriptionShort: String?,
        @SerializedName("full_description")
        val descriptionFull: String?,
        @SerializedName("addr")
        val address: String?,
        @SerializedName("short_addr")
        val addressShort: String?,
        val emails: List<EmailAffiliation>?,
        @SerializedName("web")
        val webLinks: List<String>?,
        @SerializedName("social_links")
        val socialLinks: List<String>?,
        @SerializedName("phone")
        val phones: List<PhoneAffiliation>?,
        @SerializedName("is_user_subscribed")
        var isSubscribed: Boolean?,
        @SerializedName("total_members")
        val totalMembers: Int,
        @SerializedName("total_events")
        val totalEvents: Int
) : Parcelable {
    companion object {
        const val FIELD_IS_IN_FAVORITE = "is_in_favorite"
    }
}