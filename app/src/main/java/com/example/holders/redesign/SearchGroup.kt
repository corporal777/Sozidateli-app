package com.example.holders.redesign

import com.example.holders.EventStatusItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class SearchGroup(
    private val mTitle: String,
    private val mDataItem: Section,
    val onEventClickListener: EventStatusItem.OnEventClickListener
) : NestedGroup() {

    private val mHeaderItem = SearchItemLabel(mTitle)

    init {
        add(mHeaderItem)
        add(mDataItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mHeaderItem
            1 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mHeaderItem -> 0
            mDataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2
}