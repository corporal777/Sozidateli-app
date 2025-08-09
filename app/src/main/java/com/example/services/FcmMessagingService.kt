package com.example.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.example.app.R
import com.examle.data.AppData
import com.example.data.models.RemoteNotification
import com.example.data.models.RemoteNotification.Companion.TYPE_INVITE
import com.example.receivers.NotificationClickBroadcastReceiver
import com.example.util.ChatHelper
import com.example.util.CropCircleTransformation
import com.example.common.FIELD_ACTION
import com.example.common.FIELD_EVENT
import com.example.common.FIELD_NOTIFICATION
import com.example.common.FIELD_NOTIFICATION_ID
import com.example.util.NotificationUtil
import com.example.util.loadBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import javax.inject.Inject

class FcmMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var chatHelper: ChatHelper

    @Inject
    lateinit var notificationUtil: NotificationUtil

//    @Inject
//    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var appData: AppData

    private lateinit var channel: String

    override fun onCreate() {
        super.onCreate()
        channel = getString(R.string.app_name)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        processMessage(remoteMessage)
    }

    private fun processMessage(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        when (data[DATA_TYPE]) {
            TYPE_CHAT_MESSAGE -> processChatMessage(data)
            TYPE_REGISTRATION_APPROVE,
            TYPE_REGISTRATION_DECLINE,
            TYPE_REGISTRATION_CANCELLED -> processSimpleNotification(data)
            TYPE_NOTIFICATION -> processNotification(data)
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
        val avatar = data[DATA_CHAT_USER_AVATAR]

        setMessageToMainThread { chatHelper.showNotificationIfCan(chatId, messageId, title, body, name, avatar) }
    }

    private fun processSimpleNotification(data: Map<String, String>) {
        val title = data[DATA_TITLE] ?: return
        val body = data[DATA_BODY] ?: return

        val notificationId = data[DATA_NOTIFICATION_ID]?.toIntOrNull() ?: return
        appData.notificationsCount += 1

        val eventId = data[DATA_EVENT_ID] ?: return
        val eventLogo = data[DATA_EVENT_LOGO]

        val intent = NotificationUtil.createNotificationIntent(this, bundleOf(FIELD_EVENT to eventId))

        setMessageToMainThread {
            val send: (Bitmap?) -> Unit = { bitmap ->
                notificationUtil.createNotification(
                        channel = channel,
                        notificationId = notificationId
                ) {
                    setContentTitle(title)
                    setContentText(body)
                    setTicker(body)
                    setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    setContentIntent(intent)
                    if (bitmap != null) setLargeIcon(bitmap)

                    val actionIntent = PendingIntent.getBroadcast(
                            this@FcmMessagingService,
                            notificationId + NotificationClickJobService.ACTION_MARK_AS_READ.hashCode(),
                            Intent(this@FcmMessagingService, NotificationClickBroadcastReceiver::class.java).apply {
                                putExtras(bundleOf(
                                        FIELD_NOTIFICATION_ID to notificationId,
                                        FIELD_ACTION to NotificationClickJobService.ACTION_MARK_AS_READ
                                ))
                            },
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    addAction(0, getString(R.string.notifications_mark_as_read), actionIntent)
                }
            }

            eventLogo.loadBitmap(listOf(CropCircleTransformation()), send)
        }
    }

    private fun processNotification(data: Map<String, String>) {
        val objectJson = data[DATA_OBJECT] ?: return
        val notification = Gson().fromJson(objectJson, RemoteNotification::class.java) ?: return
        val notificationId = notification.id
        appData.notificationsCount += 1

        val title = data[DATA_TITLE] ?: return
        val body = data[DATA_BODY] ?: return

        val intent = NotificationUtil.createNotificationIntent(this, bundleOf(FIELD_NOTIFICATION to notification))
        notificationUtil.createNotification(
                channel = channel,
                notificationId = notificationId
        ) {
            setContentTitle(title)
            setContentText(body)
            setTicker(body)
            setStyle(NotificationCompat.BigTextStyle().bigText(body))
            setContentIntent(intent)

            if (notification.type == TYPE_INVITE) {
                val actionAcceptIntent = PendingIntent.getBroadcast(
                        this@FcmMessagingService,
                        notificationId + NotificationClickJobService.ACTION_ACCEPT.hashCode(),
                        Intent(this@FcmMessagingService, NotificationClickBroadcastReceiver::class.java).apply {
                            putExtras(bundleOf(
                                    FIELD_NOTIFICATION_ID to notificationId,
                                    FIELD_ACTION to NotificationClickJobService.ACTION_ACCEPT
                            ))
                        },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                addAction(0, getString(R.string.notifications_accept), actionAcceptIntent)

                val actionDeclineIntent = PendingIntent.getBroadcast(
                        this@FcmMessagingService,
                        notificationId + NotificationClickJobService.ACTION_DECLINE.hashCode(),
                        Intent(this@FcmMessagingService, NotificationClickBroadcastReceiver::class.java).apply {
                            putExtras(bundleOf(
                                    FIELD_NOTIFICATION_ID to notificationId,
                                    FIELD_ACTION to NotificationClickJobService.ACTION_DECLINE
                            ))
                        },
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                addAction(0, getString(R.string.notifications_cancel), actionDeclineIntent)
            }
        }
    }

    private fun sendNoTypeNotification(id: Int, title: String?, body: String?) {
        if (title == null && body == null) return

        notificationUtil.createNotification(notificationId = id, channel = channel) {
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

        private const val DATA_OBJECT = "object"
    }
}