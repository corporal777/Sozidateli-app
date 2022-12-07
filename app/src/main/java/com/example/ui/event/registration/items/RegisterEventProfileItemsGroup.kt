package com.example.ui.event.registration.items

import android.util.Log
import com.example.extensions.findItemBy
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class RegisterEventProfileItemsGroup(
    profileForm: ProfileFieldsFormResult,
    onShowProfileClick: () -> Unit
) : NestedGroup() {

    private val mainItem = RegisterEventProfileMainItem(
        profileForm.user_birthday,
        profileForm.user_gender,
        profileForm.address,
        profileForm.user_notes,
        profileForm.user_email,
        profileForm.user_phone,
        profileForm.user_work_phone,
        profileForm.user_links,
        profileForm.user_sites,
        profileForm.user_public_email,
        profileForm.user_files
    )

    private val educationSection = Section()

    private val workSection = Section().apply {
        setFooter(RegisterEventProfileFooterItem(profileForm.checkProfileFieldsIsValid(profileForm)) {
            onShowProfileClick.invoke()
        })
    }

    private var oldWork = profileForm.work_experience
    private var oldEducation = profileForm.education
    private var oldProfileForm = profileForm

    init {
        setEducation(profileForm)
        setWork(profileForm)
        add(mainItem)
        add(educationSection)
        add(workSection)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mainItem
            1 -> educationSection
            2 -> workSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mainItem -> 0
            educationSection -> 1
            workSection -> 2
            else -> -1
        }
    }

    private fun setWork(profileForm: ProfileFieldsFormResult) {
        if (profileForm.work_experience.isChosen) {
            if (profileForm.work_experience.value.isNullOrEmpty()) {
                workSection.update(
                    listOf(
                        RegisterEventProfileWorkItem(
                            profileForm.work_experience.isRequired,
                            profileForm.work_experience.isAbsent,
                            true,
                            null
                        )
                    )
                )
            } else {
                workSection.update(
                    profileForm.work_experience.value?.mapIndexed { index, workExperience ->
                        RegisterEventProfileWorkItem(
                            profileForm.work_experience.isRequired,
                            profileForm.work_experience.isAbsent,
                            index == 0,
                            workExperience
                        )
                    } ?: emptyList()
                )
            }
        }
    }

    private fun setEducation(profileForm: ProfileFieldsFormResult) {

        val fieldsRequired = profileForm.fieldsIsRequired

        if (profileForm.education.isChosen || profileForm.academic_degree.isChosen) {
            educationSection.update(
                listOf(
                    RegisterEventProfileEducationLevelItem(
                        profileForm.educationLevel.value?.name,
                        fieldsRequired,
                        profileForm.education.value.isNullOrEmpty(),
                        profileForm.academic_degree.value.isNullOrEmpty()
                    )
                ).plus(
                    profileForm.academic_degree.value?.map {
                        RegisterEventAcademicDegreeItem(
                            it.degree?.name,
                            it.speciality?.name
                        )
                    } ?: emptyList()
                ).plus(
                    profileForm.education.value?.map { educationModel ->
                        RegisterEventEducationItem(
                            educationModel.organization,
                            educationModel.speciality,
                            educationModel.begin,
                            educationModel.end,
                        )
                    } ?: emptyList()
                )
            )
        }
    }


    fun updateProfileFields(profileForm: ProfileFieldsFormResult) {
        if (oldProfileForm != profileForm) {
            mainItem.updateFields(profileForm)
            oldProfileForm = profileForm
        }
        if (profileForm.education != oldEducation) {
            setEducation(profileForm)
            oldEducation = profileForm.education
        }
        if (profileForm.work_experience != oldWork) {
            setWork(profileForm)
            oldWork = profileForm.work_experience
        }
        val isValid = profileForm.checkProfileFieldsIsValid(profileForm)
        workSection.findItemBy<RegisterEventProfileFooterItem> { true }?.updateFooterText(isValid)
    }

    override fun getGroupCount() = 3
}