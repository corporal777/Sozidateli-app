package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_save.*

class ProfileDataEditSaveItem(
        id: Long,
        private val saveClickListener: () -> Unit,
        private val cancelClickListener: () -> Unit
) : Item(id) {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            btnSave.setOnClickListener { saveClickListener() }
            btnRevoke.setOnClickListener { cancelClickListener() }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_save
}