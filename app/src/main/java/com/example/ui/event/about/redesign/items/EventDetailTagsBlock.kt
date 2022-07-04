package com.example.ui.event.about.redesign.items

import com.example.data.models.Tag
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailTagsBlock(
    val title: String,
    private val listTags : List<Tag>,
    private val onTagClick: (tag : Tag) -> Unit
) : NestedGroup() {

    private val mDataItem = Section()
    private val mLabelItem = EventDetailBlocksLabelItem(title)

    init {
        add(mLabelItem)
        mDataItem.apply {
            update(listOf(
                TagsItem(listTags) {
                    onTagClick(it)
                }
            ))
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