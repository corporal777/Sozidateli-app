package com.example.ui.event.my.schedule.items

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.R
import com.example.databinding.ItemNoScheduleEventBinding
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem

class NoScheduleEventItem(
    val title: String,
    val description: String = "",
    val padding: Int = 0,
) : BindableItem<ItemNoScheduleEventBinding>() {

    override fun bind(viewBinding: ItemNoScheduleEventBinding, position: Int) {
        viewBinding.apply {
            if (padding > 0) {
                noDataPlaceholder.updatePadding(top = padding.dp)
            }
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