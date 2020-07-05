package com.example.holders.registerEvent

import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventRegistrationPersonalDataFileGroup(
        url: String?,
        description: String?,
        private val data: Group,
        onFileClickListener: OnPersonalDataFileClickListener
) : NestedGroup() {

    private val fileItem = EventRegistrationPersonalDataFileItem(url, description, onFileClickListener)

    init {
        add(data)
        add(fileItem)
    }

    override fun getGroup(position: Int): Group {
        return if (position == 0) data
        else fileItem
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            data -> 0
            fileItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2
}

fun Group.withEventRegistrationPersonalDataFile(
        url: String?,
        description: String?,
        onFileClickListener: OnPersonalDataFileClickListener
): Group {
    return EventRegistrationPersonalDataFileGroup(url, description, this, onFileClickListener)
}