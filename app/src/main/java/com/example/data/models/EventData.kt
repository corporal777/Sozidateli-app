package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventData(
        @SerializedName("event_id")
        val id: String,
        @SerializedName("event_code")
        val code: String,
        @SerializedName("organization_id")
        val organizationId: String?,
        val organization: Organization?,
        val info: String?,
        val description: String?,
        val name: String,
        val logo: String?,
        @SerializedName("conference_start")
        val conferenceStart: String?,
        @SerializedName("conference_finish")
        val conferenceFinish: String?,
        @SerializedName("registration_start")
        val registrationStart: String?,
        @SerializedName("registration_finish")
        val registrationFinish: String?,
        val status: Event.Status?,
        val address: String?,
        @SerializedName("address_federal")
        val addressFederal: String?,
        val place: String?,
        @SerializedName("place_how2get_title")
        val placeHowToGetTitle: String?,
        @SerializedName("place_how2get")
        val placeHowToGet: String?,
        @SerializedName("place_gps_lat")
        val placeLat: Double?,
        @SerializedName("place_gps_lon")
        val placeLon: Double?,
        val phone: List<PhoneAffiliation>,
        val email: List<EmailAffiliation>,
        val web: List<String>,
        val social: List<String>,
        @SerializedName("rating_headline")
        val ratingHeadline: String?,
        @SerializedName("rating_subtitle")
        val ratingSubtitle: String?
)

fun EventData.createMapInfo(): MapInfo? {
        val lat = placeLat
        val lon = placeLon
        val title = placeHowToGetTitle
        val description = placeHowToGet

        return if (lat == null || lon == null) null
        else MapInfo(lat, lon, title, description)
}