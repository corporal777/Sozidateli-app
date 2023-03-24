package com.example.ui.notification.center.redesign.types

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.NotificationsListContract
import javax.inject.Inject

@InjectViewState
class NotificationTypePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager
) : BasePresenter<NotificationTypeContract.View>(appData), NotificationTypeContract.Presenter {


    lateinit var notificationType: NotificationType

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        when(notificationType){
            NotificationType.SYSTEM -> {
                viewState.setToolbarTitle(R.string.system_notifications_short)
            }
            NotificationType.ESTIMATES -> {
                viewState.setToolbarTitle(R.string.estimates_rf)
            }
            NotificationType.ORGANIZER -> {
                viewState.setToolbarTitle(R.string.organizer)
            }
            NotificationType.EVENTS -> {
                viewState.setToolbarTitle(R.string.events)
            }
            NotificationType.PROJECTS -> {
                viewState.setToolbarTitle(R.string.my_projects)
            }
        }
    }
}