package com.examle.data.models

import android.os.Parcelable
import com.examle.data.models.event.BackgroundColorModel
import com.examle.data.models.event.EventPhoneModel
import com.examle.data.models.event.EventResponse
import com.examle.data.models.event.EventStatusModel
import com.examle.data.models.event.EventUserAgreement
import com.examle.data.models.event.EventUserFavorite
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrganizationNew(
    val id: Long? = null,
    val description: String? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    @SerializedName("modifiedDate")
    val modifiedDate: String? = null,
    @SerializedName("createdBy")
    val createdBy: Int? = null,
    @SerializedName("isNko")
    val isNko: Boolean? = null,
    @SerializedName("legalInformation")
    val legalInformation: LegalInformationModel? = null,
    val authority: OrganizationAuthorityModel? = null,
    val phone: List<EventPhoneModel>? = null,
    val email: List<EventPhoneModel>? = null,
    val site: List<EventPhoneModel>? = null,
    @SerializedName("socialLink")
    val socialLink: List<EventPhoneModel>? = null,
    val address: List<NewUserAddress>? = null,
    val logo: EventUserAgreement? = null,
    val image: EventUserAgreement? = null,
    val backgroundColor: BackgroundColorModel? = null,
    val status: EventStatusModel? = null,
    @SerializedName("isSpecial")
    val isSpecial: Boolean? = null,
    //val state: Any? = null,
    var binds: OrganizationBindsModel? = null
) : Parcelable {

    fun getOrganizationName(): String {
        return legalInformation?.name?.short ?: legalInformation?.name?.full ?: ""
    }

    companion object {
        const val ORGANIZATION_SORT_FIELD = "sortField"
        const val ORGANIZATION_SORT_Type = "sortType"
        const val ORGANIZATION_LIMIT = "limit"
        const val ORGANIZATION_OFFSET = "offset"
        const val ORGANIZATION_BINDS = "binds"
        const val ORGANIZATION_LEGAL_INFORMATION_NAME_SHORT = "legalInformationNameShort"
        const val ORGANIZATION_LEGAL_INFORMATION_NAME_FULL = "legalInformationNameFull"
        const val ORGANIZATION_ADDRESS_INDEX = "addressIndex"
        const val ORGANIZATION_ADDRESS_COUNTRY = "addressCountry"
        const val ORGANIZATION_ADDRESS_STREET = "addressStreet"
        const val ORGANIZATION_ADDRESS_FEDERAL = "addressFederal"
        const val ORGANIZATION_ADDRESS_REGION = "addressRegion"
        const val ORGANIZATION_ADDRESS_AREA = "addressArea"
        const val ORGANIZATION_ADDRESS_CITY = "addressCity"
        const val ORGANIZATION_ADDRESS_SETTLEMENT = "addressSettlement"
        const val ORGANIZATION_ADDRESS_HOUSE = "addressHouse"
        const val ORGANIZATION_ADDRESS_FLAT = "addressFlat"
        const val ORGANIZATION_LEGAL_INFORMATION_INN = "legalInformationInn"
        const val ORGANIZATION_STATUS = "status"
        const val ORGANIZATION_IS_SPECIAL = "isSpecial"
    }
}

@Parcelize
data class OrganizationLeaderModel(
    val id: Int? = null,
    val organization: Int? = null,
    val name: String? = null,
    val position: String? = null,
    @SerializedName("confirmationFile")
    val confirmationFile: EventUserAgreement? = null
) : Parcelable

@Parcelize
data class OrganizationBindsModel(
    val rights: OrganizationRightsModel? = null,
    val leader: List<OrganizationLeaderModel>? = null,
    var member: List<OrganizationMemberModel>? = null,
    var membersSize: Int? = 0,
    val user: List<UserDetail>? = null,
    @SerializedName("userFavorite")
    var userFavorite: EventUserFavorite? = null,
    var events: List<EventResponse>? = null,
    var eventsSize: Int? = 0
) : Parcelable

@Parcelize
data class OrganizationMemberBindsModel(
    val user: UserDetail? = null,
    @SerializedName("userFavorite")
    var userFavorite: EventUserFavorite? = null
) : Parcelable

@Parcelize
data class OrganizationMemberModel(
    val id: Int? = null,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    @SerializedName("invitedBy")
    val invitedBy: Int? = null,
    val organization: Int? = null,
    val user: Int? = null,
    var isCurrentUser: Boolean? = false,
    val position: OrganizationMemberPositionModel? = null,
    val nko: OrganizationMemberNkoModel? = null,
    @SerializedName("canCreateEvent")
    val canCreateEvent: Boolean? = null,
    val isHidden: Boolean? = null,
    val status: String? = null,
    val binds: OrganizationMemberBindsModel? = null
) : Parcelable

@Parcelize
data class OrganizationMemberNkoModel(
    val position: Int? = null,
    val canEdit: Boolean? = null
) : Parcelable

@Parcelize
data class OrganizationMemberPositionModel(
    val value: String? = null,
    val manual: String? = null
) : Parcelable

@Parcelize
data class OrganizationAuthorityModel(
    val type: String? = null,
    val document: OrganizationDocumentModel? = null
) : Parcelable

@Parcelize
data class OrganizationDocumentModel(
    val file: EventUserAgreement? = null
) : Parcelable

@Parcelize
data class LegalInformationModel(
    val name: LegalInformationNameModel? = null,
    val inn: String? = null,
    val ogrn: String? = null
) : Parcelable

@Parcelize
data class LegalInformationNameModel(
    val short: String? = null,
    val full: String? = null
) : Parcelable