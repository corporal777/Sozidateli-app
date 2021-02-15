package com.example.data.models

import com.google.gson.annotations.SerializedName

data class EventsListModel (
        val data: List<EventModel>? = null,
        @SerializedName("totalCount")
        val totalCount: Int? = null
)

data class EventModel (
        val id: Int,
       val name: String? = null,
        val code: String? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
       val description: String? = null,
       @SerializedName("holdingDate")
       val holdingDate: HoldingDateModel? = null,
       @SerializedName("requestsApply")
       val requestsApply: RequestsApplyModel? = null,
       val organization: Int? = null,
       val format: FormatModel? = null,
       val regularity: Int? = null,
       @SerializedName("targetedAudience")
       val targetedAudience: List<Int>? = null,
       val topic: TopicModel? = null,
       val phone: List<FieldDetails>? = null,
       val email: List<FieldDetails>? = null,
       val site: List<FieldDetails>? = null,
       @SerializedName("socialLink")
       val socialLink: List<FieldDetails>? = null,
       val address: NewUserAddress? = null,
       @SerializedName("backgroundColor")
       val backgroundColor: ColorModel? = null,
       val state: EventState? = null
)

data class EventState (
    val registration: EventStateRegistration? = null,
    val rating: EventStateRating? = null,
    @SerializedName("isPublic")
    val isPublic: Boolean? = null,
    @SerializedName("isHidden")
    val isHidden: Boolean? = null
)

data class EventStateRegistration (
        @SerializedName("isAvailable")
        val isAvailable: Boolean? = null,
        @SerializedName("formEnabled")
        val formEnabled: Boolean? = null,
        @SerializedName("approvingMode")
        val approvingMode: String? = null
)

data class EventStateRating (
        @SerializedName("formEnabled")
        val formEnabled: Boolean? = null,
        @SerializedName("askDelay")
        val askDelay: Int? = null
)

data class ColorModel (
    val value: String? = null,
    @SerializedName("isEnabled")
    val isEnabled: Boolean? = false
)

data class HoldingDateModel (
    val from: String? = null,
    val to: String? = null
)

data class RequestsApplyModel (
    @SerializedName("dateLimit")
    val dateLimit: String? = null
)

data class FormatModel (
    val custom: String? = null
)

data class TopicModel (
        val category: Int? = null,
        val subcategories: List<Int>? = null
)