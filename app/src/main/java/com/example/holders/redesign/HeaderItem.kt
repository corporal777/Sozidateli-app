package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemTitleBinding
import com.xwray.groupie.databinding.BindableItem

class HeaderItem(
    val title: String
): BindableItem<ItemTitleBinding>() {

    override fun bind(viewBinding: ItemTitleBinding, position: Int) {
        viewBinding.tvTitle.text = title
    }

    override fun getLayout(): Int = R.layout.item_title
}