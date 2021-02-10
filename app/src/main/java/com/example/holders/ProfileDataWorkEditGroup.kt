package com.example.holders

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.models.*
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.data.models.user.UserData
import com.example.extensions.findItemBy
import com.example.ui.views.NoWorkDialog
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import okhttp3.internal.notifyAll

class ProfileDataWorkEditGroup(
        context: Context,
        private val birthday: FieldDetails?,
        work: WorkExperienceModel?,
        private val exeption: (workCheckB: Boolean) -> Unit
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

    init {
        add(noWork)
        work?.models?.map { createWorkItem(it) }.let {
            if (it?.isEmpty() == true) {
                add(createWorkItem(null))
            } else {
                it?.let { it1 ->
                    works.addAll(it1)
                    addAll(it1)
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
    }

    private fun isWorkEditable(it: Boolean) {
        try {
            addItem.hasExp(it)
            notifyItemChanged(works.size + 1)
            isDeleteVisible(it)
            hasWork = !it
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
        return ProfileDataWorkEditItem(
                socialRoles?.id,
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.position,
                birthday?.value
        ) { item ->
            val position = getItemCountBeforeGroup(item) + 1
            remove(item)
            works.remove(item)
            isDeleteVisible(!hasWork)
            notifyItemRemoved(position)
        }
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
                        organization = it.mOrganization, position = it.mPosition, description = "")
            })
        else
            WorkExperienceServerModel(absent = !hasWork, data = arrayListOf())
    }
}