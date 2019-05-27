package com.example.util

import android.annotation.SuppressLint
import com.example.R
import com.example.data.models.UserChat
import com.example.data.prefs.AppPrefs
import com.example.repository.ChatRepository
import com.example.util.chat.ChatNotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.android.AndroidInjection
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.models.Message
import javax.inject.Inject

class FcmMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var chatNotificationHelper: ChatNotificationHelper

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let { sendNotification(it) }
    }

    override fun onNewToken(p0: String?) {

    }

    @SuppressLint("CheckResult")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        if (chatNotificationHelper.isConnectingToSocket) return

        val chatData = remoteMessage.data[DATA_CHAT_OBJECT] ?: return
        val userChat = Gson().fromJson(chatData, UserChat::class.java) ?: return
        val chatId = userChat.id.toString()
        val messageId = userChat.messageId ?: return

        if (!chatNotificationHelper.isCanSendMessage(chatId, messageId)) return

        val message = when (userChat.lastMessageType) {
            Message.Type.IMAGE -> getString(R.string.chat_photo_message_text)
            else -> userChat.lastMessage
        } ?: return

        val senderId = userChat.userSender?.user_id ?: return
        val senderName = userChat.userSender?.fullName ?: return

        Completable.fromAction {
            chatNotificationHelper.showNotificationIfCan(
                    chatId = chatId,
                    messageId = messageId,
                    message = message,
                    senderId = senderId,
                    senderName = senderName,
                    avatarUrl = userChat.userSender?.user_avatar
            )
        }.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    companion object {
        private const val DATA_CHAT_OBJECT = "object"
    }
}