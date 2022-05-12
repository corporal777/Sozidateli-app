package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.*
import com.example.ui.views.NoWorkDialog
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileDataWorkEditGroup(
        context: Context,
        private val birthday: FieldDetails?,
        private val work: WorkExperienceModel?,
        private val exeption: (workCheckB: Boolean) -> Unit,
        private val enableNextButton:(enable: Boolean) -> Unit
) : NestedGroup() {

    private var hasWork = work?.absent?: false

    private val noWork = ProfileDataNoExperienceItem {
        if (it && works.size >= 1 && !works[0].mOrganization.isNullOrEmpty()) {
            NoWorkDialog(context)
                    .setSelectCallback { isDelete ->
                        if (isDelete)
                            isWorkEditable(it)
                        else setHasWork()
                    }
        } else isWorkEditable(it)
    }
    private val works = mutableListOf<ProfileDataWorkEditItem>()
    private val addItem = ProfileButtonEditItem(context.getString(R.string.add_record), false) { if (checkDataValid()) { add(createWorkItem(null)) } }.apply {
        hasDivider = false
        compactMargin = true
    }
    private var isWorksValid = false

    init {
        isWorksValid = work?.models?.firstOrNull { it.description == null } != null
        add(noWork)
        work?.models?.map { createWorkItem(it) }.let {
            if (it?.isEmpty() == true) {
                add(createWorkItem(null))
            } else {
                it?.forEach { it1 ->
                    add(it1)
                }
            }
        }
        add(addItem)
        isWorkEditable(work?.absent?: false)
        noWork.hasWork(work?.absent?: false)
    }

    private fun setHasWork() {
        noWork.hasWork(false)
        noWork.notifyChanged()
        validateEnableButton(hasWork)
    }

    private fun isWorkEditable(it: Boolean) {
        try {
            addItem.hasExp(it)
            notifyItemChanged(works.size + 1)
            isDeleteVisible(it)
            hasWork = !it
            validateEnableButton(hasWork)
        } catch (e: IllegalStateException) {
            exeption(!hasWork)
        }
    }

    override fun getGroup(position: Int): Group {
        return if (position > works.size) {
            when (position - works.size - 1) {
                0 -> addItem
                else -> throw IndexOutOfBoundsException("Invalid item position: $position")
            }
        } else if (position == 0) {
            noWork
        } else {
            works[position - 1]
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            noWork -> 0
            addItem -> works.size + 1
            else -> works.indexOf(group) + 1
        }
    }

    override fun getGroupCount(): Int {
        return works.size + 2
    }

    private fun add(item: ProfileDataWorkEditItem) {
        works.add(item)
        isDeleteVisible(!hasWork)
        super.add(item)
        notifyItemInserted(works.size)
    }

    private fun isDeleteVisible(hasWork: Boolean) {
        works.forEachIndexed { index, profileDataWorkEditItem ->
            profileDataWorkEditItem.hasExp(hasWork)
            if (works.size == 1) {
                profileDataWorkEditItem.isDeleteVisible = index != 0
            } else {
                profileDataWorkEditItem.isDeleteVisible = true
            }
            notifyItemChanged(index + 1)
        }
    }


    private fun createWorkItem(socialRoles: WorkExperience?): ProfileDataWorkEditItem {
        validateEnableButton(hasWork)
        return ProfileDataWorkEditItem(
                socialRoles?.id,
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.position,
                birthday?.value,
                socialRoles?.showInProfile,
        { item ->
            val position = getItemCountBeforeGroup(item) + 1
            remove(item)
            works.remove(item)
            isDeleteVisible(!hasWork)
            notifyItemRemoved(position)
        }, {
            isWorksValid = it
            validateEnableButton(hasWork)
        })
    }

    private fun validateEnableButton(hasWork: Boolean) {
        if (!hasWork) enableNextButton(true)
        else enableNextButton(isWorksValid)
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        if (hasWork) {
            works.forEach {
                if (!it.isDataValid()) {
                    isValid = false
                    it.notifyChanged(true)
                }
            }
        }
        return isValid
    }

    fun getDataToSave(): WorkExperienceServerModel {
        return if (hasWork)
            WorkExperienceServerModel(absent = !hasWork, data = works.map {
                WorkExperience(id = it.mId, begin = it.mStart, end = it.mFinish,
                        organization = it.mOrganization, position = it.mPosition, description = "",
                showInProfile = it.mShowInProfile)
            })
        else
            WorkExperienceServerModel(absent = !hasWork, data = arrayListOf())
    }
}