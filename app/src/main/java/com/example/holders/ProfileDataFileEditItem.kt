package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile_data_edit_file.*
import onTextChanged

class ProfileDataFileEditItem(
        name: String?
) : Item() {

    var mName = name
        private set

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            etName.apply {
                setText(mName)
                onTextChanged { mName = it?.toString() }
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_data_edit_file
}