package com.example.holders.redesign

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_screen_header_label.*

class ScreenHeaderItem (
        private val label: String
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvHeaderLabel.text = label
    }

    override fun getLayout() = R.layout.item_screen_header_label
}