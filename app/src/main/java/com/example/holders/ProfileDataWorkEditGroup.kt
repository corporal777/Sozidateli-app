package com.example.holders

import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.example.holders.ActionButtonItem.Companion.ACTION_ADD_RECORD
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileDataWorkEditGroup(
        workList: List<SocialRoles>,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit
) : NestedGroup() {

    private val works = mutableListOf<ProfileDataWorkEditItem>()
    private val addItem = ActionButtonItem(0L, ACTION_ADD_RECORD) { add(createWorkItem(null)) }
    private val saveItem = ProfileDataEditSaveItem(1L, {
        if (checkDataValid()) saveClickListener(getDataToSave())
    }, {
        cancelClickListener()
    })

    init {
        workList.map { createWorkItem(it) }.let {
            works.addAll(it)
            addAll(it)
        }
        add(addItem)
        add(saveItem)
    }

    override fun getGroup(position: Int): Group {
        return if (position > works.size - 1) {
            when (position - works.size) {
                0 -> addItem
                1 -> saveItem
                else -> throw IndexOutOfBoundsException("Invalid item position: $position")
            }
        } else {
            works[position]
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            addItem -> works.size
            saveItem -> works.size + 1
            else -> works.indexOf(group)
        }
    }

    override fun getGroupCount(): Int {
        return works.size + 2
    }

    private fun add(item: ProfileDataWorkEditItem) {
        works.add(item)
        super.add(item)
        notifyItemInserted(works.size)
    }

    private fun createWorkItem(socialRoles: SocialRoles?): ProfileDataWorkEditItem {
        return ProfileDataWorkEditItem(
                socialRoles?.begin,
                socialRoles?.end,
                socialRoles?.organization,
                socialRoles?.position,
                socialRoles?.description
        ) { item ->
            val position = getItemCountBeforeGroup(item)
            remove(item)
            works.remove(item)
            notifyItemRemoved(position)
        }
    }

    private fun checkDataValid(): Boolean {
        var isValid = true
        works.forEach {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }
        return isValid
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mapOf(
                User.FIELD_WORK to works.map {
                    mapOf(
                            SocialRoles.FIELD_BEGIN to it.mStart,
                            SocialRoles.FIELD_END to if (it.isNotFinished) null else it.mFinish,
                            SocialRoles.FIELD_ORGANIZATION to it.mOrganization,
                            SocialRoles.FIELD_POSITION to it.mPosition,
                            SocialRoles.FIELD_DESCRIPTION to it.mDescription
                    )
                }
        )
    }
}