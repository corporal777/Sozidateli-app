package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class EventNewModel(
        val data: List<EventNew?>? = null,
        @SerializedName("totalCount")
        val totalCount: Int? = null
)

data class EventNew(
        val id: Int? = null,
        val name: String? = null,
        val code: String? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val description: String? = null,
        @SerializedName("holdingDate")
        val holdingDate: DateModel? = null,
        @SerializedName("requestsApply")
        val requestsApply: RequestApplyModel? = null,
        val organization: Int? = null,
        val regularity: EventRegularity? = null,
        val topic: TopicModel? = null,
        @SerializedName("backgroundColor")
        val backgroundColor: BackgroundColorModel? = null,
        val status: EventStatusModel? = null,
        val state: EventStateModel? = null,
        val format: EventFormatModel? = null,
        @SerializedName("targetedAudience")
        val targetedAudience: List<Int>? = null,
        val phone: List<EventPhoneModel>? = null,
        val email: List<EventPhoneModel>? = null,
        val site: List<EventPhoneModel>? = null,
        @SerializedName("socialLink")
        val socialLink: List<EventPhoneModel>? = null,
        @SerializedName("userAgreement")
        val userAgreement: EventUserAgreement? = null,
        val image: EventUserAgreement? = null,
        val address: NewUserAddress? = null,
        val binds: EventBindsModel? = null
) {

        companion object {
                const val EVENT_SORT_FIELD = "sortField"
                const val EVENT_SORT_TYPE = "sortType"
                const val EVENT_LIMIT = "limit"
                const val EVENT_OFFSET = "offset"
                const val EVENT_BINDS = "binds"
                const val EVENT_ID = "id"
                const val EVENT_ORGANIZATION = "organization"
        }
}

@Parcelize
data class EventBindsModel(
        val organization: OrganizationNew? = null,
        val activity: List<EventActivityModel>? = null
): Parcelable {

        fun getFirstActionStartDate(): String? {
                return if (activity.isNullOrEmpty())
                        null
                else
                        activity.first().holdingDate?.from
        }
}

@Parcelize
data class EventActivityModel(
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("createdBy")
        val createdBy: Int? = null,
        val event: Int? = null,
        val title: String? = null,
        val description: String? = null,
        @SerializedName("holdingDate")
        val holdingDate: DateModel? = null/*,
        val auditorium: Any? = null,
        val member: List<Any>? = null,
        val tag: List<Any>? = null*/
): Parcelable

@Parcelize
data class EventUserAgreement(
        val name: String? = null,
        @SerializedName("mimeType")
        val mimeType: String? = null,
        val size: Long? = null,
        val uri: String? = null
): Parcelable

@Parcelize
data class EventPhoneModel(
        val value: String? = null,
        val title: String? = null
): Parcelable {

        fun getAffiliationString(): String {
                return if (!title.isNullOrBlank()) "${title}: $value" else value?: ""
        }
}

data class EventFormatModel(
        val value: Int? = null,
        val custom: String? = null
)

data class EventStateModel(
        @SerializedName("isRunning")
        val isRunning: Boolean,
        @SerializedName("isFinished")
        val isFinished: Boolean,
        @SerializedName("isPublic")
        val isPublic: Boolean? = null,
        @SerializedName("isHidden")
        val isHidden: Boolean? = null,
        val rating: EventRatingModel? = null,
        val registration: EventRatingModel? = null
)

data class EventRatingModel(
        @SerializedName("isAvailable")
        val isAvailable: Boolean? = null,
        @SerializedName("formEnabled")
        val formEnabled: Boolean? = null,
        @SerializedName("askDelay")
        val askDelay: Int? = null,
        @SerializedName("approvingMode")
        val approvingMode: String? = null
)

@Parcelize
data class EventStatusModel(
        val value: Event.Status,
        val changed: String? = null,
        val comments: String? = null
): Parcelable

@Parcelize
data class BackgroundColorModel(
        @SerializedName("isEnabled")
        val isEnabled: Boolean,
        val value: String? = null
): Parcelable

@Parcelize
data class DateModel(
        val from: String? = null,
        val to: String? = null
): Parcelable

data class RequestApplyModel(
        @SerializedName("dateLimit")
        val dateLimit: String? = null,
        @SerializedName("isClosed")
        val isClosed: Int? = null
)