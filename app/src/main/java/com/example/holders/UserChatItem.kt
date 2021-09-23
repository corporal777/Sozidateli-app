package com.example.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Message.MessageType
import com.example.data.models.UserChat
import com.example.extensions.*
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat.*
import setCircleImage
import java.util.*


class UserChatItem(
        val userChat: UserChat,
        private val onClick: (UserChat) -> Unit,
        private val onBind: ((UserChatItem) -> Unit)? = null,
        private val onUnBind: ((UserChatItem) -> Unit)? = null,
        private val withDivider: Boolean
) : Item(userChat.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        onBind?.invoke(this)
        viewHolder.apply {
            ivAvatar.apply {
                setCircleImage(userChat.user.image.uri/*.user_avatar*/, R.drawable.avatar_placeholder)
            }

            updateBadge(viewHolder.tvBadge)

            tvName.text = userChat.user.fullName

            tvLastMessage.apply {
                text = when (userChat.lastMessageType) {
                    MessageType.IMAGE -> context.getString(R.string.chat_photo_message_text)
                    MessageType.SERVICE -> {
                        if (userChat.lastMessage == CHAT_SERVICE_MESSAGE_ACCEPT) {
                            if (userChat.lastMessageSender == userChat.user.id) context.getString(R.string.chat_accepted)
                            else context.getString(R.string.chat_accept_by_me)
                        } else ""
                    }
                    else -> userChat.lastMessage
                }
            }

            itemView.setOnClickListener { onClick(userChat) }

            tvDate.apply {
                if (userChat.lastMessageDate == null) visibility = View.GONE
                else {
                    text = formatMessageDate(context, userChat.lastMessageDate)
                    visibility = View.VISIBLE
                }
            }

            divider.isVisible = withDivider
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        val payload = payloads.firstOrNull()
        if (payload == null) super.bind(viewHolder, position, payloads)
        else if (payload is Int) {
            updateBadge(viewHolder.tvBadge)
        }
    }

    private fun updateBadge(tvBadge: TextView) {
        tvBadge.apply {
            val messageCount = userChat.unreadMessageCount
            isVisible = messageCount > 0
            val count = if (messageCount <= 99) messageCount.toString() else "99+"
            text = count
        }
    }

    private fun formatMessageDate(context: Context, date: String): String {
        val messageDate = defaultServerDateTimeFormatter.parse(date)
        val messageCalendar = messageDate.time.calendar()
        val now = Calendar.getInstance()

        return when {
            messageCalendar.isSameDay(now) -> defaultTimeFormatter.format(messageDate)//context.getString(R.string.today)
            //messageCalendar.isYesterday(now) -> context.getString(R.string.yesterday)
            else -> dateFormatterShortMothNoYear.format(messageDate)
        }
    }

    override fun unbind(holder: GroupieViewHolder) {
        super.unbind(holder)
        onUnBind?.invoke(this)
    }

    override fun getLayout() = R.layout.item_chat

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserChatItem) return false

        if (userChat != other.userChat) return false

        return true
    }

    override fun hashCode(): Int {
        return userChat.hashCode()
    }
}