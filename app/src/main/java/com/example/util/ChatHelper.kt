package com.example.util

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.MainThread
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import com.example.R
import com.example.data.models.MessageModel
import javax.inject.Inject

class ChatHelper @Inject constructor(
        private val context: Context,
        private val notificationUtil: NotificationUtil
) {

    var currentChatId: String? = null
    var isConnectingToSocket = false

    private val showedMessages = HashSet<String>()

    @MainThread
    fun showNotificationIfCan(
            chatId: String,
            messageId: String,
            title: String,
            message: String,
            label: String,
            icon: String?
    ) {
        if (isCanSendMessage(chatId, messageId)) {
            val intent = NotificationUtil.createNotificationIntent(context, bundleOf(FIELD_CHAT to bundleOf(
                    FIELD_CHAT_ID to chatId,
                    FIELD_LABEL to label,
                    FIELD_NOTIFICATION_ID to messageId
            )))

            val channel = context.getString(R.string.app_name)

            val send: (Bitmap?) -> Unit = { bitmap ->
                if (isCanSendMessage(chatId, messageId) && showedMessages.add(messageId))
                    notificationUtil.createNotification(
                            channel = channel,
                            notificationId = messageId.hashCode(),
                            groupId = chatId
                    ) {
                        setContentTitle(title)
                        setContentText(message)
                        setTicker(message)
                        setStyle(NotificationCompat.BigTextStyle().bigText(message))
                        setContentIntent(intent)
                        if (bitmap != null) setLargeIcon(bitmap)
                        else {
                            ContextCompat.getDrawable(context, R.drawable.avatar_placeholder_circle)?.let {
                                setLargeIcon(it.toBitmap())
                            }
                        }
                    }
            }

            icon.loadBitmap(listOf(CropCircleTransformation()), send)
        }
    }

    @MainThread
    fun isCanSendMessage(chatId: String, messageId: String): Boolean {
        return !showedMessages.contains(messageId) && currentChatId != chatId
    }

    @MainThread
    fun showMessageNotification(message : MessageModel?){
        if (message == null) return
        showNotificationIfCan(
            message.chat.toString(),
            message.id.toString(),
            message.sender?.name + " " + message.sender?.lastName,
            message.message ?: "",
            "",
            message.sender?.avatar
        )
    }
}