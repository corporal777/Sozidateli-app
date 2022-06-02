package com.example.holders.redesign.blocks

import com.example.data.models.MemberModel
import com.example.holders.redesign.*
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class EventDetailSpeakersBlock(
    val title: String,
    data: List<MemberModel>,
    val onItemClick: (id: Int) -> Unit
) : NestedGroup() {

    private val mDataItem = SpeakersHorizontalListItem(data, { onItemClick(it) }, {})
    private val mHeaderItem = EventDetailBlocksLabelItem(title)

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