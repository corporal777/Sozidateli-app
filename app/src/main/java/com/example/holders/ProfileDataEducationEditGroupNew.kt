package com.example.holders

import android.content.Context
import android.util.Log
import android.view.View
import com.example.R
import com.example.data.models.*
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.extensions.forEachGroups
import com.example.extensions.forEachItems
import com.example.util.DEGREES_MAX_SIZE
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class ProfileDataEducationEditGroupNew(
        context: Context,
        private val birthday: FieldDetails?,
        private val educationLevel: ToggleIntModel?,
        private val availableEducations: List<EducationLevel>,
        private val availableDegrees: List<EducationLevel>,
        private val availableSciences: List<EducationLevel>,
        education: List<EducationModel>,
        academicDegrees: List<AcademicDegreeModel>,
        private val enableNextButton:(enable: Boolean) -> Unit
) : NestedGroup() {

    var isEducationLevelValid = false
    var isDegreeValid = true
    var isEducationValid = false
    var validatorSize = 4
    var hasAcademicDegree = false
    var data: MutableList<ProfileDataAcademicDegreeEditItem> = mutableListOf()

    private val educationLevelItem = ProfileDataEducationLevelEditItem(availableEducations.firstOrNull { it.id == educationLevel?.value }?.name ,
            availableEducations, academicDegrees.isNotEmpty(), educationLevel?.showInProfile, {
        if (it) {
            validatorSize = 4
            if (degrees.itemCount == 0) {
                degrees.add(createAcademicDegreeEditItem(null, availableDegrees.first(), null, false))
                isDeleteVisible()
            }
        } else {
            validatorSize= 3
            degrees.clear()
        }
    }, {
        hasAcademicDegree = it
        if (it) {
            isDeleteVisible()
            addDegreeButton.setButtonVisibility(View.VISIBLE)
            if (data.isEmpty() && degrees.itemCount == 0) {
                degrees.add(createAcademicDegreeEditItem(null, availableDegrees.first(), null, false))
                isDeleteVisible()
            }
            returnDegree()
        } else {
            addDegreeButton.setButtonVisibility(View.GONE)
            saveDegree()
            degrees.clear()
        }
        isEducationLevelValid = true
        validateEnableButton()
    })

    private val addDegreeButton = ButtonAddMore(context.getString(R.string.profile_sciences_add)) {
        if (degrees.itemCount < DEGREES_MAX_SIZE)
            degrees.add(createAcademicDegreeEditItem(null, availableDegrees.first(), null, false))
        isDeleteVisible()
    }

    private fun saveDegree() {
        data = mutableListOf()
        degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> { group ->
            data.add(group)
        }
    }

    private fun returnDegree() {
        if (data.isNotEmpty()) {
            degrees.addAll(data)
            data = mutableListOf()
        }
    }

    private val degrees = Section().apply {
        setHideWhenEmpty(true)
    }

    private val educations = Section().apply {
        setFooter(ProfileButtonEditItem(context.getString(R.string.profile_institution_add), false) {
            if (checkDataValid()) {
                add(createEducationItem(null))
                isDeleteVisible()
                isEducationValid = false
                validateEnableButton()
            }
        }.apply {
            hasDivider = false
            compactMargin = true
        })
    }

    init {
        if (academicDegrees.isNotEmpty()) {
            academicDegrees.map { createAcademicDegreeEditItem(it.id, availableDegrees.firstOrNull { degree -> degree.id == it.degree } ,
                    availableSciences.firstOrNull { science -> science.id == it.speciality }?.name, it.showInProfile) }.let {
                val edLevel = availableEducations.firstOrNull { avEd -> avEd.id == educationLevel?.value }?.name
                if (educationLevel != null && (edLevel == availableEducations.lastOrNull()?.name || edLevel == availableEducations[availableEducations.size - 2].name)) {
                    validatorSize = 4
                    if (it.isEmpty()) {
                        degrees.add(createAcademicDegreeEditItem(null, availableDegrees.first(), null, false))
                    } else {
                        degrees.addAll(it)
                    }
                }
            }
            addDegreeButton.setButtonVisibility(View.VISIBLE)
        } else
            addDegreeButton.setButtonVisibility(View.GONE)

        education.map { createEducationItem(it) }.let {
            if (it.isEmpty()) {
                educations.add(createEducationItem(null))
            } else {
                educations.addAll(it)
            }
        }
        addAll(listOf(
                educationLevelItem,
                degrees,
                addDegreeButton,
                educations
        ))
        isDeleteVisible()
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> educationLevelItem
            1 -> degrees
            2 -> addDegreeButton
            3 -> educations
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            educationLevelItem -> 0
            degrees -> 1
            addDegreeButton -> 2
            educations -> 3
            else -> -1
        }
    }

    private fun validateEnableButton() {
        enableNextButton(isDegreeValid && isEducationValid && isEducationLevelValid)
    }

    private fun createAcademicDegreeEditItem(id: Int?, degree: EducationLevel?, specialisation: String?, showInProfile: Boolean?): ProfileDataAcademicDegreeEditItem {
        return ProfileDataAcademicDegreeEditItem(id, degree?.name, specialisation, availableDegrees, availableSciences, showInProfile) {
            degrees.remove(it)
            if (degrees.itemCount == 0) degrees.add(createAcademicDegreeEditItem(null, availableDegrees.first(), null, false))
            isDeleteVisible()
        }
    }

    private fun createEducationItem(socialRoles: EducationModel?): ProfileDataEducationEditItem {
        isEducationValid = false
        validateEnableButton()
        return ProfileDataEducationEditItem(
                socialRoles?.id,
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.speciality,
                birthday,
                socialRoles?.showInProfile,
        {
            educations.remove(it)
            isDeleteVisible()
        }, {
            isEducationValid = it
            validateEnableButton()
        })
    }

    private fun isDeleteVisible() {
        val isDeleteD = degrees.itemCount > 1
        degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            it.isDeleteVisible = isDeleteD
            notifyChanged()
        }
        val isDeleteE = educations.itemCount > 2
        educations.forEachGroups<ProfileDataEducationEditItem> {
            it.isDeleteVisible = isDeleteE
            notifyChanged()
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (!educationLevelItem.isDataValid()) {
            isValid = false
            educationLevelItem.notifyChanged(true)
        }

        /*degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }*/

        educations.forEachGroups<ProfileDataEducationEditItem> {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }

        return isValid
    }

    fun getDegreeToSave(): List<AcademicDegreeModel>? {
        val degrees = mutableListOf<AcademicDegreeModel>()
        this.degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            degrees.add(AcademicDegreeModel(
                    id = it.mId,
                    speciality = availableSciences.firstOrNull { degree -> degree.name == it.mSciencesLevel }?.id,
                    degree = availableDegrees.firstOrNull { degree -> degree.name == it.mDegreesLevel }?.id,
                    showInProfile = it.mShowInProfile
            ))
        }
        return degrees
    }

    fun getEducationsToSave(): List<EducationModel>? {
        val educations = mutableListOf<EducationModel>()
        this.educations.forEachGroups<ProfileDataEducationEditItem> {
            educations.add(EducationModel(id = it.mId, begin = it.mStart,
            end = it.mFinish, organization = it.mInstitution, speciality = it.mSpeciality, showInProfile = it.mShowInProfile))
        }
        return educations
    }

    fun getEducationLevelToSave(): ToggleIntModel? = ToggleIntModel(availableEducations.firstOrNull { it.name == educationLevelItem.mEducationLevel }?.id, educationLevelItem.mShowInProfile)

    override fun getGroupCount(): Int {
        return 4
    }
}