package com.example.holders

import android.view.View
import com.example.R
import com.example.extensions.setUnderlineSpan
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_file_name.*

class ProfileDataFileItem(
        private val name: String,
        private val compactBottom: Boolean,
        private val onFileClick: () -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvFileName.text = name.setUnderlineSpan()
            divider.visibility = if (compactBottom) View.GONE else View.VISIBLE
            tvFileName.setOnClickListener { onFileClick() }
        }
    }

    override fun getLayout() = R.layout.item_file_name
}