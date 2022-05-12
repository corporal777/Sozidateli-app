package com.example.holders.redesign

import com.example.R
import com.example.data.models.EventActivityModel
import com.example.databinding.ItemEventDetailActivitiesBlockBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem

class SubEventsListItem(
    val list: List<EventActivityModel>?
) : BindableItem<ItemEventDetailActivitiesBlockBinding>() {

    private val subEventsSection = Section()

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            add(subEventsSection)
        }
    }

    init {
        val listActivitiesMap =
            list?.groupBy { it.holdingDate?.from?.split(" ")?.get(0) }
        listActivitiesMap?.map { map ->
            val date = EventActivityDateItem(map.key)
            subEventsSection.add(date)
            map.value.forEach { data ->
                subEventsSection.add(EventActivityItem(data, null, null))
            }
        }
    }

    override fun bind(viewBinding: ItemEventDetailActivitiesBlockBinding, p1: Int) {
        viewBinding.apply {
            activitiesList.adapter = groupAdapter
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_activities_block
}