package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_notes_description.*
import setOnClickListener

class ProfileDataNotesDescriptionItem(
        id: Long,
        private val onWhyClick: () -> Unit
) : Item(id) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.btnWhy.setOnClickListener(onWhyClick)
    }

    override fun getLayout() = R.layout.item_profile_data_notes_description
}