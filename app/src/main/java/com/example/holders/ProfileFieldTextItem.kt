package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_field_text.*
import removeUrlUnderline

class ProfileFieldTextItem(
        private val title: String,
        private val data: String
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTitle.apply {
                isVisible = title.isNotEmpty()
                text = title
            }
            tvData.apply {
                text = data
                removeUrlUnderline()
            }
        }
    }

    override fun getLayout() = R.layout.item_profile_field_text
}