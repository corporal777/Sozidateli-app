package com.example.util

import android.graphics.Bitmap
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.example.R
import com.example.data.prefs.AppPrefs
import com.example.repository.ChatRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class FcmMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var chatHelper: ChatHelper

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var appPrefs: AppPrefs

    private lateinit var channel: String

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        channel = getString(R.string.app_name)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        processMessage(remoteMessage)
    }

    private fun processMessage(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        Timber.tag("NOTIFICATIONS_T").d("RECEIVE MESSAGE: ${remoteMessage.notification?.title}/${remoteMessage.notification?.body}")
        data.forEach {
            Timber.tag("NOTIFICATIONS_T").d("${it.key}: ${it.value}")
        }
        Timber.tag("NOTIFICATIONS_T").d("==============")

        when (data[DATA_TYPE]) {
            TYPE_CHAT_MESSAGE -> processChatMessage(data)
            TYPE_REGISTRATION_APPROVE -> processSimpleNotification(data)
            TYPE_REGISTRATION_DECLINE -> processSimpleNotification(data)
            TYPE_REGISTRATION_CANCELLED -> processSimpleNotification(data)
            TYPE_NOTIFICATION -> processChatMessage(data)
            else -> sendNoTypeNotification(remoteMessage.messageId.hashCode(),
                    remoteMessage.notification?.title ?: data[DATA_TITLE],
                    remoteMessage.notification?.body ?: data[DATA_BODY]
            )
        }
    }

    private fun processChatMessage(data: Map<String, String>) {
        val title = data[DATA_TITLE] ?: return
        val body = data[DATA_BODY] ?: return

        val chatId = data[DATA_CHAT_ID] ?: return
        val messageId = data[DATA_CHAT_MESSAGE_ID] ?: return
        val name = data[DATA_CHAT_USER_NAME] ?: return
        val avatar = data[DATA_CHAT_USER_AVATAR] ?: return

        setMessageToMainThread { chatHelper.showNotificationIfCan(chatId, messageId, title, body, name, avatar) }
    }

    private fun processSimpleNotification(data: Map<String, String>) {
        val title = data[DATA_TITLE] ?: return
        val body = data[DATA_BODY] ?: return

        val notificationId = data[DATA_NOTIFICATION_ID] ?: return
        val eventId = data[DATA_EVENT_ID] ?: return
        val eventLogo = data[DATA_EVENT_LOGO] ?: return

        val intent = NotificationUtil.createNotificationIntent(this, bundleOf(FIELD_EVENT to bundleOf(
                FIELD_EVENT_ID to eventId,
                FIELD_NOTIFICATION_ID to notificationId
        )))

        setMessageToMainThread {
            val send: (Bitmap?) -> Unit = { bitmap ->
                notificationUtil.createNotification(
                        channel = channel,
                        notificationId = notificationId.hashCode()
                ) {
                    setContentTitle(title)
                    setContentText(body)
                    setTicker(body)
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    setContentIntent(intent)
                    if (bitmap != null) setLargeIcon(bitmap)
                }
            }

            eventLogo.loadBitmap(listOf(CropCircleTransformation()), send)
        }
    }

    private fun sendNoTypeNotification(id: Int, title: String?, body: String?) {
        if (title == null && body == null) return

        notificationUtil.createNotification(
                notificationId = id,
                channel = channel
        ) {
            setContentTitle(title)
            setContentText(body)
            setTicker(body)
            setStyle(NotificationCompat.BigTextStyle().bigText(body))
            setContentIntent(NotificationUtil.createNotificationIntent(this@FcmMessagingService))
        }
    }

    private fun setMessageToMainThread(send: () -> Unit) {
        Handler(mainLooper).post(send)
    }

    companion object {
        private const val TYPE_CHAT_MESSAGE = "new-chat-message"
        private const val TYPE_REGISTRATION_APPROVE = "approved-event-registration"
        private const val TYPE_REGISTRATION_DECLINE = "declined-event-registration"
        private const val TYPE_REGISTRATION_CANCELLED = "canceled-event-registration"
        private const val TYPE_NOTIFICATION = "notification"

        private const val DATA_TITLE = "title"
        private const val DATA_BODY = "body"
        private const val DATA_TYPE = "type"

        private const val DATA_CHAT_ID = "chat_id"
        private const val DATA_CHAT_MESSAGE_ID = "chat_message_id"
        private const val DATA_CHAT_USER_NAME = "chat_user_name"
        private const val DATA_CHAT_USER_AVATAR = "chat_user_avatar"

        private const val DATA_NOTIFICATION_ID = "notification_id"

        private const val DATA_EVENT_ID = "event_id"
        private const val DATA_EVENT_LOGO = "event_logo"
        private const val DATA_EVENT_NAME = "event_name"
    }
}