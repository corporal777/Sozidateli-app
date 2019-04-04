package com.example.util.chat

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import bundleOf
import com.example.R
import com.example.ui.main.MainActivity
import com.example.util.*

class ChatNotificationHelper(private val context: Context) {

    var currentChatId: String? = null
    var isConnectingToLastMessageDatabase = false

    val showedMessages = HashSet<String>()

    fun showNotificationIfCan(
            chatId: String,
            messageId: String,
            message: String,
            senderId: Int,
            senderName: String,
            avatarUrl: String?
    ) {
        if (isCanSendMessage(chatId, messageId) && showedMessages.add(messageId)) {
            showNotification(context, chatId, messageId, message, senderId, senderName, avatarUrl)
        }
    }

    fun isCanSendMessage(chatId: String, messageId: String): Boolean {
        return !showedMessages.contains(messageId)
                && currentChatId != chatId
    }

    companion object {

        fun showNotification(
                context: Context,
                chatId: String,
                messageId: String,
                message: String,
                senderId: Int,
                senderName: String,
                avatarUrl: String?
        ) {
            avatarUrl.loadBitmap(listOf(CropCircleTransformation())) { bitmap ->
                val channel = context.getString(R.string.app_name)
                val intent = createNotificationIntent(context, chatId, senderId, senderName, messageId)
                NotificationUtil.createNotification(
                        context = context,
                        channel = channel,
                        notificationId = messageId.hashCode(),
                        title = senderName,
                        message = message,
                        intent = intent,
                        largeIcon = bitmap,
                        groupId = chatId
                )
            }
        }

        private fun createNotificationIntent(
                context: Context,
                chatId: String,
                senderId: Int,
                senderName: String,
                notificationId: String
        ): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra(FIELD_CHAT, bundleOf(
                        FIELD_CHAT_ID to chatId,
                        FIELD_SENDER_ID to senderId.toString(),
                        FIELD_LABEL to senderName,
                        FIELD_NOTIFICATION_ID to notificationId
                ))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}