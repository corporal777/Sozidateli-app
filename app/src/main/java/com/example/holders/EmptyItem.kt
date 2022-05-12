package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat_list_empty.*

class EmptyItem(private val text: String): Item(-1L) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.tvNoChats.text = text
    }

    override fun getLayout() = R.layout.item_empty

}