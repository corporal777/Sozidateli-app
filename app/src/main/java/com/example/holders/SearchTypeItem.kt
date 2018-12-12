package com.example.holders

import android.widget.ImageView
import com.example.R
import com.example.data.models.SearchTypeEvent
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_search_type.view.*

open class SearchTypeItem(private val searchTypeEvent: SearchTypeEvent, private val itemClick: (searchTypeEvent: SearchTypeEvent, isSelect: Boolean) -> Unit) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            tvName.text = searchTypeEvent.name

            selected(ivSelected)
            ivSelected.setOnClickListener {
                itemClick(searchTypeEvent,!searchTypeEvent.selected)
                searchTypeEvent.selected = !searchTypeEvent.selected
                selected(ivSelected)
            }
        }
    }

    private fun selected(imageView: ImageView) {
        if (searchTypeEvent.selected) {
            imageView.setImageResource(R.drawable.ic_close_black_24dp)
        } else {
            imageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
        }
    }

    override fun getLayout() = R.layout.item_search_type
}