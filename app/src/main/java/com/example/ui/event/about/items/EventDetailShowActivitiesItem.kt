package com.example.ui.event.about.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemEventDetailShowActivitiesBlockBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemEventDetailShowActivitiesBlockBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_show_activities_block
}