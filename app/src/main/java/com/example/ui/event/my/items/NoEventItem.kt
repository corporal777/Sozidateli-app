package com.example.ui.event.my.items

import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemNoEventBinding
import com.xwray.groupie.databinding.BindableItem

class NoEventItem(
    val title: String,
    val description: String = ""
) : BindableItem<ItemNoEventBinding>() {

    override fun bind(viewBinding: ItemNoEventBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
            if (description.isNullOrEmpty()) {
                tvDescription.isVisible = false
            } else {
                tvDescription.text = description
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_no_event
}