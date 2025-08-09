package com.examle.data.models

import android.os.Parcelable
import com.examle.domain.model.CurrentUserRegistrationModel
import com.examle.domain.model.EventRegistrationStateModel
import com.examle.domain.model.EventStateModel
import com.examle.domain.model.EventsStatusModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class EventResponse(
    val id: Int,
    val name: String,
    val code: String? = null,
    val createdDate: String? = null,
    val createdBy: Int? = null,
    val description: String? = null,
    val holdingDate: DateModel? = null,
    val requestsApply: RequestApplyModel? = null,
    val organization: Int? = null,
    val backgroundColor: BackgroundColorModel? = null,
    val status: EventsStatusModel,
    val state: EventStateModel? = null,
    val format: EventFormatModel? = null,
    val phone: List<EventPhoneModel>? = null,
    val email: List<EventPhoneModel>? = null,
    val site: List<EventPhoneModel>? = null,
    val socialLink: List<EventPhoneModel>? = null,
    val userAgreement: EventUserAgreement? = null,
    val image: EventUserAgreement? = null,
    val address: NewUserAddress? = null,
    var binds: EventBindsModel? = null,
    var userRegistration: RegistrationStatus? = null,
    @SerializedName("destination-scheme")
    val destinationScheme: List<String>? = null
) : Parcelable {

    fun getEventFormat(): EventFormat {
        return if (format?.value == null && !format?.custom.isNullOrEmpty())
            EventFormat(name = format?.custom ?: "")
        else EventFormat(binds?.format?.id ?: 0, binds?.format?.name ?: "")
    }

    fun isHasOneActivity(): Boolean {
        return if (binds?.activity.isNullOrEmpty()) true
        else binds?.activity?.size!! < 2
    }

    fun isHasFormResult(): Boolean {
        val formResult = binds?.userFormResult
            ?.firstOrNull { e -> e.form?.type == EventFormModel.Type.PARTICIPATION }
        if (formResult == null) return false
        else if (formResult.result == null) return false
        else return !formResult.result.fields.isNullOrEmpty()
    }

    companion object {

    }

    enum class Status {
        @SerializedName("cancelled")
        CANCELED,

        @SerializedName("preparing")
        PREPARING,

        @SerializedName("awaiting")
        AWAITING,

        @SerializedName("pending")
        PENDING,

        @SerializedName("approved")
        APPROVED,

        @SerializedName("declined")
        DECLINED,

        @SerializedName("banned")
        BANNED,

        @SerializedName("registration")
        REGISTRATION,

        @SerializedName("registrationFinished")
        REGISTRATION_FINISHED,

        @SerializedName("running")
        RUNNING,

        @SerializedName("finished")
        FINISHED,
    }

    enum class RegistrationStatus {
        @SerializedName("pending")
        PENDING,

        @SerializedName("approved")
        APPROVED,

        @SerializedName("declined")
        DECLINED,

        @SerializedName("cancelled")
        CANCELLED
    }
}

fun EventResponse.createMapInfo(): MapInfo? {
    val lat = address?.lat?.takeIf { it in -90.0..90.0 }
    val lon = address?.lon?.takeIf { it in -180.0..180.0 }
    val title = address?.description?.title
    val description = address?.description?.description

    return if (lat == null || lon == null) null
    else MapInfo(lat, lon, title, description)
}

@Parcelize
data class PartnerModel(
    val id: Int? = null,
    val event: Int? = null,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("supportType")
    val supportType: String? = null,
    val logo: ImageModel? = null,
    val image: ImageModel? = null,
    val site: List<PartnersSiteModel>? = null,
    val binds: PartnersBindsModel? = null
) : Parcelable


@Parcelize
data class PartnersBindsModel(
    val event: EventResponse? = null
) : Parcelable

@Parcelize
data class PartnersSiteModel(
    val value: String? = null,
    val title: String? = null
) : Parcelable

@Parcelize
data class PageModel(
    val id: Int? = null,
    val event: Int? = null,
    val name: String? = null,
    val title: String? = null,
    val content: String? = null,
    val files: List<FileModel>? = null
) : Parcelable

@Parcelize
data class EventUserFavorite(
    val id: Long? = null,
    val user: Int? = null
) : Parcelable

@Parcelize
data class EventBindsModel(
    val organization: OrganizationNew? = null,
    val activity: List<EventActivityModel>? = null,
    @SerializedName("user-registration")
    val userRegistration: List<UserRegisterModel>? = null,
    val rights: EventRights? = null,
    val partner: List<PartnerModel>? = null,
    val page: List<PageModel>? = null,
    val member: List<MemberModel>? = null,
    @SerializedName("userFavorite")
    var userFavorite: EventUserFavorite? = null,
    val tag: List<EventTagModel>? = null,
    val auditorium: List<EventAuditoriumModel>? = null,
    val form: List<EventFormModel>? = null,
    @SerializedName("current-user-registration")
    var currentUserRegistration: CurrentUserRegistrationModel? = null,
    @SerializedName("current-user-registration-state")
    var currentUserRegistrationState: EventRegistrationStateModel? = null,
    @SerializedName("user-form-result")
    val userFormResult: List<UserFormResultModel>? = null,
    @SerializedName("eventRegistrationState")
    val eventRegistrationState: EventRegistrationStateModel? = null,
    @SerializedName("userFavoriteActivities")
    val userFavoriteActivities: List<EventActivityModel>? = null,
    @SerializedName("is-user-subscribed")
    val isUserSubscribed: Boolean,
    @SerializedName("event-subscribe")
    val eventSubscribe: EventSubscriptionResponse? = null,
    val format: NewEventFormat? = null,
    @SerializedName("destination-scheme")
    val destinationScheme: List<DestinationSchemeModel>? = null
) : Parcelable {

    fun getForm(): EventFormModel? {
        val formType =
            userFormResult?.firstOrNull { e -> e.form?.type == EventFormModel.Type.PARTICIPATION }
        return formType?.form
    }

    fun getFormResult(): EventFormResultModel? {
        val formResult =
            userFormResult?.firstOrNull { e -> e.form?.type == EventFormModel.Type.PARTICIPATION }
        return formResult?.result
    }
}


@Parcelize
data class UserFormResultModel(
    @SerializedName("formType")
    val form: EventFormModel? = null,
    val result: EventFormResultModel? = null
) : Parcelable


@Parcelize
data class EventFormDraftModel(
    val value: Boolean = false,
    val parent: String? = null
) : Parcelable

@Parcelize
data class EventTagModel(
    val id: Int,
    val event: Int? = null,
    val name: String? = null,
    @SerializedName("isSeparator")
    val isSeparator: Boolean? = null,
    val binds: EventTagBindsModel? = null
) : Parcelable

@Parcelize
data class EventTagBindsModel(
    val event: EventResponse? = null
) : Parcelable

@Parcelize
data class EventAuditoriumModel(
    val id: Int? = null,
    val event: Int? = null,
    val name: String? = null
) : Parcelable

@Parcelize
data class DestinationSchemeModel(
    val id: Int,
    val createdDate: String,
    val event: Int,
    val title: String?,
    val description: String?,
    val file: ImageModel
) : Parcelable {
    fun toPlace(): Place {
        return Place(
            id.toString(),
            event.toString(),
            title,
            file.uri,
            description
        )
    }
}


@Parcelize
data class MemberModel(
    val id: Int? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    @SerializedName("invitedBy")
    val invitedBy: Int? = null,
    val event: Int? = null,
    val user: Int? = null,
    val role: String? = null,
    val status: String? = null,
    val description: String? = null,
    val isLead: Boolean? = null,
    val isNewUser: Boolean? = null,
    val isRegistered: Boolean? = null,
    val name: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val imageUri: ImageModel? = null,
    @SerializedName("organizationAndPosition")
    val organizationAndPosition: String? = null,
    val binds: MemberBindsModel? = null
) : Parcelable {

    val memberNameLastName: String
        get() {
            val nameList = if (name.isNullOrEmpty() || lastName.isNullOrEmpty())
                listOfNotNull(binds?.user?.name, binds?.user?.lastName)
            else listOfNotNull(name, lastName)
            return nameList.joinToString(" ")
        }

    val memberImage: String?
        get() {
            return if (imageUri == null || imageUri.uri.isNullOrEmpty()) {
                if (binds?.user == null) null
                else binds.user.loadUserImage()
            } else imageUri.uri
        }


    fun getSpeakerStatus(): String? {
        return if (isRegistered == false) "not_registered"
        else status
    }

    companion object {
        const val MEMBER_EVENT = "event"
        const val MEMBER_ROLE = "role"
        const val MEMBER_LIMIT = "limit"
        const val MEMBER_OFFSET = "offset"
        const val MEMBER_ROLE_SPEAKER = "speaker"
        const val MEMBER_BINDS = "binds"
    }
}

@Parcelize
data class MemberBindsModel(
    val event: EventResponse? = null,
    val user: UserDetail? = null,
    @SerializedName("userFavorite")
    var userFavorite: /*List<*/EventUserFavorite/*>*/? = null,
    @SerializedName("activities")
    val activities: List<EventActivityModel>? = null
) : Parcelable

@Parcelize
data class EventRights(
    val read: Boolean? = null,
    val edit: Boolean? = null,
    val delete: Boolean? = null,
    @SerializedName("sendReview")
    val sendReview: Boolean? = null,
    val approve: Boolean? = null,
    val decline: Boolean? = null,
    val ban: Boolean? = null,
    val unban: Boolean? = null,
    val registration: Boolean = true,
    val feedback: Boolean? = null
) : Parcelable

@Parcelize
data class UserRegisterModel(
    val id: Int? = null,
    val event: Int? = null,
    val user: Int? = null,
    val status: EventStatusModel? = null,
    @SerializedName("wasPresent")
    val wasPresent: Boolean? = null
) : Parcelable

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
    val hide: Boolean,
    @SerializedName("holdingDate")
    val holdingDate: DateModel? = null,
    val tag: List<Tags>? = null,
    val auditorium: String? = null,
    val member: List<EventActivityMember>? = null,
    val binds: EventActivityBinds? = null,
    var mNoEvent: Boolean = false,
    var mIsEmpty: Boolean = false
) : Parcelable

@Parcelize
data class EventActivityBinds(
    val event: EventResponse? = null,
    val tag: List<Tags>? = null,
    val member: List<MemberModel>? = null,
    @SerializedName("userCalendar")
    var userCalendar: EventCalendarModel? = null,
    @SerializedName("userFavorite")
    var userFavorite: /*List<*/EventUserFavorite/*>*/? = null,
    var auditorium: EventAuditoriumModel? = null,
    val users: List<UserDetail>? = null
) : Parcelable

@Parcelize
data class Tags(
    val id: Int? = null,
    val event: Int? = null,
    val name: String? = null,
    @SerializedName("isSeparator")
    val isSeparator: Boolean? = false
) : Parcelable

@Parcelize
data class EventActivityMember(
    val id: Int,
    val description: String? = null
) : Parcelable

@Parcelize
data class EventUserAgreement(
    val name: String? = null,
    @SerializedName("mimeType")
    val mimeType: String? = null,
    val size: Long? = null,
    val uri: String? = null
) : Parcelable

@Parcelize
data class EventPhoneModel(
    val value: String? = null,
    val title: String? = null
) : Parcelable {

    fun getAffiliationString(): String {
        return if (!title.isNullOrBlank()) "${title}: $value" else value ?: ""
    }
}

@Parcelize
data class EventFormatModel(
    val value: Int? = null,
    val custom: String? = null,
    var name: String? = null
) : Parcelable


@Parcelize
data class EventStatusModel(
    val value: String,
    val changed: String? = null,
    val comments: String? = null
) : Parcelable


@Parcelize
data class BackgroundColorModel(
    @SerializedName("isEnabled")
    val isEnabled: Boolean,
    val value: String? = null
) : Parcelable

@Parcelize
data class DateModel(
    val from: String? = null,
    val to: String? = null
) : Parcelable {
    fun getShortDate(): String = from?.split(" ")?.get(0) ?: ""
}

@Parcelize
data class RequestApplyModel(
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateLimit")
    val dateLimit: String? = null,
    @SerializedName("isClosed")
    val isClosed: Boolean? = null
) : Parcelable

