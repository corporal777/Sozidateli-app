package com.example.holders

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemChatAcceptBinding
import com.xwray.groupie.viewbinding.BindableItem

class ChatAcceptItem(private val onBindListener: () -> Unit) : BindableItem<ItemChatAcceptBinding>() {

    override fun bind(viewBinding: ItemChatAcceptBinding, position: Int) {
        onBindListener()
    }


    override fun initializeViewBinding(view: View) = ItemChatAcceptBinding.bind(view)
    override fun getLayout() = R.layout.item_chat_accept
}