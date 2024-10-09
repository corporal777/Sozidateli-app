package com.example.ui.event.list.recommendations.items

import com.example.data.models.EventNew
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.PlaceholderItem
import com.example.holders.redesign.EventListItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

class RecommendationItemsGroup(
    val events: List<EventNew?>,
    val isTemp : Boolean,
    val isNeedUpdateApp: Boolean?,
    val eventClickListener: EventListItem.OnEventClickListener,
) : NestedGroup() {

    private val updateAppSection = Section()
    private val eventsSection = Section()


    init {
        updateItems(events, isNeedUpdateApp)
        updateAppSection.registerGroupDataObserver(this)
        eventsSection.registerGroupDataObserver(this)
    }


    fun updateItems(events: List<EventNew?>, isNeedUpdateApp: Boolean?) {
        updateAppSection.apply {
            if (isNeedUpdateApp == true) updateItem(UpdateAppItem())
        }
        eventsSection.apply {
            update(events.map {
                if (it == null) PlaceholderItem(PlaceholderItem.Type.EVENT)
                else EventListItem(it, isTemp, eventClickListener)
            })
        }
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> updateAppSection
            1 -> eventsSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            updateAppSection -> 0
            eventsSection -> 1
            else -> -1
        }
    }

    fun updateButtonState(event: EventNew?) {
        val id = event?.id?.toLong()
        eventsSection.findItemBy<EventListItem> { x -> x.id == id }?.notifyChanged(event)
    }


    override fun getGroupCount() = 2
}