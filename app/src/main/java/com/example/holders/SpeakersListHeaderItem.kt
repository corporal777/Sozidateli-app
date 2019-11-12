package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SpeakersListHeaderItem : Item(-100L) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_speakers_list_header
}