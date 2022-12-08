package com.example.holders.redesign

import com.example.R
import com.example.data.models.PageModel
import com.example.databinding.ItemEventPageNewBinding
import com.xwray.groupie.databinding.BindableItem

class EventPageItemNew(
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

    override fun getLayout(): Int = R.layout.item_event_page_new
}