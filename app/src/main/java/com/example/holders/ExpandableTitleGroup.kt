package com.example.holders

import com.xwray.groupie.ExpandableGroup

abstract class ExpandableTitleGroup<T : ExpandableTitleItem>(
        val titleItem: T,
        val onExpandChange: OnExpandChange<T>
) : ExpandableGroup(titleItem) {

    override fun onToggleExpanded() {
        super.onToggleExpanded()
        titleItem.notifyChanged(isExpanded)
        onExpandChange(this)
    }
}

typealias OnExpandChange<T> = (ExpandableTitleGroup<T>) -> Unit