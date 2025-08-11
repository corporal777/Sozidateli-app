package com.example.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.examle.data.AppData
import com.example.common.constants.FIELD_ACTION
import com.example.common.constants.FIELD_NOTIFICATION_ID
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NotificationClickJobService : JobService() {

    @Inject
    lateinit var appData: AppData

//    @Inject
//    lateinit var userRepository: UserRepository

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters): Boolean {
        val extras = params.extras
        val action = extras.getString(FIELD_ACTION) ?: return false
        val notificationId = extras.getInt(FIELD_NOTIFICATION_ID)

//        compositeDisposable += when (action) {
//            ACTION_MARK_AS_READ -> userRepository.markAsRead(notificationId.toString())
//            ACTION_ACCEPT -> userRepository.notificationsInviteAccept(notificationId)
//            ACTION_DECLINE -> userRepository.notificationsInviteDecline(notificationId)
//            else -> return false
//        }
//                .performOnBackgroundOutOnMain()
//                .subscribe({
//                    jobFinished(params, false)
//                }, {
//                    jobFinished(params, false)
//                })

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        compositeDisposable.clear()
        return false
    }

    companion object {
        const val ACTION_MARK_AS_READ = "mark as read"
        const val ACTION_ACCEPT = "accept"
        const val ACTION_DECLINE = "decline"
    }
}