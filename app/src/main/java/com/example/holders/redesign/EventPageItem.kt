package com.example.holders.redesign

import com.example.R
import com.example.databinding.ItemEventPageNewBinding
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventPageItem(
    val id : Int,
    val name : String,
    val onPageClick: (id : Int) -> Unit
) : BindableItem<ItemEventPageNewBinding>(id.toLong()) {


    override fun bind(viewBinding: ItemEventPageNewBinding, position: Int) {
        viewBinding.apply {
            tvPage.text = name
            tvPage.setOnClickListener {
                onPageClick(id)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventPageItem) return false
        if (name != other.name) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_event_page_new
}