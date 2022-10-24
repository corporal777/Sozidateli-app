package com.example.ui.event.about.redesign.items

import com.example.R
import com.example.databinding.ItemEventDetailShowActivitiesBlockBinding
import com.xwray.groupie.databinding.BindableItem

class EventDetailShowActivitiesItem(
    val showActivitiesClick: () -> Unit
) : BindableItem<ItemEventDetailShowActivitiesBlockBinding>() {


    override fun bind(viewBinding: ItemEventDetailShowActivitiesBlockBinding, p1: Int) {
        viewBinding.apply {
            btnShowActivities.setOnClickListener {
                showActivitiesClick()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_show_activities_block
}