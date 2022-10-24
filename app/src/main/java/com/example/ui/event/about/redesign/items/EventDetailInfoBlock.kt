package com.example.ui.event.about.redesign.items

import com.example.data.models.MemberModel
import com.example.data.models.OrganizationNew
import com.example.data.models.PageModel
import com.example.holders.redesign.*
import com.example.ui.views.UserSubscribeButton
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailInfoBlock(
    val organization: OrganizationNew?,
    private val actionClickListener: (UserSubscribeButton.Action) -> Unit,
    private val onOrganizationClick: (id: String) -> Unit,

    val infoTitle: String,
    val address: String?,
    val pages: List<PageModel>?,
    val mapClick: () -> Unit,
    val pageClick: (id: Int) -> Unit
) : NestedGroup() {

    private val mOrganizationItem =
        EventDetailOrganizationItem(organization, actionClickListener, onOrganizationClick)

    private val mInformationItem = Section().apply {
        setHeader(EventDetailBlocksLabelItem(infoTitle))
        setHideWhenEmpty(true)
    }

    init {
        if (organization != null) {
            add(mOrganizationItem)
        }
        mInformationItem.apply {
            if (!address.isNullOrEmpty()){
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