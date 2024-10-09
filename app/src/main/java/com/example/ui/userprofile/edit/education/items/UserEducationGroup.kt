package com.example.ui.userprofile.edit.education.items

import android.content.Context
import android.view.View
import com.example.app.R
import com.example.data.models.*
import com.example.extensions.findItemBy
import com.example.extensions.forEachGroups
import com.example.extensions.updateItem
import com.example.holders.ButtonAddMore
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class UserEducationGroup(
    context: Context,
    private val birthday: FieldDetails?,
    private val educationList: List<EducationModel>
) : NestedGroup() {

    private var selectedLevel = ""
    private var isEducationValid = false
    var enableNextButton: (isValid: Boolean) -> Unit = {}

    private val addOrganizationButton =
        ButtonAddMore(context.getString(R.string.profile_institution_add)) {
            if (checkDataValid()) {
                educationsSection.add(createEducationItem(null))
                isDeleteVisible()
            }
        }.apply { setButtonVisibility(View.VISIBLE) }

    private val educationsSection = Section().apply {
        setHideWhenEmpty(true)
    }


    init {
        if (!educationList.isNullOrEmpty()) {
            educationsSection.update(educationList.map { createEducationItem(it) })
        } else educationsSection.updateItem(createEducationItem(null))
        isDeleteVisible()

        educationsSection.registerGroupDataObserver(this)
        addOrganizationButton.registerGroupDataObserver(this)
    }

    private fun createEducationItem(education: EducationModel?): UserEducationItem {
        return UserEducationItem(
            education?.id,
            selectedLevel,
            education?.begin,
            education?.end,
            education?.organization,
            education?.speciality,
            birthday,
            education?.showInProfile,
            {
                educationsSection.remove(it)
                isDeleteVisible()
                refreshDataValid()
                validateEnableButton()
            }, {
                refreshDataValid()
                validateEnableButton()
            })
    }

    private fun isDeleteVisible() {
        val isDeleteE = educationsSection.itemCount > 1
        educationsSection.forEachGroups<UserEducationItem> {
            it.isDeleteVisible = isDeleteE
            it.setButtonDelete()
        }
    }

    private fun validateEnableButton() = enableNextButton(isEducationValid)
    fun isDataValid() = isEducationValid

    fun checkDataValid(): Boolean {
        var isValid = true
        educationsSection.forEachGroups<UserEducationItem> {
            if (!it.isDataValid()){
                it.showErrors()
                isValid = false
            }
        }
        return isValid
    }

    private fun refreshDataValid() {
        val item = educationsSection.findItemBy<UserEducationItem> { x -> !x.isDataValid() }
        isEducationValid = item?.isDataValid() ?: true
    }

    fun setSelectedLevel(level: String?) {
        selectedLevel = level ?: ""
        educationsSection.forEachGroups<UserEducationItem> {
            it.setSelectedLevel(selectedLevel)
        }
        refreshDataValid()
        validateEnableButton()
    }

    fun getEducationsToSave(): List<EducationModel>? {
        val educations = mutableListOf<EducationModel>()
        educationsSection.forEachGroups<UserEducationItem> {
            educations.add(it.getDataToSave())
        }
        return educations
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> educationsSection
            1 -> addOrganizationButton
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            educationsSection -> 0
            addOrganizationButton -> 1
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 2
    }
}