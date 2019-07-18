package com.example.holders

import android.view.View
import android.widget.EditText
import com.example.R
import com.example.util.BADGE_COUNT_MAX
import com.example.util.BADGE_TEXT_IF_MORE_THAN_MAX
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat_list_header.*

class ChatListHeaderItem(
        private val onInputClick: () -> Unit,
        private val onFilterClick: () -> Unit,
        private val onShowChatsClick: () -> Unit,
        private val onShowInvitesClick: () -> Unit
) : Item(-2L) {

    private var selectedButton = SELECTED_BUTTON_NONE
    private var viewHolder: ViewHolder? = null

    var invitesCount: Int = 0

    override fun bind(viewHolder: ViewHolder, position: Int) {
        this.viewHolder = viewHolder
        viewHolder.apply {
            btnChats.apply {
                isSelected = selectedButton == SELECTED_BUTTON_CHATS
                setOnClickListener { onShowChatsClick() }
            }

            btnRequests.apply {
                isSelected = selectedButton == SELECTED_BUTTON_REQUESTS
                setOnClickListener { onShowInvitesClick() }
            }

            (etSearch as EditText).apply {
                setOnKeyListener(null)
                isFocusable = false
                isFocusableInTouchMode = false
                isLongClickable = false

                setOnClickListener { onInputClick() }
            }
        }

        updateInvitesBadge()
    }

    fun selectChatsButton() {
        selectedButton = SELECTED_BUTTON_CHATS
        viewHolder?.apply {
            btnRequests.isSelected = false
            btnChats.isSelected = true
        }
    }

    fun selectRequestsButton() {
        selectedButton = SELECTED_BUTTON_REQUESTS
        viewHolder?.apply {
            btnChats.isSelected = false
            btnRequests.isSelected = true
        }
    }

    fun updateInvitesBadge() {
        viewHolder?.tvBadge?.apply {
            text = invitesCount.let { count: Int -> if (count > BADGE_COUNT_MAX) BADGE_TEXT_IF_MORE_THAN_MAX else count.toString() }
            visibility = if (invitesCount == 0) View.GONE else View.VISIBLE
        }
    }

    override fun getLayout() = R.layout.item_chat_list_header

    companion object {
        private const val SELECTED_BUTTON_NONE = -1
        private const val SELECTED_BUTTON_CHATS = 0
        private const val SELECTED_BUTTON_REQUESTS = 1
    }
}