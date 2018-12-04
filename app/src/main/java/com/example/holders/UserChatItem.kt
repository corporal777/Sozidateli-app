package com.example.holders

import com.example.R
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat_list.view.*


open class UserChatItem(userChatItem: UserChatItem?=null) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            Picasso.get().load(R.drawable.ic_launcher)
                    .transform(CropCircleTransformation())
                    .into(ivAvatar)
            tvName.text="Test name for example"
            tvLastMessage.text = "Test text for example Test text for example Test text for example Test text for example Test text for example Test text for example"
        }
    }

    override fun getLayout() = R.layout.item_chat_list
}