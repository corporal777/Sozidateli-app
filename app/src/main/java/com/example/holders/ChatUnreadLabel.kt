package com.example.holders

import android.view.View
import android.widget.LinearLayout
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_action_button.view.*

class ChatUnreadLabel() : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout() = R.layout.item_chat_unread_label
}