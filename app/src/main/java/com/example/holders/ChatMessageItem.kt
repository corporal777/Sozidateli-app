package com.example.holders

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.RECTANGLE
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.example.R
import com.example.data.models.ChatMessage
import com.example.extensions.defaultTimeFormatter
import com.example.extensions.dp
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

abstract class ChatMessageItem(
        val message: ChatMessage.Personal
) : Item(message.message._id.hashCode().toLong()) {

    val incomingMessageGuildLineStartPercent = INCOMING_MESSAGE_GUID_LINE_START
    val incomingMessageGuildLineEndPercent = INCOMING_MESSAGE_GUID_LINE_END
    val outgoingMessageGuildLineStartPercent = OUTGOING_MESSAGE_GUID_LINE_START
    val outgoingMessageGuildLineEndPercent = OUTGOING_MESSAGE_GUID_LINE_END

    val incomingMessageBackgroundRes = INCOMING_MESSAGE_BACKGROUND_COLOR_RES
    val outgoingMessageBackgroundRes = OUTGOING_MESSAGE_BACKGROUND_COLOR_RES

    val cornersRadius = 12f.dp

    var onBindListener: (() -> Unit)? = null

    @CallSuper
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        onBindListener?.invoke()
        viewHolder.apply {
            getGuidLineStart(this)
                    .setGuidelinePercent(
                            if (message.isMyMessage) outgoingMessageGuildLineStartPercent
                            else incomingMessageGuildLineStartPercent
                    )

            getGuidLineEnd(this)
                    .setGuidelinePercent(
                            if (message.isMyMessage) outgoingMessageGuildLineEndPercent
                            else incomingMessageGuildLineEndPercent
                    )
            getMessageContainer(this)
                    .apply {
                        background = createBackgroundDrawable(
                                ContextCompat.getColor(context,
                                        if (message.isMyMessage) outgoingMessageBackgroundRes
                                        else incomingMessageBackgroundRes
                                )
                        )

                        when (val params = layoutParams) {
                            is ConstraintLayout.LayoutParams -> {
                                params.horizontalBias = if (message.isMyMessage) OUTGOING_MESSAGE_HORIZONTAL_BIAS
                                else INCOMING_MESSAGE_HORIZONTAL_BIAS
                            }
                        }
                    }

            getDateView(this).apply {
                text = defaultTimeFormatter.format(message.message.createdAt)
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    horizontalBias = if (message.isMyMessage) OUTGOING_MESSAGE_HORIZONTAL_BIAS
                    else INCOMING_MESSAGE_HORIZONTAL_BIAS
                }
            }

        }
    }

    private fun createBackgroundDrawable(color: Int): Drawable {
        return GradientDrawable().apply {
            shape = RECTANGLE
            setColor(color)
            cornerRadii = floatArrayOf(
                    cornersRadius,
                    cornersRadius,
                    cornersRadius,
                    cornersRadius,
                    cornersRadius,
                    cornersRadius,
                    cornersRadius,
                    cornersRadius
            )
        }
    }

    abstract fun getGuidLineStart(viewHolder: GroupieViewHolder): Guideline
    abstract fun getGuidLineEnd(viewHolder: GroupieViewHolder): Guideline
    abstract fun getMessageContainer(viewHolder: GroupieViewHolder): View
    abstract fun getDateView(viewHolder: GroupieViewHolder): TextView

    companion object {
        const val INCOMING_MESSAGE_GUID_LINE_START = 0f
        const val INCOMING_MESSAGE_GUID_LINE_END = 0.7f
        const val OUTGOING_MESSAGE_GUID_LINE_START = 0.3f
        const val OUTGOING_MESSAGE_GUID_LINE_END = 1f

        const val INCOMING_MESSAGE_BACKGROUND_COLOR_RES = R.color.chat_message_background_incoming
        const val OUTGOING_MESSAGE_BACKGROUND_COLOR_RES = R.color.chat_message_background_outgoing

        const val INCOMING_MESSAGE_HORIZONTAL_BIAS = 0f
        const val OUTGOING_MESSAGE_HORIZONTAL_BIAS = 1f
    }
}