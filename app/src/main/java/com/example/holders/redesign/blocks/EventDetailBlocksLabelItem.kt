package com.example.holders.redesign.blocks

import com.example.R
import com.example.databinding.ItemEventDetailLabelBlockBinding
import com.xwray.groupie.databinding.BindableItem

class EventDetailBlocksLabelItem (
    val title: String
): BindableItem<ItemEventDetailLabelBlockBinding>() {

    override fun bind(viewBinding: ItemEventDetailLabelBlockBinding, position: Int) {
        viewBinding.tvTitle.text = title
    }

    override fun getLayout(): Int = R.layout.item_event_detail_label_block
}