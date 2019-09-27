package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_personal.*

class ProfileDataEditSaveItem(
        private val saveClickListener: () -> Unit,
        private val cancelClickListener: () -> Unit
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            btnSave.setOnClickListener { saveClickListener() }
            btnRevoke.setOnClickListener { cancelClickListener() }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_save
}