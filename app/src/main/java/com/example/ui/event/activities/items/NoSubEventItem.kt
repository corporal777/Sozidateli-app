package com.example.ui.event.activities.items

import com.example.app.R
import com.example.app.databinding.ItemNoActivityBinding
import com.xwray.groupie.databinding.BindableItem

class NoSubEventItem(
    val title : String
) : BindableItem<ItemNoActivityBinding>() {


    override fun bind(viewBinding: ItemNoActivityBinding, position: Int) {
        viewBinding.title.text = title
    }

    override fun getLayout(): Int = R.layout.item_no_activity
}