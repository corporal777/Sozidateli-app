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
        @SerializedName("bg_color")
        val backgroundColor: String?,
        @SerializedName("bg_img")
        val backgroundImage: String?,
        @SerializedName("conference_start")
        val conferenceStart: String?,
        @SerializedName("conference_finish")
        val conferenceFinish: String?,
        @SerializedName("conference_first_activity_start")
        val conferenceFirstActivityStart: String?,
        @SerializedName("conference_last_activity_finish")
        val conferenceLastActivityFinish: String?,
        @SerializedName("registration_start")
        val registrationStart: String?,
        @SerializedName("registration_finish")
        val registrationFinish: String?,
        @SerializedName("user_registration")
        val userRegistration: Event.RegistrationStatus?,
        val status: Event.Status?,
        val address: String?,
        @SerializedName("short_address")
        val shortAddress: String?,
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
        val ratingSubtitle: String?,
        @SerializedName("rating_start_at")
        val ratingStartAt: Int?,
        @SerializedName("rating_files")
        val ratingFiles: List<EventRatingFile>?,
        val format: EventFormat?,
        @SerializedName("format_custom")
        val formatCustom: String?,
        @SerializedName("is_favorite")
        var isFavorite: Boolean?,
        @SerializedName("conference_requests_receiving_date_end")
        val conferenceRegistrationFinishDate: String?,
        @SerializedName("conference_requests_receiving_closed")
        val conferenceRegistrationClosed: Boolean
)

fun EventData.createMapInfo(): MapInfo? {
    val lat = placeLat
    val lon = placeLon
    val title = placeHowToGetTitle
    val description = placeHowToGet

    return if (lat == null || lon == null) null
    else MapInfo(lat, lon, title, description)
}

fun EventData.takeFormat(): EventFormat? {
    return format ?: formatCustom?.let { customFormat ->
        EventFormat(name = customFormat)
    }
}