package com.example.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.app.R
import com.example.ui.main.MainActivity
import javax.inject.Inject

class NotificationUtil @Inject constructor(
        private val context: Context
) {

    fun createNotification(
            channel: String,
            notificationId: Int,
            groupId: String? = null,
            builder: (NotificationCompat.Builder.() -> Unit)
    ) {
        val smallIcon = R.drawable.ic_notification

        val notificationBuilder = NotificationCompat.Builder(context, channel)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(smallIcon)
                .setGroup(groupId)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        builder(notificationBuilder)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        fun createNotificationIntent(
                context: Context,
                bundle: Bundle? = null
        ): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                bundle?.let { putExtras(it) }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            else PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        }
    }
}