package com.example.holders

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

abstract class BindExpandableTitleGroup <T : BindExpandableTitleItem<*>>(
    val titleItem: T,
    isInitiallyExpanded: Boolean = false,
    val onExpandChange: OnGroupExpandChange<T>
) : ExpandableGroup(titleItem, isInitiallyExpanded) {

    init {
        titleItem.isExpanded = isExpanded
    }

    override fun onToggleExpanded() {
        super.onToggleExpanded()
        titleItem.notifyChanged(isExpanded)
        onExpandChange(this)
    }
}

typealias OnGroupExpandChange<T> = (BindExpandableTitleGroup<T>) -> Unit