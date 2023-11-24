package com.example.holders

import com.example.R
import com.example.databinding.ItemChatListEmptyBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_list_empty.*

class ChatListEmptyItem(
    private val onChatCreateClick: () -> Unit
) : BindableItem<ItemChatListEmptyBinding>(-1L) {


    override fun bind(viewBinding: ItemChatListEmptyBinding, position: Int) {
        viewBinding.btnCreateChat.setOnClickListener { onChatCreateClick() }
    }


    override fun getLayout() = R.layout.item_chat_list_empty
}