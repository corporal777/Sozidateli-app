package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_list_empty.*

class ChatListEmptyItem(
        private val onChatCreateClick: () -> Unit
) : Item(-1L) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.btnCreateChat.setOnClickListener { onChatCreateClick() }
    }

    override fun getLayout() = R.layout.item_chat_list_empty
}