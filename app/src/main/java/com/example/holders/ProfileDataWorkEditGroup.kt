package com.example.holders

import android.content.Context
import com.example.R
import com.example.data.models.user.SocialRoles
import com.example.data.models.user.User
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileDataWorkEditGroup(
        context: Context,
        workList: List<SocialRoles>
) : NestedGroup() {

    private val works = mutableListOf<ProfileDataWorkEditItem>()
    private val addItem = ProfileButtonEditItem(context.getString(R.string.add_record)) { add(createWorkItem(null)) }.apply {
        hasDivider = false
        compactMargin = true
    }

    init {
        workList.map { createWorkItem(it) }.let {
            works.addAll(it)
            addAll(it)
        }
        add(addItem)
    }

    override fun getGroup(position: Int): Group {
        return if (position > works.size - 1) {
            when (position - works.size) {
                0 -> addItem
                else -> throw IndexOutOfBoundsException("Invalid item position: $position")
            }
        } else {
            works[position]
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            addItem -> works.size
            else -> works.indexOf(group)
        }
    }

    override fun getGroupCount(): Int {
        return works.size + 1
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
                socialRoles?.position
        ) { item ->
            val position = getItemCountBeforeGroup(item)
            remove(item)
            works.remove(item)
            notifyItemRemoved(position)
        }
    }

    fun checkDataValid(): Boolean {
        var isValid = true
        works.forEach {
            if (!it.isDataValid()) {
                isValid = false
                it.notifyChanged(true)
            }
        }
        return isValid
    }

    fun getDataToSave(): Map<String, Any?> {
        return mapOf(
                User.FIELD_WORK to works.map {
                    mapOf(
                            SocialRoles.FIELD_BEGIN to it.mStart,
                            SocialRoles.FIELD_END to if (it.isNotFinished) null else it.mFinish,
                            SocialRoles.FIELD_ORGANIZATION to it.mOrganization,
                            SocialRoles.FIELD_POSITION to it.mPosition
                    )
                }
        )
    }
}