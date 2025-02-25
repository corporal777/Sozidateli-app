package com.example.adapters.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.CustomLoadStateAdapter
import com.example.app.R
import com.example.app.databinding.ItemChatBinding
import com.example.data.models.Message.MessageType
import com.example.data.models.UserChatModel
import com.example.extensions.calendar
import com.example.extensions.dateFormatterShortMothNoYear
import com.example.extensions.defaultServerDateTimeFormatter
import com.example.extensions.isSameDay
import com.example.extensions.isYesterday
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.setCircleAvatar
import dev.androidbroadcast.vbpd.viewBinding
import java.util.Calendar

class UserChatsAdapter(val onUserClick: (user: UserChatModel) -> Unit) :
    PagingDataAdapter<UserChatModel, UserChatsAdapter.UserChatsViewHolder>(AsyncDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserChatsViewHolder(layoutInflater.inflate(R.layout.item_chat, parent, false))
    }

    override fun onBindViewHolder(holder: UserChatsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class UserChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewBinding by viewBinding(ItemChatBinding::bind)

        fun bind(model: UserChatModel) {
            viewBinding.apply {
                ivAvatar.setCircleAvatar(model.user.image, crossFad = 300)
                tvName.text = model.user.name
                tvLastMessage.text = root.getMessageText(model)
                tvDate.apply {
                    if (model.lastMessageDate == null) visibility = View.GONE
                    else {
                        text = formatMessageDate(context, model.lastMessageDate)
                        visibility = View.VISIBLE
                    }
                }
                root.setOnClickListener { onUserClick(model) }

                //divider.isVisible = withDivider
                updateBadge(model.unreadMessageCount)
            }
        }

        private fun View.getMessageText(model: UserChatModel): String? {
            return when (model.lastMessageType) {
                MessageType.IMAGE -> context.getString(R.string.chat_photo_message_text)
                MessageType.SERVICE -> {
                    if (model.lastMessage == CHAT_SERVICE_MESSAGE_ACCEPT) {
                        if (model.lastMessageSender == model.user?.id)
                            context.getString(R.string.chat_accepted)
                        else context.getString(R.string.chat_accept_by_me)
                    } else ""
                }
                else -> model.lastMessage
            }
        }

        private fun updateBadge(count: Int) {
            viewBinding.tvBadge.apply {
                isVisible = count > 0
                text = if (count <= 99) count.toString() else "99+"
            }
        }

        private fun formatMessageDate(context: Context, date: String?): String {
            val messageDate = defaultServerDateTimeFormatter.parse(date)
                ?: return context.getString(R.string.today)
            val messageCalendar = messageDate.time.calendar()
            val now = Calendar.getInstance()

            return when {
                messageCalendar.isSameDay(now) -> context.getString(R.string.today)
                messageCalendar.isYesterday(now) -> context.getString(R.string.yesterday)
                else -> dateFormatterShortMothNoYear.format(messageDate)
            }
        }
    }

    private object AsyncDiffCallback : DiffUtil.ItemCallback<UserChatModel>() {
        override fun areItemsTheSame(oldItem: UserChatModel, newItem: UserChatModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserChatModel, newItem: UserChatModel): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        fun UserChatsAdapter.withLoadStateAdapters(
            header: CustomLoadStateAdapter<*>,
            footer: CustomLoadStateAdapter<*>,
            onEmpty: (show: Boolean) -> Unit
        ): ConcatAdapter {
            addLoadStateListener { loadState ->
                //refresh.loadState = loadState.refresh
                header.loadState = if (itemCount > 0) header.notRefresh else loadState.refresh
                footer.loadState = loadState.append

                if (loadState.refresh is LoadState.Error)
                    if (this.snapshot().isEmpty()) onEmpty.invoke(true)
                    else onEmpty.invoke(false)
                else onEmpty.invoke(false)
            }
            return ConcatAdapter(header, this, footer)
        }
    }
}