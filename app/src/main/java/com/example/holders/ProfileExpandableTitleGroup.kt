package com.example.holders

import com.xwray.groupie.Group

class ProfileExpandableTitleGroup(
        subtitle: String,
        isInitiallyExpanded: Boolean = false,
        onExpandChange: OnExpandChange<ProfileExpandableTitleItem>
) : ExpandableTitleGroup<ProfileExpandableTitleItem>(ProfileExpandableTitleItem(subtitle), isInitiallyExpanded, onExpandChange) {

    private val childList = mutableListOf<Group>()

    override fun add(group: Group) {
        super.add(group)
        childList.add(group)
    }

    override fun addAll(groups: MutableCollection<out Group>) {
        super.addAll(groups)
        childList.addAll(groups)
    }

    override fun addAll(position: Int, groups: MutableCollection<out Group>) {
        super.addAll(position, groups)
        childList.addAll(position, groups)
    }

    override fun remove(group: Group) {
        super.remove(group)
        childList.remove(group)
    }

    override fun removeAll(groups: MutableCollection<out Group>) {
        super.removeAll(groups)
        childList.removeAll(groups)
    }

    fun clear() {
        removeAll(childList.toMutableList())
    }
}