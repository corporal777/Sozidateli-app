package com.example.ui.event.my.schedule.items

import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventScheduleCalendarDay
import com.example.databinding.EmptyLayoutItemBinding
import com.xwray.groupie.databinding.BindableItem

class EmptyItem() : BindableItem<EmptyLayoutItemBinding>() {

    override fun bind(viewBinding: EmptyLayoutItemBinding, position: Int) {
        viewBinding.root.isVisible = false
    }




    override fun getLayout(): Int = R.layout.empty_layout_item
}