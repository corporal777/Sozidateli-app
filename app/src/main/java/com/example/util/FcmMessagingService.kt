package com.example.util

import android.annotation.SuppressLint
import com.example.data.models.UserChat
import com.example.data.prefs.AppPrefs
import com.example.repository.ChatRepository
import com.example.util.chat.ChatNotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.android.AndroidInjection
import io.reactivex.Completable
import performOnBackgroundOutOnMain
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
        if (chatNotificationHelper.isConnectingToLastMessageDatabase) return

        val chatData = remoteMessage.data[DATA_CHAT_OBJECT] ?: return
        val userChat = Gson().fromJson(chatData, UserChat::class.java) ?: return
        val chatId = userChat.id.toString()
        val messageId = userChat.messageId ?: return

        if (!chatNotificationHelper.isCanSendMessage(chatId, messageId)) return

        chatRepository.getMessage(chatId, messageId)
                .map { it.isShowed ?: false }
                .onErrorReturn { false }
                .flatMapCompletable {
                    if (!it) chatRepository.setMessageShowed(appPrefs.userId.let { uid ->
                        if (uid == -1) null else uid.toString()
                    }, chatId, messageId)
                    else Completable.complete()
                }
                .andThen(Completable.fromAction {
                    val message = userChat.lastMessage ?: return@fromAction
                    val senderId = userChat.userSender?.user_id ?: return@fromAction
                    val senderName = userChat.userSender?.fullName ?: return@fromAction
                    chatNotificationHelper.showNotificationIfCan(
                            chatId = chatId,
                            messageId = messageId,
                            message = message,
                            senderId = senderId,
                            senderName = senderName,
                            avatarUrl = userChat.userSender?.user_avatar
                    )
                })
                .performOnBackgroundOutOnMain()
                .subscribe({

                }, {
                    it.printStackTrace()
                })
    }

    companion object {
        private const val DATA_CHAT_OBJECT = "object"
    }
}