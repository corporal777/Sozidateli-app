package com.example.holders

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.ChatMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_message_text.*

class ChatMessageTextItem(
        message: ChatMessage.Personal
) : ChatMessageItem(message) {

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            tvChatMessage.apply {
                text = message.message.message
                setTextColor(ContextCompat.getColor(context, if (message.isMyMessage) R.color.chat_message_text_outgoing
                else R.color.chat_message_text_incoming))
            }
        }
    }

    override fun getGuidLineStart(viewHolder:GroupieViewHolder): Guideline = viewHolder.guidelineStart
    override fun getGuidLineEnd(viewHolder:GroupieViewHolder): Guideline = viewHolder.guidelineEnd
    override fun getMessageContainer(viewHolder:GroupieViewHolder): View = viewHolder.tvChatMessage
    override fun getLayout() = R.layout.item_chat_message_text
}