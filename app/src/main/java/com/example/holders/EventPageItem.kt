package com.example.holders

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.R
import com.example.extensions.dp
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_page.*


class EventPageItem(
        private val id: Int,
        private val title: String,
        private val onClickListener: () -> Unit
) : Item(id.toLong()) {

    var hasBottomPadding = false

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tvLabel.text = title
            itemView.setOnClickListener { onClickListener() }

            if (hasBottomPadding) {
                divider.isVisible = false
                itemView.updatePadding(bottom = 20.dp)
            } else {
                divider.isVisible = true
                itemView.updatePadding(bottom = 0.dp)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventPageItem) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (hasBottomPadding != other.hasBottomPadding) return false

        return true
    }

    override fun getLayout() = R.layout.item_event_page
}
