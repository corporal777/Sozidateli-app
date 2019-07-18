package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ChatEmptyItem : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {}

    override fun getLayout() = R.layout.item_chat_empty
}