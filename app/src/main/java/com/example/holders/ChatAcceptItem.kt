package com.example.holders

import com.example.app.R
import com.example.app.databinding.ItemChatAcceptBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ChatAcceptItem(private val onBindListener: () -> Unit) : BindableItem<ItemChatAcceptBinding>() {

    override fun bind(viewBinding: ItemChatAcceptBinding, position: Int) {
        onBindListener()
    }


    override fun getLayout() = R.layout.item_chat_accept
}