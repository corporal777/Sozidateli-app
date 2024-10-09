package com.example.holders

import com.example.app.R
import com.example.app.databinding.ItemStoriesBinding
import com.xwray.groupie.databinding.BindableItem

class StoriesItem(
    val id: Int,
    val value: String
) : BindableItem<ItemStoriesBinding>(id.toLong()) {


    override fun bind(viewBinding: ItemStoriesBinding, position: Int) {
        viewBinding.apply {
            tvValue.text = value
        }
    }

    override fun getLayout(): Int = R.layout.item_stories
}