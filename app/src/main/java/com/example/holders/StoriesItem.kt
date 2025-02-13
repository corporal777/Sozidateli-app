package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemStoriesBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem


class StoriesItem(
    val id: Int,
    val value: String
) : BindableItem<ItemStoriesBinding>(id.toLong()) {

    override fun bind(viewBinding: ItemStoriesBinding, position: Int) {
        viewBinding.apply {
            tvValue.text = value
        }
    }

    override fun initializeViewBinding(view: View) = ItemStoriesBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_stories

}