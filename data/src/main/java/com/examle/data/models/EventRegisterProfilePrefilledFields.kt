package com.examle.data.models

import android.os.Parcelable
import com.examle.domain.model.user.EducationLevel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventRegisterProfilePrefilledData(
    val id: Int,
    val form: Int,
    val user: Int,
    val isRequired: Boolean,
    val fields: EventRegisterProfilePrefilledFields
) : Parcelable

@Parcelize
data class EventRegisterProfilePrefilledFields(
    val user_fio: String? = null,
    val userName: String? = null,
    val userLastName: String? = null,
    val userMiddleName: String? = null,
    @SerializedName("user_birthday")
    val userBirthday: String? = null,
    val user_gender: String? = null,
    val user_notes: String? = null,
    val recommendationFile: List<FileModel>? = null,
    val user_phone: String? = null,
    val user_work_phone: String? = null,
    val user_email: String? = null,
    val user_id: String? = null,
    val address: NewUserAddress? = null,
    val educationLevel: EducationLevel? = null,
    val education: List<EducationModel>? = null,
    @SerializedName("academic-degree")
    val academic_degree: List<PrefilledFieldAcademicDegreeModel>? = null,
    @SerializedName("work-experience")
    val work_experience: List<WorkExperience>? = null,
    val work_experience_absent: Boolean,
    val contactInformation: ContactInformationModel? = null,
    @SerializedName("user_work_phone_absent")
    val work_phone_absent: Boolean,
    @SerializedName("user_social_links_absent")
    val social_links_absent: Boolean,
    @SerializedName("user_site_absent")
    val siteAbsent: Boolean
) : Parcelable {

    fun prefilledToJson(): JsonElement {
        return Gson().toJsonTree(this)
    }

    companion object {

        fun JsonElement?.prefFromJson(params: EventFormFieldModel?): EventRegisterPrefilledFields? {
            val options = params?.parameters?.options
            if (this == null || options.isNullOrEmpty()) return null

            val data = Gson().fromJson(this, EventRegisterProfilePrefilledFields::class.java)
            val mainDataList = arrayListOf<ProfileField>().apply {
                if (options.contains("user_fio")){
                    add(PrefilledFieldString("Фамилия", data.userLastName))
                    add(PrefilledFieldString("Имя", data.userName))
                    add(PrefilledFieldString("Отчество", data.userMiddleName ?: "«Нет отчества»"))
                }
                if (options.contains("user_birthday"))
                    add(PrefilledFieldString("Дата рождения", data.userBirthday))
                if (options.contains("user_notes"))
                    add(PrefilledFieldString("Дополнительные сведения", data.user_notes))
                if (options.contains("user_gender"))
                    add(PrefilledFieldString("Пол", data.user_gender))
                if (options.contains("address"))
                    add(
                        PrefilledFieldString(
                            "Регион и населенный пункт фактического проживания",
                            data.address?.fullValue ?: data.address?.getShortAddress()
                        )
                    )
                if (options.contains("recommendationFile"))
                    add(PrefilledFieldFiles("Файлы", data.recommendationFile))
                if (options.contains("user_email"))
                    add(PrefilledFieldString("Основной e-mail", data.user_email))
                if (options.contains("user_phone"))
                    add(PrefilledFieldString("Мобильный телефон", data.user_phone))
                if (options.contains("user_work_phone"))
                    add(
                        PrefilledFieldContacts(
                            "Рабочий телефон",
                            data.user_work_phone,
                            data.work_phone_absent
                        )
                    )
                if (options.contains("socialNetwork"))
                    add(
                        PrefilledFieldContacts(
                            "Социальные сети",
                            data.contactInformation?.socialLinks?.values?.joinToString("\n") {
                                it.value ?: ""
                            },
                            data.social_links_absent
                        )
                    )
                if (options.contains("publicEmail"))
                    add(
                        PrefilledFieldContacts(
                            "Публичный e-mail",
                            data.contactInformation?.emails?.joinToString("\n") { it.value ?: "" },
                            false
                        )
                    )
                if (options.contains("site"))
                    add(
                        PrefilledFieldContacts(
                            "Сайт",
                            data.contactInformation?.site?.values?.joinToString("\n") {
                                it.value ?: ""
                            },
                            data.siteAbsent
                        )
                    )
                if (options.contains("education"))
                    add(
                        PrefilledFieldEducation(
                            "Уровень образования",
                            data.educationLevel?.name,
                            data.education,
                            data.academic_degree
                        )
                    )

                if (options.contains("workExperience"))
                    add(
                        PrefilledFieldWorkExperience(
                            "Опыт работы",
                            data.work_experience,
                            data.work_experience_absent
                        )
                    )
            }

            return EventRegisterPrefilledFields(mainDataList)
        }

    }

}


data class EventRegisterPrefilledFields(val prefilledFields: List<ProfileField>) {
    fun isPrefilledFieldsValid(): Boolean = !prefilledFields.any { x -> !x.isValid }
}

sealed class ProfileField(val isValid: Boolean)

data class PrefilledFieldString(
    val name: String,
    val value: String?
) : ProfileField(!value.isNullOrEmpty())

data class PrefilledFieldContacts(
    val name: String,
    val value: String?,
    val isAbsent: Boolean
) : ProfileField(if (isAbsent) true else !value.isNullOrEmpty())

data class PrefilledFieldFiles(
    val name: String,
    val value: List<FileModel>?
) : ProfileField(!value.isNullOrEmpty())

data class PrefilledFieldEducation(
    val name: String,
    val educationLevel: String?,
    val education: List<EducationModel>?,
    val academicDegree: List<PrefilledFieldAcademicDegreeModel>?
) : ProfileField(!educationLevel.isNullOrEmpty())

data class PrefilledFieldWorkExperience(
    val name: String,
    val value: List<WorkExperience>?,
    val isAbsent: Boolean
) : ProfileField(if (isAbsent) true else !value.isNullOrEmpty())


@Parcelize
data class PrefilledFieldAcademicDegreeModel(
    val id: Int? = null,
    val user: Int? = null,
    val speciality: String? = null,
    val degree: String? = null
) : Parcelable