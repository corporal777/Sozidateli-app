package com.examle.data.models

import com.examle.data.models.event.EventResponse
import com.examle.data.models.event.PageModel
import com.examle.data.models.event.PartnerModel
import com.examle.data.models.event.UserRegisterModel
import com.google.gson.annotations.SerializedName

data class EventInfo(
    val event: EventResponse,
    /*@SerializedName("place")
    val places: List<Place>,*/
        val partners: List<PartnerModel>,
    val pages: List<PageModel>,
    @SerializedName("user_registration")
        val userRegistration: UserRegisterModel?,
    @SerializedName("value")
        val ratingValue: Int?,
    @SerializedName("fields")
        val responseFields: List<EventFormModel?>?
    /*val event: EventData,
    @SerializedName("place")
    val places: List<Place>,
    val partners: List<EventParther>,
    val pages: List<EventPage>,
    @SerializedName("user_registration")
    val userRegistration: EventUserRegistration?,
    @SerializedName("value")
    val ratingValue: Int?,
    @SerializedName("fields")
    val responseFields: List<EventRegisterResponseField?>?*/
)