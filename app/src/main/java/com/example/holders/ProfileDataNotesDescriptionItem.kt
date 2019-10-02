package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ProfileDataNotesDescriptionItem(id: Long) : Item(id) {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_profile_data_notes_description
}