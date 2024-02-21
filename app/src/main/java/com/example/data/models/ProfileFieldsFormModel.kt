package com.example.data.models

import android.os.Parcelable
import com.example.data.models.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileFieldsData(
    val form: Int,
    val user: Int,
    val isRequired: Boolean,
    val fields: ProfileFieldsFormModel
) : Parcelable {
    fun toFormResult(options: List<String>?): ProfileFieldsFormResult {
        return ProfileFieldsFormResult(
            fieldId = form,
            fieldsIsRequired = isRequired,
            user_name = ProfileFieldString(isRequired, false, false, fields.user_fio),
            user_birthday = ProfileFieldString(isRequired, false, false, fields.user_birthday),
            user_gender = ProfileFieldString(isRequired, false, false, fields.user_gender),
            user_notes = ProfileFieldString(isRequired, false, false, fields.user_notes),
            user_phone = ProfileFieldString(isRequired, false, false, fields.user_phone),
            user_work_phone = ProfileFieldString(isRequired, false, fields.work_phone_absent, fields.user_work_phone),
            user_email = ProfileFieldString(isRequired, false, false, fields.user_email),
            address = ProfileFieldString(isRequired, false, false, fields.address?.fullValue ?: fields.address?.getShortAddress()),
            educationLevel = ProfileFieldEducationLevel(isRequired, fields.educationLevel),
            education = ProfileFieldEducation(isRequired, false, fields.education),
            academic_degree = ProfileFieldAcademicDegree(isRequired, false, fields.academic_degree),
            work_experience = ProfileFieldWorkExperience(isRequired, false, fields.work_experience_absent, fields.work_experience),
            user_links = ProfileFieldString(isRequired, false, fields.social_links_absent, fields.contactInformation?.socialLinks?.values?.joinToString("\n") { it.value ?: "" }),
            user_sites = ProfileFieldString(isRequired, false, fields.site_absent, fields.contactInformation?.site?.values?.joinToString("\n") { it.value ?: "" }),
            user_public_email = ProfileFieldString(isRequired, false, false, fields.contactInformation?.emails?.joinToString("\n") { it.value ?: "" }),
            user_files = ProfileFieldString(isRequired, false, false, fields.recommendationFile?.joinToString("\n") { it.name ?: "" })
        ).setFieldsIsChosen(options)
    }
}

@Parcelize
data class ProfileFieldsFormModel(
    val user_fio : String? = null,
    val user_name: String? = null,
    val user_last_name: String? = null,
    val user_middle_name: String? = null,
    val user_birthday: String? = null,
    val user_gender: String? = null,
    val user_notes: String? = null,
    val user_phone: String? = null,
    val user_work_phone: String? = null,
    val user_email: String? = null,
    val user_id: String? = null,
    val address: NewUserAddress? = null,
    val educationLevel: EducationLevel? = null,
    val education: List<EducationModel>? = null,
    @SerializedName("academic-degree")
    val academic_degree: List<PrefilledFormAcademicDegree>? = null,
    @SerializedName("work-experience")
    val work_experience: List<WorkExperience>? = null,
    val recommendationFile: List<FileModel>? = null,
    val contactInformation: ContactInformationModel? = null,
    @SerializedName("user_work_phone_absent")
    val work_phone_absent: Boolean,
    @SerializedName("user_social_links_absent")
    val social_links_absent: Boolean,
    @SerializedName("user_site_absent")
    val site_absent: Boolean,
    val work_experience_absent: Boolean,
) : Parcelable

data class ProfileFieldsFormResult(
    val fieldId : Int?,
    val fieldsIsRequired: Boolean,
    var user_name: ProfileFieldString,
    var user_birthday: ProfileFieldString,
    var user_gender: ProfileFieldString,
    var user_notes: ProfileFieldString,
    var user_phone: ProfileFieldString,
    var user_work_phone: ProfileFieldString,
    var user_email: ProfileFieldString,
    var address: ProfileFieldString,
    var educationLevel: ProfileFieldEducationLevel,
    var education: ProfileFieldEducation,
    var academic_degree: ProfileFieldAcademicDegree,
    var work_experience: ProfileFieldWorkExperience,
    var user_links: ProfileFieldString,
    var user_sites: ProfileFieldString,
    var user_public_email: ProfileFieldString,
    var user_files: ProfileFieldString
) {

    fun toJsonElement(): JsonElement {
        return Gson().toJsonTree(this)
    }

    fun checkProfileFieldsIsValid(data: ProfileFieldsFormResult): Boolean {
        var isValid = true
        if (data.fieldsIsRequired) {
            if (data.user_name.isChosen) {
                if (data.user_name.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_birthday.isChosen) {
                if (data.user_birthday.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_gender.isChosen) {
                if (data.user_gender.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_notes.isChosen) {
                if (data.user_notes.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_phone.isChosen) {
                if (data.user_phone.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_work_phone.isChosen) {
                if (!data.user_work_phone.isAbsent) {
                    if (data.user_work_phone.value.isNullOrEmpty())
                        isValid = false
                }
            }
            if (data.user_email.isChosen) {
                if (data.user_email.value.isNullOrEmpty()) isValid = false
            }
            if (data.address.isChosen) {
                if (data.address.value.isNullOrEmpty()) isValid = false
            }
//            if (data.education.isChosen) {
//                if (data.education.value.isNullOrEmpty() && data.academic_degree.value.isNullOrEmpty()){
//                    isValid = false
//                }
//            }
            if (data.academic_degree.isChosen || data.education.isChosen) {
                if (data.educationLevel.value == null || data.education.value.isNullOrEmpty()) {
                    isValid = false
                }
            }
            if (data.work_experience.isChosen) {
                if (!data.work_experience.isAbsent) {
                    if (data.work_experience.value.isNullOrEmpty()) isValid = false
                }
            }
            if (data.user_links.isChosen) {
                if (!data.user_links.isAbsent) {
                    if (data.user_links.value.isNullOrEmpty()) isValid = false
                }
            }
            if (data.user_public_email.isChosen) {
                if (data.user_public_email.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_files.isChosen) {
                if (data.user_files.value.isNullOrEmpty()) isValid = false
            }
            if (data.user_sites.isChosen) {
                if (!data.user_sites.isAbsent) {
                    if (data.user_sites.value.isNullOrEmpty()) isValid = false
                }
            }
        }
        return isValid
    }

    fun setFieldsIsChosen(options: List<String>?) : ProfileFieldsFormResult{
        user_name.isChosen = true
//            if (options?.contains("user_fio") == true) {
//                user_name.isChosen = true
//            }
        if (options?.contains("user_birthday") == true) {
            user_birthday.isChosen = true
        }
        if (options?.contains("user_gender") == true) {
            user_gender.isChosen = true
        }
        if (options?.contains("user_notes") == true) {
            user_notes.isChosen = true
        }
        if (options?.contains("user_phone") == true) {
            user_phone.isChosen = true
        }
        if (options?.contains("user_work_phone") == true) {
            user_work_phone.isChosen = true
        }
        if (options?.contains("user_email") == true) {
            user_email.isChosen = true
        }
        if (options?.contains("address") == true) {
            address.isChosen = true
        }
        if (options?.contains("education") == true) {
            education.isChosen = true
        }
        if (options?.contains("academicDegree") == true) {
            academic_degree.isChosen = true
        }
        if (options?.contains("workExperience") == true) {
            work_experience.isChosen = true
        }
        if (options?.contains("socialNetwork") == true) {
            user_links.isChosen = true
        }
        if (options?.contains("site") == true) {
            user_sites.isChosen = true
        }
        if (options?.contains("publicEmail") == true) {
            user_public_email.isChosen = true
        }
        if (options?.contains("recommendationFile") == true) {
            user_files.isChosen = true
        }
        return this@ProfileFieldsFormResult
    }
}

data class ProfileFieldString(
    var isRequired: Boolean,
    var isChosen: Boolean = false,
    var isAbsent: Boolean = false,
    var value: String? = null
)


data class ProfileFieldEducationLevel(
    var isRequired: Boolean,
    var value: EducationLevel? = null
)

data class ProfileFieldEducation(
    var isRequired: Boolean,
    var isChosen: Boolean,
    var value: List<EducationModel>? = null
)

data class ProfileFieldAcademicDegree(
    var isRequired: Boolean,
    var isChosen: Boolean,
    var value: List<PrefilledFormAcademicDegree>? = null
)

data class ProfileFieldWorkExperience(
    var isRequired: Boolean,
    var isChosen: Boolean,
    var isAbsent: Boolean,
    var value: List<WorkExperience>? = null
)

@Parcelize
data class PrefilledFormAcademicDegree(
    val id: Int? = null,
    val user: Int? = null,
    val speciality: String? = null,
    val degree: String? = null
) : Parcelable

