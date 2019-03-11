package com.example.holders

import android.view.View
import com.example.R
import com.example.data.models.UserChat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat.*
import setCircleImageWithPlaceholder

class UserChatItem(
        val userChat: UserChat,
        private val onClick: (UserChat) -> Unit,
        private val onBind: (UserChatItem) -> Unit,
        private val onUnBind: (UserChatItem) -> Unit
) : Item(userChat.id.toLong()) {

    private var viewHolder: ViewHolder? = null

    var unreadMessageCount = 0
        set(value) {
            field = value
            updateBadge()
        }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        onBind(this)
        viewHolder.apply {
            ivAvatar.setCircleImageWithPlaceholder(userChat.user.user_avatar, R.drawable.avatar_placeholder)

            tvName.text = userChat.user.fullName
            tvLastMessage.text = userChat.lastMessage ?: "-"
            itemView.setOnClickListener { onClick(userChat) }

            updateBadge()
        }
    }

    private fun updateBadge() {
        viewHolder?.tvBadge?.apply {
            text = unreadMessageCount.toString()
            visibility = if (unreadMessageCount == 0) View.GONE else View.VISIBLE
        }
    }


    override fun unbind(holder: ViewHolder) {
        super.unbind(holder)
        onUnBind(this)
    }

    override fun getLayout() = R.layout.item_chat

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserChatItem

        if (userChat != other.userChat) return false
        if (unreadMessageCount != other.unreadMessageCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userChat.hashCode()
        result = 31 * result + unreadMessageCount
        return result
    }

}