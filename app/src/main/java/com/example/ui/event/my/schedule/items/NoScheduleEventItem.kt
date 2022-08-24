package com.example.ui.event.my.schedule.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemNoEventBinding
import com.example.databinding.ItemNoScheduleEventBinding
import com.xwray.groupie.databinding.BindableItem

class NoScheduleEventItem (
    val title: String,
    val description: String = ""
) : BindableItem<ItemNoScheduleEventBinding>() {

    override fun bind(viewBinding: ItemNoScheduleEventBinding, position: Int) {
        viewBinding.apply {
            tvTitle.apply {
                text = title
            }
            if (description.isNullOrEmpty()) {
                tvDescription.isVisible = false
            } else {
                tvDescription.text = description
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_no_schedule_event
}