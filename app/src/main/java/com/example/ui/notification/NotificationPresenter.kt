package com.example.ui.notification

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class NotificationPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val eventRepository: EventRepository,
        private val appData: AppData,
        private val notificationManager: NotificationManager
) : BasePresenter<NotificationContract.View>(), NotificationContract.Presenter {

    lateinit var notification: Notification
    var showButtons = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(notification, showButtons)
        if (!notification.wasRead && notification.type != Notification.Type.RATE) {
            compositeDisposable += userRepository.markNotificationsAsRead(listOf(notification.id))
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        notification.wasRead = true
                    }
        }

        compositeDisposable += appData.notificationReadSubject
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    val id = it.first
                    val state = it.second
                    if (id == notification.id) {
                        notification.apply {
                            wasRead = true
                            acceptState = state
                        }
                        viewState.setData(notification, showButtons)
                    }
                }
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationAcceptClick() {
        updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id), notification.id)
    }

    override fun onNotificationCancelClick() {
        updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id), notification.id)
    }

    override fun onNotificationChangeDecisionClick() {
        notification.acceptState = Notification.AcceptState.NONE
        viewState.setData(notification, showButtons)
    }

    override fun onNotificationRateClick() {
        viewState.showRatingChooser()
    }

    override fun onNotificationRatingChosen(rating: Int) {
        val event = notification.rateId ?: return
        compositeDisposable += eventRepository.setEventRating(event, rating)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { }
    }

    private fun updateNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { notificationManager.cancel(notificationId) }
    }
}
