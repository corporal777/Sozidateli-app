package com.example.data.models

import com.google.gson.annotations.SerializedName

data class Organization(
        val id: String,
        val logo: String?,
        @SerializedName("bg_image")
        val background: String?,
        val name: String,
        @SerializedName("small_description")
        val descriptionShort: String?,
        @SerializedName("full_description")
        val descriptionFull: String?,
        @SerializedName("addr")
        val address: String?,
        val emails: List<EmailAffiliation>?,
        @SerializedName("web")
        val webLinks: List<String>?,
        @SerializedName("social_links")
        val socialLinks: List<String>?,
        @SerializedName("phone")
        val phones: List<PhoneAffiliation>?,
        @SerializedName("is_user_subscribed")
        val isSubscribed: Boolean?,
        @SerializedName("total_members")
        val totalMembers: Int,
        @SerializedName("total_events")
        val totalEvents: Int
)