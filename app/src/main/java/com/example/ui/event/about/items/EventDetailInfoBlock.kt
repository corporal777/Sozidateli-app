package com.example.ui.event.about.items

import com.example.data.models.OrganizationNew
import com.example.data.models.PageModel
import com.example.extensions.updateItems
import com.example.holders.redesign.*
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailInfoBlock(
    private val organization: OrganizationNew?,
    private val address: String?,
    private val pages: List<PageModel>?,
    private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
    private val onOrganizationClick: (id: String) -> Unit,
    private val mapClick: () -> Unit,
    private val pageClick: (id: Int) -> Unit
) : NestedGroup() {

    private val mOrganizationItem =
        EventDetailOrganizationItem(organization, actionClickListener, onOrganizationClick)

    private val mInformationItem = Section().apply {
        setHeader(EventDetailBlocksLabelItem("Информация", id = -1005L))
        setHideWhenEmpty(true)
    }

    init {
        if (organization != null) add(mOrganizationItem)
        mInformationItem.apply {
            updateItems(
                if (!address.isNullOrEmpty())
                    EventPageItem(101, "Как добраться") { mapClick() }
                else null,
                pages?.map { item ->
                    EventPageItem(item.id ?: 0, item.name ?: "") { pageClick(it) }
                }
            )
        }
        add(mInformationItem)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mOrganizationItem
            1 -> mInformationItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mOrganizationItem -> 0
            mInformationItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2

}