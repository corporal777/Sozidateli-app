package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_list_section_name.*

class ListSectionNameItem(
        id: Long,
        private val name: String
) : Item(id) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvName.text = name
    }

    override fun getLayout() = R.layout.item_list_section_name
}