package com.example.holders

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.ChatMessage
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_chat_message_text.*

class ChatMessageTextItem(
        message: ChatMessage.Personal
) : ChatMessageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            tvChatMessage.apply {
                text = message.message.message
                setTextColor(ContextCompat.getColor(context, if (message.isMyMessage) R.color.chat_message_text_outgoing
                else R.color.chat_message_text_incoming))

                setCompoundDrawablesWithIntrinsicBounds(0, 0, if (message.message.wasRead && message.isMyMessage) R.drawable.ic_read_msg else 0, 0)
                compoundDrawablePadding = context.resources.getDimensionPixelSize(R.dimen.chat_padding_read_icon)
            }
        }
    }

    override fun getGuidLineStart(viewHolder: ViewHolder): Guideline = viewHolder.guidelineStart
    override fun getGuidLineEnd(viewHolder: ViewHolder): Guideline = viewHolder.guidelineEnd
    override fun getMessageContainer(viewHolder: ViewHolder): View = viewHolder.tvChatMessage
    override fun getLayout() = R.layout.item_chat_message_text
}