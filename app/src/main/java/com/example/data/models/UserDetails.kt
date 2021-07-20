package com.example.data.models

import android.os.Parcelable
import com.example.ui.views.UserSubscribeButton
import com.example.util.USER_DATA_EMPTY
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetail(
        val id: Int,
        var name: String? = null,
        @SerializedName("lastName")
        var lastName: String? = null,
        @SerializedName("middleName")
        var middleName: FieldDetails? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("modifiedDate")
        val modifiedDate: String? = null,
        var email: FieldDetails? = null,
        var phone: List<FieldDetails>? = null,
        var site: FieldListDetails? = null,
        @SerializedName("socialLinks")
        var socialLinks: FieldListDetails? = null,
        var birthday: FieldDetails? = null,
        var image: ImageModel,
        var gender: String? = null,
        var address: NewUserAddress? = null,
        val state: UserState? = null,
        var interests: List<Int>? = null,
        var notes: String? = null,
        @SerializedName("educationLevel")
        var educationLevel: Int? = null,
        var binds: UserBinds? = null,
        @SerializedName("educationLevelList")
        var educationLevelList: List<EducationLevel>? = null,
        var speciality: List<EducationLevel>? = null,
        @SerializedName("academicDegrees")
        var academicDegrees: List<EducationLevel>? = null,
        @SerializedName("isCurrentUser")
        var isCurrentUser: Boolean = false
): Parcelable {

        val fullName: String
                get() {
                        val nameList = listOfNotNull(
                                name,
                                getMiddleName(),
                                lastName
                        )
                        return nameList.joinToString(" ")
                }

        fun getMiddleName(): String? {
                return middleName?.let { if (it.value == USER_DATA_EMPTY || it.value?.isEmpty() == true) null else it.value }
        }

        fun getUserSubscribeAction(): UserSubscribeButton.Action? {
                return when {
                        isCurrentUser -> null
                        //user_banned || chat?.isBannedByYou == true -> UserSubscribeButton.Action.UNBLOCK
                        binds?.userFavorite != null -> UserSubscribeButton.Action.UNFAVORITE
                        else -> UserSubscribeButton.Action.FAVORITE
                }
        }

        companion object {
                const val USER_EMAIL = "email"
                const val USER_NAME = "name"
                const val USER_LAST_NAME = "lastName"
                const val USER_MIDDLE_NAME = "middleName"
                const val USER_PHONE = "phone"
                const val USER_STATE = "state"
                const val USER_GENDER = "gender"
                const val USER_BIRTHDAY = "birthday"
                const val USER_NOTES = "notes"
                const val USER_SITE = "site"
                const val USER_SOCIAL_LINKS = "socialLinks"
                const val USER_WORK_EXPERIENCE = "work-experience"
                const val USER_INTERESTS = "interests"
                const val USER_EDUCATION_LEVEL = "educationLevel"
                const val USER_ADDRESS = "address"
                const val USER_LIMIT = "limit"
                const val USER_OFFSET = "offset"
                const val USER_BINDS = "binds"
                const val USER_ADDRESS_STREET = "addressStreet"
                const val USER_SEARCH = "search"
        }
}

@Parcelize
data class UserBinds(
        val rights: UserRightsModel? = null,
        var education: List<EducationModel>? = null,
        @SerializedName("academic-degree")
        var academicDegree: List<AcademicDegreeModel>? = null,
        @SerializedName("work-experience")
        var workExperience: WorkExperienceModel? = null,
        @SerializedName("recommendation-file")
        var recommendationFile: List<FileModel>? = null,
        val organization: List<OrganizationNew>? = null,
        @SerializedName("userOrganizationRights")
        val userOrganizationRights: List<UserOrganizationRights>? = null,
        @SerializedName("userFavorite")
        var userFavorite: EventUserFavorite? = null
): Parcelable

@Parcelize
data class UserOrganizationRights(
        val organization: Int? = null,
        val rights: OrganizationRightsModel? = null
): Parcelable

@Parcelize
data class OrganizationRightsModel(
        val common: CommonModel? = null,
        val organizations: List<UsersRights>? = null
): Parcelable

@Parcelize
data class OrganizationModel(
        val id: Int? = null
): Parcelable

@Parcelize
data class FileModel(
        val id: Int? = null,
        val user: Int? = null,
        @SerializedName("createdDate")
        val createdDate: String? = null,
        @SerializedName("mimeType")
        val mimeType: String? = null,
        val size: Long? = null,
        var name: String? = null,
        val uri: String? = null
): Parcelable

@Parcelize
data class WorkExperienceModel(
        var absent: Boolean? = null,
        var models: List<WorkExperience>? = null
): Parcelable

@Parcelize
data class WorkExperienceServerModel(
        val absent: Boolean? = null,
        val data: List<WorkExperience>? = null
): Parcelable

@Parcelize
data class WorkExperience(
        val id: Int?,
        val begin: String?,
        val end: String?,
        val organization: String?,
        val position: String?,
        val description: String?
): Parcelable

@Parcelize
data class AcademicDegreeModel(
        val id: Int? = null,
        val speciality: Int? = null,
        val degree: Int? = null
): Parcelable

@Parcelize
data class EducationModel(
        val id: Int? = null,
        val begin: String? = null,
        val end: String? = null,
        val organization: String? = null,
        val speciality: String? = null
): Parcelable

@Parcelize
data class UserRightsModel(
        val common: CommonModel? = null,
        val users: List<UsersRights>? = null,
        val organizations: List<UsersRights>? = null
): Parcelable

@Parcelize
data class UsersRights(
        val id: Int? = null,
        val read: Boolean? = null,
        val edit: Boolean? = null,
        val logout: Boolean? = null,
        val block: Boolean? = null,
        val unblock: Boolean? = null,
        val activate: Boolean? = null,
        val deactivate: Boolean? = null,
        @SerializedName("checkProfileFullness")
        val checkProfileFullness: Boolean? = null,
        val delete: Boolean? = null,
        @SerializedName("sendReview")
        val sendReview: Boolean? = null,
        val approve: Boolean? = null,
        val decline: Boolean? = null,
        val ban: Boolean? = null,
        val unban: Boolean? = null,
        @SerializedName("markSpecial")
        val markSpecial: Boolean? = null,
        @SerializedName("unmarkSpecial")
        val unmarkSpecial: Boolean? = null,
        @SerializedName("createEvent")
        val createEvent: Boolean? = null
): Parcelable

@Parcelize
data class CommonModel(
        val create: Boolean? = null
): Parcelable

@Parcelize
data class UserState(
        @SerializedName("nameEdited")
        val nameEdited: Boolean? = null,
        @SerializedName("isHidden")
        var isHidden: Boolean? = null,
        @SerializedName("isBlocked")
        val isBlocked: Int? = null,
        @SerializedName("isSuspend")
        val isSuspend: BooleanModel? = null,
): Parcelable

@Parcelize
data class BooleanModel(
        val value: Boolean? = null,
        val till: String? = null
): Parcelable

@Parcelize
data class NewUserAddress(
        var index: String? = null,
        var country: String? = null,
        var federal: String? = null,
        var region: String? = null,
        var area: String? = null,
        var city: String? = null,
        var settlement: String? = null,
        var street: String? = null,
        var house: String? = null,
        var flat: String? = null,
        var lon: Double? = null,
        var lat: Double? = null,
        @SerializedName("fiasId")
        var fiasId: String? = null,
        @SerializedName("fullValue")
        var fullValue: String? = null,
        var description: AddressDescription? = null,
        var shortAddres: String? = null
): Parcelable {

        fun getShortAddress(): String {
                return if (!shortAddres.isNullOrEmpty())
                        shortAddres?: ""
                else
                        if (!region.isNullOrEmpty())
                                city?: ""
                        else {
                                if (region.isNullOrEmpty() && city.isNullOrEmpty()) ""
                                else if (!region.isNullOrEmpty() && city.isNullOrEmpty()) region?: ""
                                else if (region.isNullOrEmpty() && !city.isNullOrEmpty()) city?: ""
                                else "$region, $city"
                        }
        }
}

@Parcelize
data class AddressDescription(
        val place: String? = null,
        val title: String? = null,
        val description: String? = null
): Parcelable

@Parcelize
data class CityLocation(
        val value: String? = null,
        val lat: Double? = null,
        val lon: Double? = null
): Parcelable

@Parcelize
data class ImageModel(
        @SerializedName("mimeType")
        val mimeType: String? = null,
        val size: Long? = null,
        var uri: String? = null,
        val name: String? = null,
        val id: Int? = null,
        val user: Int? = null
): Parcelable

@Parcelize
data class FieldDetails(
        var value: String? = null,
        val type: String? = null,
        @SerializedName("isVisible")
        val isVisible: Boolean? = true,
        @SerializedName("isConfirmed")
        var isConfirmed: Boolean? = false,
        val absent: Boolean? = false,
        val title: String? = null
): Parcelable

@Parcelize
data class FieldListDetails(
        @SerializedName("values")
        val value: List<String>? = null,
        val type: String? = null,
        val absent: Boolean? = null
): Parcelable