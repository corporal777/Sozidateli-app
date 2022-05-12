package com.example.holders

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.extensions.dp
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_list_section_name.*

class ListSectionNameItem(
        id: Long,
        private val name: String? = null
) : Item(id) {

    var withTopMargin = false

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvName.apply {
            text = name
            isVisible = !name.isNullOrEmpty()
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = if (withTopMargin) 20.dp else 0
            }
        }
    }

    override fun getLayout() = R.layout.item_list_section_name
}