package com.example.ui.editwork.items

import android.content.Context
import android.util.Log
import android.view.View
import com.example.R
import com.example.data.models.FieldDetails
import com.example.data.models.WorkExperience
import com.example.data.models.WorkExperienceModel
import com.example.data.models.WorkExperienceServerModel
import com.example.extensions.forEachGroups
import com.example.extensions.updateItem
import com.example.holders.ButtonAddMore
import com.example.holders.ProfileButtonEditItem
import com.example.holders.ProfileDataNoExperienceItem
import com.example.holders.ProfileDataWorkEditItem
import com.example.ui.views.NoWorkDialog
import com.xwray.groupie.Group
import com.xwray.groupie.Item
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class UserWorksGroup(
    context: Context,
    private val birthday: FieldDetails?,
    private val work: WorkExperienceModel?,
    private val enableNextButton: (enable: Boolean) -> Unit
) : NestedGroup() {

    private var isNoExperience = work?.absent ?: false

    private val worksSection = Section()

    private val checkBoxNoWork = ProfileDataNoExperienceItem {
        if (it && isDataNotEmpty()) {
            NoWorkDialog(context)
                .setSelectCallback { isDelete ->
                    if (isDelete) {
                        setNoExperience(it)
                        isWorkEditable(it)
                        validateEnableButton()
                    } else setNoExperience(!it)
                }
        } else {
            setNoExperience(it)
            isWorkEditable(it)
            validateEnableButton()
        }
    }

    private val addWorkButton = ButtonAddMore(context.getString(R.string.add_record)) {
        if (checkDataValid()) {
            worksSection.add(createWorkItem(null))
            isDeleteVisible()
        }
    }.apply {
        setButtonVisibility(View.VISIBLE)
        setButtonEnabled(!isNoExperience)
    }


    init {
        checkBoxNoWork.registerGroupDataObserver(this)
        setNoExperience(isNoExperience)

        worksSection.apply {
            if (work != null && !work.models.isNullOrEmpty()) {
                update(work.models!!.map { createWorkItem(it) })
            } else updateItem(createWorkItem(null))
            isDeleteVisible()
        }

        worksSection.registerGroupDataObserver(this)
        addWorkButton.registerGroupDataObserver(this)
    }

    private fun setNoExperience(has: Boolean) {
        isNoExperience = has
        checkBoxNoWork.setNoExperience(has)
    }

    private fun isWorkEditable(enable: Boolean) {
        worksSection.forEachGroups<UserWorkItem> {
            it.isFieldsEnabled = !enable
            it.isEnabledItems()
        }
        addWorkButton.setButtonEnabled(!enable)
    }

    private fun isDeleteVisible() {
        try {
            val isVisible = worksSection.itemCount > 1
            worksSection.forEachGroups<UserWorkItem> {
                it.isDeleteVisible = isVisible
                it.setButtonRemove()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun isDataNotEmpty(): Boolean {
        var isNotEmpty = false
        worksSection.forEachGroups<UserWorkItem> {
            if (it.isDataNotEmpty()) {
                isNotEmpty = true
                return@forEachGroups
            }
        }
        return isNotEmpty
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (!isNoExperience) {
            worksSection.forEachGroups<UserWorkItem> {
                if (!it.isDataValid()) {
                    isValid = false
                    it.showErrors()
                }
            }
        }
        return isValid
    }

    private fun createWorkItem(socialRoles: WorkExperience?): UserWorkItem {
        return UserWorkItem(
            socialRoles?.id,
            socialRoles?.begin,
            socialRoles?.end,
            socialRoles?.organization,
            socialRoles?.position,
            birthday?.value,
            socialRoles?.showInProfile
        ) { item ->
            worksSection.remove(item)
            isDeleteVisible()
            validateEnableButton()
        }.apply {
            this.isFieldsEnabled = !isNoExperience
            isEnabledItems()
            onDataValidCallback = {
                validateEnableButton()
            }
        }
    }

    private fun validateEnableButton() {
        var isValid = true
        if (isNoExperience) enableNextButton(true)
        else {
            worksSection.forEachGroups<UserWorkItem> {
                if (!it.isDataValid()) {
                    isValid = false
                    return@forEachGroups
                }
            }
            enableNextButton(isValid)
        }
    }


    fun getDataToSave(): WorkExperienceServerModel {
        return if (isNoExperience) WorkExperienceServerModel(absent = true, data = arrayListOf())
        else {
            val works = arrayListOf<WorkExperience>()
            worksSection.forEachGroups<UserWorkItem> { works.add(it.getDataToSave()) }
            WorkExperienceServerModel(absent = false, data = works)
        }
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> checkBoxNoWork
            1 -> worksSection
            2 -> addWorkButton
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            checkBoxNoWork -> 0
            worksSection -> 1
            addWorkButton -> 2
            else -> -1
        }
    }

    override fun getGroupCount(): Int {
        return 3
    }
}