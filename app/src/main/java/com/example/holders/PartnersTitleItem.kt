package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class PartnersTitleItem(id: Long) : Item(id) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {}

    override fun getLayout() = R.layout.item_partners_title
}