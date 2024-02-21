package com.example.holders

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Message.MessageType
import com.example.data.models.UserChat
import com.example.databinding.ItemChatBinding
import com.example.extensions.*
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.setCircleAvatar
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import java.util.*


class UserChatItem(
    val userChat: UserChat,
    private val onClick: (UserChat) -> Unit,
    private val onBind: ((UserChatItem) -> Unit)? = null,
    private val onUnBind: ((UserChatItem) -> Unit)? = null,
    private val withDivider: Boolean
) : BindableItem<ItemChatBinding>(userChat.id.toLong()) {

    private lateinit var mViewHolder: ItemChatBinding
    private var lastMessage = userChat.lastMessage
    private var messageCount = userChat.unreadMessageCount

    override fun bind(viewBinding: ItemChatBinding, position: Int) {
        onBind?.invoke(this)
        mViewHolder = viewBinding
        viewBinding.apply {
            ivAvatar.setCircleAvatar(userChat.user.loadUserImage())
            tvName.text = userChat.user.nameLastName
            tvLastMessage.apply {
                text = when (userChat.lastMessageType) {
                    MessageType.IMAGE -> context.getString(R.string.chat_photo_message_text)
                    MessageType.SERVICE -> {
                        if (lastMessage == CHAT_SERVICE_MESSAGE_ACCEPT) {
                            if (userChat.lastMessageSender == userChat.user.id) context.getString(R.string.chat_accepted)
                            else context.getString(R.string.chat_accept_by_me)
                        } else ""
                    }
                    else -> lastMessage
                }
            }

            tvDate.apply {
                if (userChat.lastMessageDate == null) visibility = View.GONE
                else {
                    text = formatMessageDate(context, userChat.lastMessageDate)
                    visibility = View.VISIBLE
                }
            }

            root.setOnClickListener { onClick(userChat) }

            updateBadge(messageCount)
            divider.isVisible = withDivider
        }
    }


    fun updateBadge(count: Int) {
        if (this::mViewHolder.isInitialized) {
            mViewHolder.tvBadge.apply {
                isVisible = count > 0
                messageCount = count
                text = if (count <= 99) count.toString() else "99+"
            }
        }
    }

    fun updateMessage(message: String) {
        if (message != userChat.lastMessage) {
            if (this::mViewHolder.isInitialized) {
                mViewHolder.tvLastMessage.text = message
                lastMessage = message
            }
        }
    }

    private fun formatMessageDate(context: Context, date: String): String {
        val messageDate = defaultServerDateTimeFormatter.parse(date) ?: return context.getString(R.string.today)
        val messageCalendar = messageDate.time.calendar()
        val now = Calendar.getInstance()

        return when {
            messageCalendar.isSameDay(now) -> context.getString(R.string.today)
            messageCalendar.isYesterday(now) -> context.getString(R.string.yesterday)
            else -> dateFormatterShortMothNoYear.format(messageDate)
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemChatBinding>) {
        super.unbind(viewHolder)
        onUnBind?.invoke(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserChatItem) return false

        if (userChat != other.userChat) return false

        return true
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is UserChatItem) return false
        if (userChat != other.userChat) return false
        return true
    }

    override fun hashCode(): Int {
        return userChat.hashCode()
    }

    override fun getLayout() = R.layout.item_chat
}