package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_button.*
import setOnClickListener

class ProfileButtonItem(
        private val text: String,
        private val onClickListener: () -> Unit
) : Item(text.hashCode().toLong()) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvTitle.apply {
            text = this@ProfileButtonItem.text
            setOnClickListener(onClickListener)
        }
    }

    override fun getLayout() = R.layout.item_profile_button
}