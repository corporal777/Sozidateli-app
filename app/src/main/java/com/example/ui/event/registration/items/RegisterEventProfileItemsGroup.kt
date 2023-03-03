package com.example.ui.event.registration.items

import android.util.Log
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class RegisterEventProfileItemsGroup(
    profileForm: ProfileFieldsFormResult,
    private val onShowProfileClick: () -> Unit
) : NestedGroup() {

    private val headerSection = Section()
    private val mainSection = Section()
    private val educationSection = Section()
    private val workSection = Section()

    private var oldWork = profileForm.work_experience
    private var oldEducation = profileForm.education
    private var oldProfileForm = profileForm

    init {
        setMain(profileForm)
        setEducation(profileForm)
        setWork(profileForm)
        headerSection.registerGroupDataObserver(this)
        mainSection.registerGroupDataObserver(this)
        educationSection.registerGroupDataObserver(this)
        workSection.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> headerSection
            1 -> mainSection
            2 -> educationSection
            3 -> workSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            headerSection -> 0
            mainSection -> 1
            educationSection -> 2
            workSection -> 3
            else -> -1
        }
    }

    private fun setMain(profileForm: ProfileFieldsFormResult) {
        headerSection.updateItem(
            RegisterEventProfileHeaderItem(
                100L,
                profileForm.checkProfileFieldsIsValid(profileForm)
            ) { onShowProfileClick.invoke() }
        )

        mainSection.updateItem(
            RegisterEventProfileMainItem(
                101L,
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
        )
    }

    private fun setWork(profileForm: ProfileFieldsFormResult) {
        if (profileForm.work_experience.isChosen) {
            if (profileForm.work_experience.value.isNullOrEmpty()) {
                workSection.updateItem(
                    RegisterEventProfileWorkItem(
                        200L,
                        profileForm.work_experience.isRequired,
                        profileForm.work_experience.isAbsent,
                        true,
                        null
                    )
                )
            } else {
                workSection.update(
                    profileForm.work_experience.value?.mapIndexed { index, workExperience ->
                        RegisterEventProfileWorkItem(
                            200L + index,
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
                        103L,
                        profileForm.educationLevel.value?.name,
                        fieldsRequired,
                        profileForm.education.value.isNullOrEmpty(),
                        profileForm.academic_degree.value.isNullOrEmpty()
                    )
                ).plus(
                    profileForm.academic_degree.value?.mapIndexed { index, p ->
                        RegisterEventAcademicDegreeItem(
                            300L + index,
                            p.degree,
                            p.speciality
                        )
                    } ?: emptyList()
                ).plus(
                    profileForm.education.value?.mapIndexed { index, educationModel ->
                        RegisterEventEducationItem(
                            400L + index,
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

        setMain(profileForm)
        setEducation(profileForm)
        setWork(profileForm)

//        if (oldProfileForm != profileForm) {
//            headerSection.findItemBy<RegisterEventProfileMainItem> { true }?.updateFields(profileForm)
//            oldProfileForm = profileForm
//        }
//        if (profileForm.education != oldEducation) {
//            setEducation(profileForm)
//            oldEducation = profileForm.education
//        }
//        if (profileForm.work_experience != oldWork) {
//            setWork(profileForm)
//            oldWork = profileForm.work_experience
//        }
//        val isValid = profileForm.checkProfileFieldsIsValid(profileForm)
//        headerSection.findItemBy<RegisterEventProfileHeaderItem> { true }?.updateFooterText(isValid)
    }

    override fun getGroupCount() = 4
}