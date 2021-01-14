package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.AcademicDegree
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.extensions.forEachGroups
import com.example.extensions.forEachItems
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class ProfileDataEducationEditGroup(
        context: Context,
        private val birthday: String?,
        private val educationLevel: String?,
        private val availableEducations: List<String>,
        private val availableDegrees: List<String>,
        private val availableSciences: List<String>,
        education: List<SocialRoles>,
        academicDegrees: List<AcademicDegree>
) : NestedGroup() {

    var validatorSize = 3

    private val educationLevelItem = ProfileDataEducationLevelEditItem(educationLevel, availableEducations) {
        if (it) {
            validatorSize = 4
            if (degrees.itemCount == 0) {
                degrees.add(createAcademicDegreeEditItem(null, null))
                isDeleteVisible()
            }
        } else {
            validatorSize= 3
            degrees.clear()
        }
    }

    private val degrees = Section().apply {
        setHideWhenEmpty(true)
        setFooter(ProfileButtonEditItem(context.getString(R.string.profile_sciences_add), false) {
            if (checkDataValid()) {
                add(createAcademicDegreeEditItem(null, null))
                isDeleteVisible()
            }
        }.apply {
            hasDivider = false
            compactMargin = true
        })
    }
    private val educations = Section().apply {
        setFooter(ProfileButtonEditItem(context.getString(R.string.profile_institution_add), false) {
            if (checkDataValid()) {
                add(createEducationItem(null))
                isDeleteVisible()
            }
        }.apply {
            hasDivider = false
            compactMargin = true
        })
    }

    init {
        academicDegrees.map { createAcademicDegreeEditItem(it.degree, it.specialisation) }.let {
            if (educationLevel != null && educationLevel == availableEducations.lastOrNull()) {
                validatorSize = 4
                if (it.isEmpty()) {
                    degrees.add(createAcademicDegreeEditItem(null, null))
                } else {
                    degrees.addAll(it)
                }
            }
        }
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
                educations
        ))
        isDeleteVisible()
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> educationLevelItem
            1 -> degrees
            2 -> educations
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            educationLevelItem -> 0
            degrees -> 1
            educations -> 2
            else -> -1
        }
    }

    private fun createAcademicDegreeEditItem(degree: String?, specialisation: String?): ProfileDataAcademicDegreeEditItem {
        return ProfileDataAcademicDegreeEditItem(degree, specialisation, availableDegrees, availableSciences) {
            degrees.remove(it)
            if (degrees.itemCount == 0) degrees.add(createAcademicDegreeEditItem(null, null))
            isDeleteVisible()
        }
    }

    private fun createEducationItem(socialRoles: SocialRoles?): ProfileDataEducationEditItem {
        return ProfileDataEducationEditItem(
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.specialty,
                birthday
        ) {
            educations.remove(it)
            isDeleteVisible()
        }
    }

    private fun isDeleteVisible() {
        this.forEachItems { item, position ->
            if (this.itemCount <= validatorSize) {
                when (item) {
                    is ProfileDataEducationEditItem -> {
                        item.isDeleteVisible = position != 1
                    }
                    is ProfileDataAcademicDegreeEditItem -> {
                        item.isDeleteVisible = position != 1
                    }
                }
            } else {
                when (item) {
                    is ProfileDataEducationEditItem -> {
                        item.isDeleteVisible = true
                    }
                    is ProfileDataAcademicDegreeEditItem -> {
                        item.isDeleteVisible = true
                    }
                }
            }
            notifyChanged()
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (!educationLevelItem.isDataValid()) {
            isValid = false
            educationLevelItem.notifyChanged(true)
        }

        degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }

        educations.forEachGroups<ProfileDataEducationEditItem> {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }

        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        val degrees = mutableListOf<AcademicDegree>()
        this.degrees.forEachGroups<ProfileDataAcademicDegreeEditItem> {
            degrees.add(AcademicDegree(it.mDegreesLevel, it.mSciencesLevel))
        }

        val educations = mutableListOf<Map<String, Any?>>()
        this.educations.forEachGroups<ProfileDataEducationEditItem> {
            educations.add(mapOf(
                    SocialRoles.FIELD_BEGIN to it.mStart,
                    SocialRoles.FIELD_END to if (it.isNotFinished) null else it.mFinish,
                    SocialRoles.FIELD_ORGANIZATION to it.mInstitution,
                    SocialRoles.FIELD_SPECIALITY to it.mSpeciality
            ))
        }

        return mapOf(
                User.FIELD_USER_EDUCATION to educationLevelItem.mEducationLevel,
                User.FIELD_ACADEMIC_DEGREE to degrees,
                User.FIELD_EDUCATION to educations
        )
    }

    override fun getGroupCount(): Int {
        return 3
    }
}