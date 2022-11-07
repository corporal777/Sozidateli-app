package com.example.holders

import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.ChatMessage
import com.example.util.markWon
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_chat_message_text.*

class ChatMessageTextItem(
        message: ChatMessage.Personal,
        val canAnim : Boolean
) : ChatMessageItem(message) {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.apply {
            tvChatMessage.apply {
                markWon(context).setMarkdown(this, message.message.message)
                //text = message.message.message
                setTextColor(ContextCompat.getColor(context, if (message.isMyMessage) R.color.chat_message_text_outgoing
                else R.color.chat_message_text_incoming))
            }
            if (canAnim){
                setFadeAnimation(this.root)
            }
        }
    }

    override fun getGuidLineStart(viewHolder: GroupieViewHolder): Guideline = viewHolder.guidelineStart
    override fun getGuidLineEnd(viewHolder: GroupieViewHolder): Guideline = viewHolder.guidelineEnd
    override fun getMessageContainer(viewHolder: GroupieViewHolder): View = viewHolder.tvChatMessage
    override fun getDateView(viewHolder: GroupieViewHolder): TextView = viewHolder.tvMessageDate

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ChatMessageTextItem) return false
        if (message != other.message) return false
        return true
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 450
        view.startAnimation(anim)
    }

    override fun getLayout() = R.layout.item_chat_message_text
}