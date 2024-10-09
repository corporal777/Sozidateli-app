package com.example.holders

import com.example.app.R
import com.example.app.databinding.ItemChatListEmptyBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ChatListEmptyItem(
    private val onChatCreateClick: () -> Unit
) : BindableItem<ItemChatListEmptyBinding>(-1L) {


    override fun bind(viewBinding: ItemChatListEmptyBinding, position: Int) {
        viewBinding.btnCreateChat.setOnClickListener { onChatCreateClick() }
    }


    override fun getLayout() = R.layout.item_chat_list_empty
}