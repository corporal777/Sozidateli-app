package com.example.ui.support.items

import com.example.holders.BindExpandableTitleGroup
import com.example.holders.ExpandableTitleGroup
import com.example.holders.OnGroupExpandChange
import com.example.holders.ProfileExpandableTitleItem
import com.xwray.groupie.Group

class SupportQuestionExpandableTitleGroup(
    subtitle: String,
    isInitiallyExpanded: Boolean = false,
    onExpandChange: OnGroupExpandChange<SupportQuestionExpandableTitleItem>
) : BindExpandableTitleGroup<SupportQuestionExpandableTitleItem>(
    SupportQuestionExpandableTitleItem(
        subtitle
    ), isInitiallyExpanded, onExpandChange
) {

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