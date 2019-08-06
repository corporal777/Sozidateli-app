package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_notes.*

class ProfileDataNotesItem(
        private val notes: String
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            tvNotes.text = notes
        }
    }

    override fun getLayout() = R.layout.item_profile_data_notes
}