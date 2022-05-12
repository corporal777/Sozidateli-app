package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat_unread_label.*

class ChatUnreadLabelItem(
        private val count: Int
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvNewMessages.apply {
            text = resources.getQuantityString(R.plurals.chat_new_messages_count, count, count)
        }
    }

    override fun getLayout() = R.layout.item_chat_unread_label
}