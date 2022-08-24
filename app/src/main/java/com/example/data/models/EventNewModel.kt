package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class EventNewModel(
    val data: List<EventNew?>? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class EventNewModelWithoutPagination(
    val data: List<EventNew>? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

@Parcelize
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
    val status: EventsStatusModel? = null,
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
    var binds: EventBindsModel? = null,
    var userRegistration: Event.RegistrationStatus? = null,
    @SerializedName("destination-scheme")
    val destinationScheme : List<String>? = null
) : Parcelable {

    companion object {
        const val EVENT_SORT_FIELD = "sortField"
        const val EVENT_SORT_TYPE = "sortType"
        const val EVENT_LIMIT = "limit"
        const val EVENT_ACTIVE = "onlyActive"
        const val EVENT_OFFSET = "offset"
        const val EVENT_BINDS = "binds"
        const val EVENT_ID = "id"
        const val EVENT_ORGANIZATION = "organization"
        const val EVENT_SEARCH = "search"
        const val EVENT_NAME = "name"
        const val EVENT_START_DATE = "holdingDate"
        const val EVENT_FORMAT = "format"
        const val EVENT_CATEGORY = "topicCategory"
        const val EVENT_STATUS = "status"
        const val EVENT_HIDDEN = "stateIsHidden"
        const val EVENT_PUBLIC = "isPublic"
        const val EVENT_CODE = "code"
        const val EVENT_USER_ID = "userRegistration.user"
        const val EVENT_USER_STATUS = "userRegistration.status"
        const val EVENT_ADDRESS_COUNTRY = "addressCountry"
        const val EVENT_ADDRESS_CITY = "addressCity"
        const val EVENT_ADDRESS_REGION = "addressRegion"
        const val EVENT_ADDRESS_STREET = "addressStreet"

        const val FILTER_REGISTRATION_PENDING = "pending"
        const val FILTER_REGISTRATION_APPROVED = "approved"
        const val FILTER_REGISTRATION_DECLINED = "declined"
        const val FILTER_REGISTRATION_ANY_REGISTERED = "pending,approved,declined"
    }
}

fun EventNew.createMapInfo(): MapInfo? {
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
    val event: EventNew? = null
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
    @SerializedName("userRegister")
    val userRegister: List<UserRegisterModel>? = null,
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
    val currentUserRegistration: CurrentUserRegistrationModel? = null,
    @SerializedName("user-form-result")
    val userFormResult: List<UserFormResultModel>? = null,
    @SerializedName("eventRegistrationState")
    val eventRegistrationState: EventRegistrationStateModel? = null,
    @SerializedName("userFavoriteActivities")
    val userFavoriteActivities: List<EventActivityModel>? = null,
//        @SerializedName("destination-scheme")
//        val destinationScheme: Any? = null,
    @SerializedName("is-user-subscribed")
    val isUserSubscribed: Boolean,
    @SerializedName("event-subscribe")
    val eventSubscribe: EventSubscriptionResponse? = null
) : Parcelable {

    fun getFirstActionStartDate(): String? {
        return if (activity.isNullOrEmpty())
            null
        else
            activity.first().holdingDate?.from
    }
}

@Parcelize
data class EventRegistrationStateModel(
    @SerializedName("availableActions")
    val availableActions: List<String>? = null,
    val prohibitions: ProhibitionsModel? = null
) : Parcelable

@Parcelize
data class ProhibitionsModel(
    @SerializedName("profileLevelToLow")
    val profileLevelToLow: ProfileLevelToLowModel? = null,
    @SerializedName("registrationClosed")
    val registrationClosed: Boolean
) : Parcelable

@Parcelize
data class ProfileLevelToLowModel(
    val value: Boolean,
    @SerializedName("requiredLevel")
    val requiredLevel: String? = null
) : Parcelable

@Parcelize
data class CurrentUserRegistrationModel(
    val id: Int? = null,
    val event: Int? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    val user: Int? = null,
    val status: EventsStatusModel? = null,
    @SerializedName("wasPresent")
    val wasPresent: String? = null
) : Parcelable

@Parcelize
data class UserFormResultModel(
    @SerializedName("formType")
    val formType: EventFormModel? = null,
    val result: EventFormResultModel? = null
) : Parcelable

@Parcelize
data class EventFormModel(
    val id: Int? = null,
    val event: Int? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val draft: EventFormDraftModel? = null,
    val type: Type? = null,
    val files: List<FileModel>? = null,
    val fields: List<EventRegisterFields>? = null
) : Parcelable {
    enum class Type {
        @SerializedName("participation")
        PARTICIPATION,

        @SerializedName("rating")
        RATING
    }

    companion object {
        const val FORM_EVENT_ID = "event"
        const val FORM_TYPE = "type"
    }
}

@Parcelize
data class EventFormDraftModel(
    val value: Boolean = false,
    val parent: String? = null
) : Parcelable

@Parcelize
data class EventRegisterFields(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    @SerializedName("isRequired")
    val isRequired: Boolean,
    val sort: Int? = null,
    val type: EventRegisterField.Type? = null,
    val parameters: FieldsParameters? = null
) : Parcelable {
    enum class Type {
        @SerializedName("string")
        STRING,

        @SerializedName("textarea")
        TEXT_AREA,

        @SerializedName("number")
        NUMBER,

        @SerializedName("date")
        DATE,

        @SerializedName("datetime")
        DATETIME,

        @SerializedName("file")
        FILE,

        @SerializedName("list")
        LIST,

        @SerializedName("checkbox")
        CHECKBOX,

        @SerializedName("radiobuttons")
        RADIO_BUTTONS,

        @SerializedName("boolean")
        BOOLEAN,

        @SerializedName("passport")
        PASSPORT,

        @SerializedName("separator")
        SEPARATOR,

        @SerializedName("selectbox")
        SELECT_BOX,

        @SerializedName("radiobox")
        RADIO_BOX,

        @SerializedName("datetimeplaned")
        DATETIMEPLANED
    }
}

@Parcelize
data class FieldsParameters(
    @SerializedName("allowedExtensions")
    val allowedExtensions: List<String>? = null,
    val options: List<String>? = null
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
    val event: EventNew? = null
) : Parcelable

@Parcelize
data class EventAuditoriumModel(
    val id: Int? = null,
    val event: Int? = null,
    val name: String? = null
) : Parcelable

data class TestModel(
    val id: Int,
    val name: String,
    val uri: String
)


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
    val isLead : Boolean? = null,
    val isNewUser : Boolean? = null,
    @SerializedName("organizationAndPosition")
    val organizationAndPosition: String? = null,
    val binds: MemberBindsModel? = null
) : Parcelable {

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
    val event: EventNew? = null,
    val user: UserDetail? = null,
    @SerializedName("userFavorite")
    var userFavorite: /*List<*/EventUserFavorite/*>*/? = null,
    @SerializedName("activities")
    val activities : List<EventActivityModel>? = null
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
    val tag: List<Int>? = null,
    val auditorium: Int? = null,
    val member: List<EventActivityMember>? = null,
    val binds: EventActivityBinds? = null,
    var mNoEvent: Boolean = false,
    var mIsEmpty : Boolean = false
) : Parcelable

@Parcelize
data class EventActivityBinds(
    val event: EventNew? = null,
    val tag: List<Tags>? = null,
    val member: List<MemberModel>? = null,
    @SerializedName("userCalendar")
    var userCalendar: EventCalendarItem? = null,
    @SerializedName("userFavorite")
    var userFavorite: /*List<*/EventUserFavorite/*>*/? = null,
    var auditorium: EventAuditoriumModel? = null,
    val users : List<UserDetail>? = null
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
) : Parcelable

@Parcelize
data class EventRatingModel(
    @SerializedName("isAvailable")
    val isAvailable: Boolean? = null,
    @SerializedName("formEnabled")
    val formEnabled: Boolean? = false,
    @SerializedName("askDelay")
    val askDelay: Int? = null,
    @SerializedName("approvingMode")
    val approvingMode: String? = null
) : Parcelable

@Parcelize
data class EventStatusModel(
    val value: Event.RegistrationStatus,
    val changed: String? = null,
    val comments: String? = null
) : Parcelable

@Parcelize
data class EventsStatusModel(
    val value: Event.Status? = null,
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
) : Parcelable

@Parcelize
data class RequestApplyModel(
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateLimit")
    val dateLimit: String? = null,
    @SerializedName("isClosed")
    val isClosed: Boolean? = null
) : Parcelable