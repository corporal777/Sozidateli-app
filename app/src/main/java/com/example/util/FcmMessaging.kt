package com.example.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.DEFAULT_ALL
import com.example.R
import com.example.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let { sendNotification(it) }
    }

    override fun onNewToken(p0: String?) {

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val channel = getString(R.string.app_name)

        NotificationCompat.Builder(this, channel)
                .setDefaults(DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setAutoCancel(true)
                .setContentIntent(createNotificationIntent(remoteMessage))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .apply {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(remoteMessage.data?.get(DATA_MESSAGE_ID)?.hashCode()
                            ?: 0, this.build())
                }
    }

    private fun createNotificationIntent(remoteMessage: RemoteMessage): PendingIntent {
        val link = remoteMessage.notification?.link
        val intent = if (link != null) {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = link
            }
        } else {
            Intent(this, MainActivity::class.java).apply {
                putExtras(remoteMessage.toIntent())
            }
        }

        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        private const val DATA_MESSAGE_ID = "message_id"
    }
}