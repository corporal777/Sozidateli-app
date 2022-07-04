package com.example.ui.event.about.redesign.items

import com.example.data.models.PartnerModel
import com.example.holders.redesign.EventPartnerItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class EventDetailPartnersBlock(
    private val title: String,
    private val list: List<PartnerModel>?,
    private val onPartnerClick: (id : Int) -> Unit
) : NestedGroup() {

    private val headerItem = EventDetailBlocksLabelItem(title)
    private val partnersItemsSection = Section()

    init {
        add(headerItem)
        if (!list.isNullOrEmpty()) {
            list.forEach {
                partnersItemsSection.add(
                    EventPartnerItem(
                        it.id?: 0,
                        it.name,
                        it.description,
                        it.logo?.uri
                    ) {id ->
                        onPartnerClick(id)
                    }
                )
            }
        }
        add(partnersItemsSection)
    }


    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> headerItem
            1 -> partnersItemsSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            headerItem -> 0
            partnersItemsSection -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 2

}
