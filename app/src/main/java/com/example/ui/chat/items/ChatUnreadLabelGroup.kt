package com.example.ui.chat.items

import com.example.data.models.EventNew
import com.example.holders.ChatUnreadLabelItem
import com.example.holders.redesign.EventItemNew
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ChatUnreadLabelGroup(
    private val count: Int
) : NestedGroup() {
    private val unreadLabelItem = ChatUnreadLabelItem(count)

    init {
        unreadLabelItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> unreadLabelItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            unreadLabelItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 1
}