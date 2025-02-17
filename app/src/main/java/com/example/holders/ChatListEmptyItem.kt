package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemChatListEmptyBinding
import com.xwray.groupie.viewbinding.BindableItem

class ChatListEmptyItem(
    private val onChatCreateClick: () -> Unit
) : BindableItem<ItemChatListEmptyBinding>(-1L) {


    override fun bind(viewBinding: ItemChatListEmptyBinding, position: Int) {
        viewBinding.btnCreateChat.setOnClickListener { onChatCreateClick() }
    }

    override fun initializeViewBinding(view: View) = ItemChatListEmptyBinding.bind(view)
    override fun getLayout() = R.layout.item_chat_list_empty
}