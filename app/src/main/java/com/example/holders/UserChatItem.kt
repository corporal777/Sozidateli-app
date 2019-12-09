package com.example.holders

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.UserChat
import com.example.extensions.*
import com.example.ui.views.BadgeDrawable
import com.example.ui.views.addBadge
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_chat.*
import ru.houseofapps.chat.models.Message
import setCircleImage
import java.util.*
import kotlin.math.roundToInt


class UserChatItem(
        val userChat: UserChat,
        private val onClick: (UserChat) -> Unit,
        private val onBind: ((UserChatItem) -> Unit)? = null,
        private val onUnBind: ((UserChatItem) -> Unit)? = null,
        private val badgeDrawable: BadgeDrawable? = null,
        private val withDivider: Boolean
) : Item(userChat.id.toLong()) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        onBind?.invoke(this)
        viewHolder.apply {
            ivAvatar.apply {
                setCircleImage(userChat.user.user_avatar, R.drawable.avatar_placeholder)

                doOnNextLayout { view ->
                    badgeDrawable?.apply {
                        (view.parent as ViewGroup).overlay.clear()
                        number = userChat.unreadMessageCount
                        view.addBadge(this) { badgeWidth, badgeHeight, anchorRect ->
                            val badgeCenterX = (anchorRect.right - width / 2.5f).roundToInt()
                            val badgeCenterY = anchorRect.top + badgeHeight / 2

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

    private fun formatMessageDate(context: Context, date: String): String {
        val messageDate = defaultServerDateTimeFormatter.parse(date)
        val messageCalendar = messageDate.time.calendar()
        val now = Calendar.getInstance()

        return when {
            messageCalendar.isSameDay(now) -> context.getString(R.string.today)
            messageCalendar.isYesterday(now) -> context.getString(R.string.yesterday)
            else -> dateFormatterFullMothFullYear.format(messageDate)
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