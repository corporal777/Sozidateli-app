package com.example.holders

import com.xwray.groupie.ExpandableGroup

abstract class ExpandableTitleGroup<T: ExpandableTitleItem>(
        val titleItem: T
) : ExpandableGroup(titleItem) {

    override fun onToggleExpanded() {
        super.onToggleExpanded()
        titleItem.notifyChanged(isExpanded)
    }
}