package com.example.holders

import com.example.R
import com.example.data.models.SearchTypeEvent
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_search_type_selected.view.*

open class SelectedSearchTypeItem(private val searchTypeEvent: SearchTypeEvent, private val itemClick: (searchTypeEvent: SearchTypeEvent) -> Unit) : Item() {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvName.text = searchTypeEvent.name
            ivClose.setOnClickListener {
                itemClick(searchTypeEvent)
            }
        }
    }

    override fun getLayout() = R.layout.item_search_type_selected
}