package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ChatAcceptItem(private val onBindListener: () -> Unit) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        onBindListener()
    }

    override fun getLayout() = R.layout.item_chat_accept
}