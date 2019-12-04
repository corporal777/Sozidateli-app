package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_file_name.*

class ProfileDataFileItem(
        private val name: String,
        private val onFileClick: () -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvFileName.text = name
            tvFileName.setOnClickListener { onFileClick() }
        }
    }

    override fun getLayout() = R.layout.item_file_name
}