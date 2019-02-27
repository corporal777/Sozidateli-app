package com.example.holders

import android.view.View
import com.example.R
import com.example.data.models.UserChat
import com.example.util.CropCircleTransformation
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat.*

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
            Picasso.get().load(userChat.user.user_avatar.let { if (it.isNullOrBlank()) null else it })
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(CropCircleTransformation())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(ivAvatar)

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
}