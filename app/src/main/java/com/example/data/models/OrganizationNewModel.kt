package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class OrganizationNewModel(
        val data: List<OrganizationNew?>? = null,
        @SerializedName("totalCount")
        val totalCount: Int? = null
)

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
    val binds: OrganizationBindsModel? = null
): Parcelable {

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
        const val ORGANIZATION_LEGAL_INFORMATION_INN = "legalInformationInn"
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
): Parcelable

@Parcelize
data class OrganizationBindsModel(
        val rights: OrganizationRightsModel? = null,
        val leader: List<OrganizationLeaderModel>? = null,
        val member: List<OrganizationMemberModel>? = null,
        val user: List<UserDetail>? = null,
        @SerializedName("userFavorite")
        var userFavorite: EventUserFavorite? = null
): Parcelable

@Parcelize
data class OrganizationMemberBindsModel(
    val user: UserDetail? = null
): Parcelable

@Parcelize
data class OrganizationMemberModel(
        val id: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("invitedBy")
        val invitedBy: Int? = null,
        val organization: Int? = null,
        val user: Int? = null,
        val position: OrganizationMemberPositionModel? = null,
        val nko: OrganizationMemberNkoModel? = null,
        @SerializedName("canCreateEvent")
        val canCreateEvent: Boolean? = null,
        val status: String? = null,
        val binds: OrganizationMemberBindsModel? = null
): Parcelable

@Parcelize
data class OrganizationMemberNkoModel(
    val position: Int? = null,
    val canEdit: Boolean? = null
): Parcelable

@Parcelize
data class OrganizationMemberPositionModel(
        val value: String? = null,
        val manual: String? = null
): Parcelable

@Parcelize
data class OrganizationAuthorityModel(
        val type: String? = null,
        val document: OrganizationDocumentModel? = null
): Parcelable

@Parcelize
data class OrganizationDocumentModel(
    val file: EventUserAgreement? = null
): Parcelable

@Parcelize
data class LegalInformationModel(
        val name: LegalInformationNameModel? = null,
        val inn: String? = null,
        val ogrn: String? = null
): Parcelable

@Parcelize
data class LegalInformationNameModel(
    val short: String? = null,
    val full: String? = null
): Parcelable