package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_button_edit.*
import setOnClickListener

class ProfileButtonEditItem(
        private val onClickListener: () -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.btnEdit.apply {
            setOnClickListener(onClickListener)
        }
    }

    override fun getLayout() = R.layout.item_profile_button_edit
}