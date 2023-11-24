package com.example.holders

import com.example.R
import com.example.databinding.ItemProfileDataNotesBinding
import com.xwray.groupie.databinding.BindableItem

class ProfileDataNotesItem(
    private val notes: String
) : BindableItem<ItemProfileDataNotesBinding>() {

    override fun bind(viewBinding: ItemProfileDataNotesBinding, position: Int) {
        viewBinding.apply {
            tvNotes.text = notes
        }
    }

    override fun getLayout() = R.layout.item_profile_data_notes
}