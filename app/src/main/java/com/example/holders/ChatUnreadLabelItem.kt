package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ChatUnreadLabelItem() : Item() {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_chat_unread_label
}