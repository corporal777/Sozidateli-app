package com.example.holders

import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import com.example.R
import com.example.data.models.UserChat
import com.example.extensions.*
import com.example.ui.views.BadgeDrawable
import com.example.ui.views.addBadge
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat.*
import ru.houseofapps.chat.models.Message
import setCircleImage
import java.util.*


class UserChatItem(
        val userChat: UserChat,
        private val onClick: (UserChat) -> Unit,
        private val onBind: ((UserChatItem) -> Unit)? = null,
        private val onUnBind: ((UserChatItem) -> Unit)? = null,
        private val badgeDrawable: BadgeDrawable? = null
) : Item(userChat.id.toLong()) {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        onBind?.invoke(this)
        viewHolder.apply {
            ivAvatar.setCircleImage(userChat.user.user_avatar, R.drawable.avatar_placeholder)

            tvName.text = userChat.user.fullName

            tvLastMessage.apply {
                text = when (userChat.lastMessageType) {
                    Message.Type.IMAGE -> context.getString(R.string.chat_photo_message_text)
                    Message.Type.SERVICE -> {
                        if (userChat.lastMessage == CHAT_SERVICE_MESSAGE_ACCEPT) {
                            if (userChat.lastMessageSender == userChat.user.user_id) context.getString(R.string.chat_accepted)
                            else context.getString(R.string.chat_accept_by_me)
                        } else ""
                    }
                    else -> userChat.lastMessage
                }

                doOnNextLayout { view ->
                    badgeDrawable?.apply {
                        (view.parent as ViewGroup).overlay.clear()
                        number = userChat.unreadMessageCount
                        view.addBadge(this) { badgeWidth, badgeHeight, anchorRect ->
                            val badgeCenterX = anchorRect.right + 8.dp
                            val badgeCenterY = anchorRect.top + height / 2

                            anchorRect.set(
                                    badgeCenterX,
                                    badgeCenterY - badgeHeight / 2,
                                    badgeCenterX + badgeWidth,
                                    badgeCenterY + badgeHeight / 2
                            )
                        }
                    }
                }
            }

            itemView.setOnClickListener { onClick(userChat) }

            tvDate.apply {
                if (userChat.lastMessageDate == null) visibility = View.GONE
                else {
                    text = formatMessageDate(userChat.lastMessageDate)
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun formatMessageDate(date: String): String {
        val messageDate = defaultServerDateTimeFormatter.parse(date)
        val messageCalendar = messageDate.time.calendar()
        val now = Calendar.getInstance()

        return when {
            now.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR) -> {
                if (now.get(Calendar.DAY_OF_YEAR) == messageCalendar.get(Calendar.DAY_OF_YEAR)) defaultTimeFormatter.format(messageDate)
                else dateFormatterFullMothNoYear.format(messageDate)
            }
            else -> dateFormatterShortMoth.format(messageDate)
        }
    }

    override fun unbind(holder:GroupieViewHolder) {
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