package com.example.holders

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.extensions.findItemBy
import com.example.ui.views.NoWorkDialog
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import okhttp3.internal.notifyAll

class ProfileDataWorkEditGroup(
        context: Context,
        private val birthday: String?,
        workList: List<SocialRoles>,
        private val workExperienceAbsent: Boolean,
        private val exeption: (workCheckB: Boolean) -> Unit
) : NestedGroup() {

    private var hasWork = workExperienceAbsent

    private val noWork = ProfileDataNoExperienceItem {
        if (it && addItem.itemCount > 0) {
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
        workList.map { createWorkItem(it) }.let {
            if (it.isEmpty()) {
                add(createWorkItem(null))
            } else {
                works.addAll(it)
                addAll(it)
            }
        }
        add(addItem)
        isWorkEditable(workExperienceAbsent)
        noWork.hasWork(workExperienceAbsent)
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


    private fun createWorkItem(socialRoles: SocialRoles?): ProfileDataWorkEditItem {
        return ProfileDataWorkEditItem(
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.position,
                birthday
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

    fun getDataToSave(): MutableMap<String, Any?> {
        return if (hasWork) {
            mutableMapOf(
                    User.FIELD_WORK to works.map {
                        mapOf(
                                SocialRoles.FIELD_BEGIN to it.mStart,
                                SocialRoles.FIELD_END to if (it.isNotFinished) null else it.mFinish,
                                SocialRoles.FIELD_ORGANIZATION to it.mOrganization,
                                SocialRoles.FIELD_POSITION to it.mPosition
                        )
                    },
                    User.FIELD_USER_HAS_WORK_EXPERIENCE to !hasWork
            )
        } else {
            mutableMapOf(
                    User.FIELD_WORK to arrayListOf<SocialRoles>(),
                    User.FIELD_USER_HAS_WORK_EXPERIENCE to !hasWork
            )
        }
    }
}