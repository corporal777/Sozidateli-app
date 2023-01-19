package com.example.services

import android.app.job.JobParameters
import android.app.job.JobService
import androidx.core.app.JobIntentService
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.util.FIELD_ACTION
import com.example.util.FIELD_NOTIFICATION_ID
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

class NotificationClickJobService : JobService() {

    @Inject
    lateinit var appData: AppData

    @Inject
    lateinit var userRepository: UserRepository

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters): Boolean {
        val extras = params.extras ?: return false
        val action = extras.getString(FIELD_ACTION) ?: return false
        val notificationId = extras.getInt(FIELD_NOTIFICATION_ID)

        compositeDisposable += when (action) {
            ACTION_MARK_AS_READ -> userRepository.markAsRead(notificationId.toString())
            ACTION_ACCEPT -> userRepository.notificationsInviteAccept(notificationId)
            ACTION_DECLINE -> userRepository.notificationsInviteDecline(notificationId)
            else -> return false
        }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    jobFinished(params, false)
                }, {
                    jobFinished(params, false)
                })

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