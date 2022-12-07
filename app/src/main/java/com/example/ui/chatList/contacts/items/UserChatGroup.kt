package com.example.ui.chatList.contacts.items

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventNew
import com.example.data.models.Message
import com.example.data.models.UserChat
import com.example.databinding.ItemChatBinding
import com.example.extensions.*
import com.example.holders.UserChatItem
import com.example.util.CHAT_SERVICE_MESSAGE_ACCEPT
import com.example.util.setCircleAvatar
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat.*
import java.util.*

class UserChatGroup(
    val userChat: UserChat,
    private val onClick: (UserChat) -> Unit,
    private val onBind: ((UserChatItem) -> Unit)? = null,
    private val onUnBind: ((UserChatItem) -> Unit)? = null,
    private val withDivider: Boolean
) : NestedGroup() {

    private val chatItem = UserChatItem(userChat, onClick, onBind, onUnBind, withDivider)

    init {
        chatItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> chatItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            chatItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    fun updateBadge(count: Int) {
        chatItem.updateBadge(count)
    }

    fun updateMessage(message: String) {
        chatItem.updateMessage(message)
    }

    override fun getGroupCount() = 1

}