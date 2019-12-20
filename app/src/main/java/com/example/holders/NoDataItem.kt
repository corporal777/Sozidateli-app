package com.example.holders

import androidx.core.view.isVisible
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_no_data.*

class NoDataItem(
        private val title: String,
        private val description: String? = null
) : Item(-1000) {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvTitle.text = title
            tvDescription.apply {
                text = description
                isVisible = !description.isNullOrEmpty()
            }
        }
    }

    override fun getLayout() = R.layout.item_no_data
}