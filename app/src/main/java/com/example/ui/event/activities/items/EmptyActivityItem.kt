package com.example.ui.event.activities.items

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_no_data.*

class EmptyActivityItem() : Item(-1000) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTitle.text = viewHolder.root.context.getString(R.string.schedule_my_empty_day_placeholder_title)
            tvDescription.apply {
                text = viewHolder.root.context.getString(R.string.schedule_my_empty_day_placeholder_description)
            }
        }
    }

    override fun getLayout() = R.layout.item_no_data
}