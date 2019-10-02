package com.example.holders

import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_list_section_name.*

class ListSectionNameItem(
        id: Long,
        private val name: String? = null
) : Item(id) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.tvName.apply {
            if (name.isNullOrEmpty()) visibility = GONE
            else {
                text = name
                visibility = VISIBLE
            }
        }
    }

    override fun getLayout() = R.layout.item_list_section_name
}