package com.example.holders

import com.xwray.groupie.ExpandableGroup

abstract class ExpandableTitleGroup<T : ExpandableTitleItem>(
        val titleItem: T,
        isInitiallyExpanded: Boolean = false,
        val onExpandChange: OnExpandChange<T>
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

typealias OnExpandChange<T> = (ExpandableTitleGroup<T>) -> Unit