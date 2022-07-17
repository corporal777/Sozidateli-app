package com.example.ui.event.about.redesign.items

import com.example.data.models.EventNew
import com.example.data.models.PageModel
import com.example.holders.redesign.EventPageItemNew
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailInformationBlock(
    val label: String,
    val data: EventNew?,
    val pages: List<PageModel>?,
    val mapClick: () -> Unit,
    val pageClick: (id: Int) -> Unit
) : NestedGroup() {

    private val mLabelItem = EventDetailBlocksLabelItem(label)
    private val mContentSection = Section()

    init {
        add(mLabelItem)
        mContentSection.apply {
            if (!data?.address?.fullValue.isNullOrEmpty()) {
                add(
                    EventPageItemNew(
                        1,
                        "Как добраться"
                    ) { mapClick() })
            }
            if (!pages.isNullOrEmpty()) {
                addAll(pages.map { item ->
                    EventPageItemNew(
                        item.id ?: 0,
                        item.name ?: ""
                    ) { pageClick(it) }
                })
            }
        }
        add(mContentSection)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mLabelItem
            1 -> mContentSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mLabelItem -> 0
            mContentSection -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2
}