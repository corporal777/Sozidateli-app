package com.example.holders.registerEvent

import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventRegistrationTitleGroup(
        title: String?,
        private val data: Group
) : NestedGroup() {

    private val titleItem = EventRegistrationTitleItem(title)

    init {
        add(titleItem)
        add(data)
    }

    override fun getGroup(position: Int): Group {
        return if (position == 0) titleItem
        else data
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            titleItem -> 0
            data -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2
}

fun Group.withEventRegistrationTitle(title: String?): Group {
    return EventRegistrationTitleGroup(title, this)
}