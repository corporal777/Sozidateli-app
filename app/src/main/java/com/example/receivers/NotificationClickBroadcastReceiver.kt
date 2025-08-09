package com.example.receivers

import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import com.example.services.NotificationClickJobService
import com.example.common.FIELD_ACTION
import com.example.common.FIELD_NOTIFICATION_ID

class NotificationClickBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val action = extras.getString(FIELD_ACTION) ?: return
        val notificationId = extras.getInt(FIELD_NOTIFICATION_ID, NOTIFICATION_ID_INVALID)
        if (notificationId == NOTIFICATION_ID_INVALID) return

        val serviceName = ComponentName(context, NotificationClickJobService::class.java)
        val jobBuilder = JobInfo.Builder(notificationId, serviceName)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)
                .setExtras(PersistableBundle().apply {
                    putString(FIELD_ACTION, action)
                    putInt(FIELD_NOTIFICATION_ID, notificationId)
                })

        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobBuilder.build())
        if (result == JobScheduler.RESULT_SUCCESS) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }

    companion object {
        private const val NOTIFICATION_ID_INVALID = -1
    }
}