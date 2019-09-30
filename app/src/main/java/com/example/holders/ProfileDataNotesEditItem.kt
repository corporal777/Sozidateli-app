package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_notes.*
import onTextChanged

class ProfileDataNotesEditItem(
        id: Long,
        notes: String?
) : Item(id) {

    var mNotes = notes
        private set

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            etNotes.apply {
                setText(mNotes)
                onTextChanged { mNotes = it?.toString() }
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_notes
}