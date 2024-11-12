package com.example.holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.data.models.ChatMessage
import com.example.app.databinding.ItemChatMessageTextBinding
import com.example.extensions.markWon

class ChatMessageTextItem(message: ChatMessage.Personal) : ChatMessageItem<ItemChatMessageTextBinding>(message) {

    override fun bind(viewBinding: ItemChatMessageTextBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            tvChatMessage.apply {
                markWon(context).setMarkdown(this, message.message.message)
                //text = message.message.message
                setTextColor(ContextCompat.getColor(context, if (message.isMyMessage) R.color.chat_message_text_outgoing
                else R.color.chat_message_text_incoming))
            }
            //setFadeAnimation(this.root)
        }
    }

    override fun getGuidLineStart(binding: ItemChatMessageTextBinding): Guideline = binding.guidelineStart
    override fun getGuidLineEnd(binding: ItemChatMessageTextBinding): Guideline = binding.guidelineEnd
    override fun getMessageContainer(binding: ItemChatMessageTextBinding): View = binding.tvChatMessage
    override fun getDateView(binding: ItemChatMessageTextBinding): TextView = binding.tvMessageDate

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ChatMessageTextItem) return false
        if (message != other.message) return false
        return true
    }

//    private fun setFadeAnimation(view: View) {
//        val anim = AlphaAnimation(0.0f, 1.0f)
//        if (counter <= 8){
//            anim.duration = 450
//        }else {
//            anim.duration = 150
//        }
//        view.startAnimation(anim)
//    }

    override fun getLayout() = R.layout.item_chat_message_text
}