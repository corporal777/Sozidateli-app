package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventInfo(
        /*val event: EventNew,
        /*@SerializedName("place")
        val places: List<Place>,*/
        val partners: List<PartnerModel>,
        val pages: List<PageModel>,
        @SerializedName("user_registration")
        val userRegistration: UserRegisterModel?,
        @SerializedName("value")
        val ratingValue: Int?,
        @SerializedName("fields")
        val responseFields: List<EventFormModel?>?*/
        val event: EventData,
        @SerializedName("place")
        val places: List<Place>,
        val partners: List<EventParther>,
        val pages: List<EventPage>,
        @SerializedName("user_registration")
        val userRegistration: EventUserRegistration?,
        @SerializedName("value")
        val ratingValue: Int?,
        @SerializedName("fields")
        val responseFields: List<EventRegisterResponseField?>?
)