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
import com.example.services.NotificationClickJobService.Companion.JOB_ID_MARK_AS_READ
import com.example.util.FIELD_JOB_ID
import com.example.util.FIELD_NOTIFICATION_ID
import timber.log.Timber


class NotificationClickBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.tag("NOTIFICATIONS_T").d("RECEIVE BROADCAST")

        val extras = intent.extras ?: return
        val jobId = extras.getInt(FIELD_JOB_ID, JOB_ID_INVALID)
        if (jobId == JOB_ID_INVALID) return

        val notificationId = extras.getInt(FIELD_NOTIFICATION_ID)

        val serviceName = ComponentName(context, NotificationClickJobService::class.java)
        val jobBuilder = JobInfo.Builder(jobId, serviceName)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)

        when (jobId) {
            JOB_ID_MARK_AS_READ -> {
                jobBuilder.setExtras(PersistableBundle().apply {
                    putInt(FIELD_NOTIFICATION_ID, notificationId)
                })
            }
            else -> return
        }

        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(jobBuilder.build())
        if (result == JobScheduler.RESULT_SUCCESS) {
            Timber.tag("NOTIFICATIONS_T").d("RESULT SUCCESS")
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }

    companion object {
        private const val JOB_ID_INVALID = -1
    }
}