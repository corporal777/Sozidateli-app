package com.example.ui.chatList.contacts.items

import com.example.data.models.UserChat
import com.example.holders.UserChatItem
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

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