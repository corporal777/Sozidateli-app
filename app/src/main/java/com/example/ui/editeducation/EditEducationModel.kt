package com.example.ui.editeducation

import java.util.*


data class EditEducationModel(
        var id: Int,
        var type: Int,
        var birthday: Date? = null,
        var education: EducationModelNew? = null,
        var academicDegrees: AcademicDegreeModelNew? = null,
        var availableEducations: List<EducationLevelNew>? = null,
        var availableDegrees: List<EducationLevelNew>? = null,
        var availableSciences: List<EducationLevelNew>? = null,
        var educationLevel: ToggleIntModelNew? = null,
        var isDeleteVisible: Boolean,
        var isDataValid: Boolean,
        var showErrors: Boolean,
        var isNotFinishedSelected: Boolean,
        var selectedDegree: String?,
        var hasAcademicDegreee: Boolean
) {

    companion object {
        const val EDUCATION_LEVEL = 1
        const val HIGHT_LEVEL_ITEM = 2
        const val ADD_HIGHT_LEVEL = 3
        const val EDUCATION_ITEM = 4
        const val ADD_EDUCATION = 5
    }
}

data class EducationModelNew(
        val id: Int? = null,
        var begin: String? = null,
        var end: String? = null,
        var organization: String? = null,
        var speciality: String? = null,
        var showInProfile: Boolean? = false
)

data class AcademicDegreeModelNew(
        val id: Int? = null,
        var speciality: Int? = null,
        var degree: Int? = null,
        var showInProfile: Boolean? = null
)

data class EducationLevelNew(
        val id: Int?,
        val name: String,
        val order: Int?
)

data class ToggleIntModelNew(
        var value: Int? = null,
        var showInProfile: Boolean? = null
)
