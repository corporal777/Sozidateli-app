package com.example.holders

import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class ProfileEducationGroup(
        private val educationLevel: String?,
        private val edit: Boolean
) : NestedGroup() {

    private val items = mutableListOf<Group>()

    init {
        val educationLevelItem = if (edit) ProfileDataEditEducationLevelItem(educationLevel)
        else ProfileDataEducationLevelItem(educationLevel)
        add(educationLevelItem)
    }

    override fun getGroup(position: Int): Group {
        return items[position]
    }

    override fun getPosition(group: Group): Int {
        return items.indexOf(group)
    }

    override fun getGroupCount(): Int {
        return items.size
    }

    override fun add(group: Group) {
        items.add(group)
        super.add(group)
    }
}