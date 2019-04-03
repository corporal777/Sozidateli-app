package com.example.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R

object NotificationUtil {

    fun createNotification(
            context: Context,
            channel: String,
            notificationId: Int,
            title: String? = null,
            message: String? = null,
            intent: PendingIntent?,
            largeIcon: Bitmap? = null,
            groupId: String? = null
    ) {
        val smallIcon = R.mipmap.ic_launcher_round

        val notificationBuilder = NotificationCompat.Builder(context, channel)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(smallIcon)
                .setGroup(groupId)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(intent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && groupId != null) {
            val summaryNotificationBuilder = NotificationCompat.Builder(context, channel)
                    .setSmallIcon(smallIcon)
                    .setGroup(groupId)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSound(null)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    .setGroupSummary(true)
                    .setContentIntent(intent)
            notificationManager.notify(groupId.hashCode(), summaryNotificationBuilder.build())
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}