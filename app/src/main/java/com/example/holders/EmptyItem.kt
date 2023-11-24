package com.example.holders

import com.example.R
import com.example.databinding.ItemEmptyBinding
import com.xwray.groupie.databinding.BindableItem

class EmptyItem(private val text: String) : BindableItem<ItemEmptyBinding>(-1L) {

    override fun bind(viewBinding: ItemEmptyBinding, position: Int) {
        viewBinding.tvNoChats.text = text
    }


    override fun getLayout() = R.layout.item_empty

}