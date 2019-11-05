package com.example.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.util.FIELD_NOTIFICATION_ID
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import timber.log.Timber
import javax.inject.Inject

class NotificationClickJobService : JobService() {

    @Inject
    lateinit var eventRepository: EventRepository

    @Inject
    lateinit var userRepository: UserRepository

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.tag("NOTIFICATIONS_T").d("START SERVICE")
        compositeDisposable += when (params?.jobId) {
            JOB_ID_MARK_AS_READ -> {
                val extras = params.extras
                val ids = extras.getInt(FIELD_NOTIFICATION_ID)
                userRepository.markNotificationsAsRead(listOf(ids))
            }
            else -> return false
        }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    Timber.tag("NOTIFICATIONS_T").d("SERVICE COMPLETE")
                    jobFinished(params, false)
                }, {
                    Timber.tag("NOTIFICATIONS_T").d("SERVICE ERROR ${it.message}")
                    jobFinished(params, false)
                })

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.tag("NOTIFICATIONS_T").d("STOP SERVICE")
        return false
    }

    companion object {
        const val JOB_ID_MARK_AS_READ = 1
    }
}