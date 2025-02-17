package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemProfileDataNotesBinding
import com.xwray.groupie.viewbinding.BindableItem

class ProfileDataNotesItem(
    private val notes: String
) : BindableItem<ItemProfileDataNotesBinding>() {

    override fun bind(viewBinding: ItemProfileDataNotesBinding, position: Int) {
        viewBinding.apply {
            tvNotes.text = notes
        }
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataNotesBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_notes
}