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
import androidx.databinding.ViewDataBinding
import com.example.app.R
import com.example.data.models.ChatMessage
import com.example.extensions.defaultTimeFormatter
import com.example.extensions.dp
import com.xwray.groupie.databinding.BindableItem

abstract class ChatMessageItem<T : ViewDataBinding>(
        val message: ChatMessage.Personal
) : BindableItem<T>(message.message._id.hashCode().toLong()) {

    val incomingMessageGuildLineStartPercent = INCOMING_MESSAGE_GUID_LINE_START
    val incomingMessageGuildLineEndPercent = INCOMING_MESSAGE_GUID_LINE_END
    val outgoingMessageGuildLineStartPercent = OUTGOING_MESSAGE_GUID_LINE_START
    val outgoingMessageGuildLineEndPercent = OUTGOING_MESSAGE_GUID_LINE_END

    val incomingMessageBackgroundRes = INCOMING_MESSAGE_BACKGROUND_COLOR_RES
    val outgoingMessageBackgroundRes = OUTGOING_MESSAGE_BACKGROUND_COLOR_RES

    val cornersRadius = 12f.dp

    var onBindListener: (() -> Unit)? = null

    @CallSuper
    override fun bind(viewBinding: T, position: Int) {
        onBindListener?.invoke()
        viewBinding.apply {
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

    abstract fun getGuidLineStart(binding : T): Guideline
    abstract fun getGuidLineEnd(binding : T): Guideline
    abstract fun getMessageContainer(binding : T): View
    abstract fun getDateView(binding : T): TextView

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