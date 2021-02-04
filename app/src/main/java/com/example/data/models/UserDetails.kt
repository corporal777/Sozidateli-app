package com.example.data.models

import com.example.util.USER_DATA_EMPTY
import com.google.gson.annotations.SerializedName

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
        var image: ImageModel? = null,
        var gender: String? = null,
        val address: NewUserAddress? = null,
        val state: UserState? = null,
        val interests: List<Int>? = null,
        var notes: String? = null,
        @SerializedName("educationLevel")
        val educationLevel: Int? = null,
        var binds: UserBinds? = null
) {

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
        }
}

data class UserBinds(
        val rights: UserRightsModel? = null,
        val education: List<EducationModel>? = null,
        @SerializedName("academic-degree")
        val academicDegree: List<AcademicDegreeModel>? = null,
        @SerializedName("work-experience")
        val workExperience: WorkExperienceModel? = null,
        @SerializedName("recommendation-file")
        var recommendationFile: List<FileModel>? = null,
        val organization: List<OrganizationModel>? = null,
        @SerializedName("userOrganizationRights")
        val userOrganizationRights: List<UserOrganizationRights>? = null
)

data class UserOrganizationRights(
        val organization: Int? = null,
        val rights: OrganizationRightsModel? = null
)

data class OrganizationRightsModel(
        val common: CommonModel? = null,
        val organizations: List<UsersRights>? = null
)

data class OrganizationModel(
        val id: Int? = null
)

data class FileModel(
        val id: Int? = null,
        val user: Int? = null,
        @SerializedName("mimeType")
        val mimeType: String? = null,
        val size: Long? = null,
        var name: String? = null,
        val uri: String? = null
)

data class WorkExperienceModel(
        val absent: Boolean? = null,
        val models: List<AcademicDegreeModel>? = null
)

data class AcademicDegreeModel(
        val id: Int? = null,
        val user: Int? = null,
        val speciality: Int? = null,
        val degree: Int? = null
)

data class EducationModel(
        val id: Int? = null,
        val user: Int? = null,
        val begin: String? = null,
        val end: String? = null,
        val organization: String? = null,
        val speciality: String? = null
)

data class UserRightsModel(
        val common: CommonModel? = null,
        val users: List<UsersRights>? = null
)

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
)

data class CommonModel(
        val create: Boolean? = null
)

data class UserState(
        @SerializedName("nameEdited")
        val nameEdited: Boolean? = null,
        @SerializedName("isHidden")
        var isHidden: Boolean? = null,
        @SerializedName("isBlocked")
        val isBlocked: Boolean? = null,
        @SerializedName("isSuspend")
        val isSuspend: BooleanModel? = null,
)

data class BooleanModel(
        val value: Boolean? = null,
        val till: String? = null
)

data class NewUserAddress(
        val index: String? = null,
        val country: String? = null,
        val federal: String? = null,
        val region: String? = null,
        val area: String? = null,
        val city: CityLocation? = null,
        val settlement: String? = null,
        val street: String? = null,
        val house: String? = null,
        val flat: String? = null,
        val lon: Double? = null,
        val lat: Double? = null,
        @SerializedName("fiasId")
        val fiasId: String? = null,
        @SerializedName("fullValue")
        val fullValue: String? = null,
        val description: AddressDescription? = null
)

data class AddressDescription(
        val place: String? = null,
        val title: String? = null,
        val description: String? = null
)

data class CityLocation(
        val value: String? = null,
        val lat: Double? = null,
        val lon: Double? = null
)

data class ImageModel(
        @SerializedName("mimeType")
        val mimeType: String? = null,
        val size: Long? = null,
        var uri: String? = null,
        val name: String? = null,
        val id: Int? = null,
        val user: Int? = null
)

data class FieldDetails(
        var value: String? = null,
        val type: String? = null,
        @SerializedName("isVisible")
        val isVisible: Boolean? = true,
        @SerializedName("isConfirmed")
        val isConfirmed: Boolean? = false,
        val absent: Boolean? = false,
        val title: String? = null
)

data class FieldListDetails(
        @SerializedName("values")
        val value: List<String>? = null,
        val type: String? = null,
        val absent: Boolean? = null
)