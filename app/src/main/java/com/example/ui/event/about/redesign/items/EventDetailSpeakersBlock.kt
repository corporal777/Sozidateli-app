package com.example.ui.event.about.redesign.items

import android.util.Log
import com.example.data.models.MemberModel
import com.example.holders.redesign.*
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailSpeakersBlock(
    val title: String,
    data: List<MemberModel>,
    val onItemClick: (id: Int) -> Unit,
    val onShowAllClick : () -> Unit,
) : NestedGroup() {

    private val mDataItem = Section()
    private val mLabelItem = EventDetailBlocksLabelItem(title)

    init {
        add(mLabelItem)
        mDataItem.apply {
            update(listOf(SpeakersHorizontalListItem(data,
                { onItemClick(it) },
                { onShowAllClick() })))
        }

        add(mDataItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mLabelItem
            1 -> mDataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mLabelItem -> 0
            mDataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2

}