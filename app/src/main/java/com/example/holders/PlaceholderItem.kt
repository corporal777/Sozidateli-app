package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class PlaceholderItem(
        private val type: Type
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout() = when (type) {
        Type.EVENT -> R.layout.item_event_placeholder
        Type.SEARCH_EVENT -> R.layout.item_search_event_placeholder
        Type.ORGANIZATION -> R.layout.item_organization_placeholder
        Type.USER -> R.layout.item_user_placeholder
        Type.CHAT_LIST -> R.layout.item_user_chat_placeholder
        Type.NOTIFICATION -> R.layout.item_notification_placeholder
    }

    enum class Type {
        EVENT,
        ORGANIZATION,
        SEARCH_EVENT,
        USER,
        CHAT_LIST,
        NOTIFICATION
    }
}