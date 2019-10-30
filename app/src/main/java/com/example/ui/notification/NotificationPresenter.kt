package com.example.ui.notification

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
        private val appData: AppData
) : BasePresenter<NotificationContract.View>(), NotificationContract.Presenter {

    lateinit var notification: Notification

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(notification)
        if (!notification.wasRead && notification.type != Notification.Type.RATE) {
            compositeDisposable += userRepository.markNotificationsAsRead(listOf(notification.id))
                    .doOnSuccess { appData.notificationsCount -= it.countMarked }
                    .flatMapCompletable { Completable.complete() }
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        notification.wasRead = true
                    }, {
                        it.printStackTrace()
                        viewState.showToast(it.message ?: it.localizedMessage)
                    })
        }
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationAcceptClick() {
        updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id.toString()), Notification.AcceptState.ACCEPTED)
    }

    override fun onNotificationCancelClick() {
        updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id.toString()), Notification.AcceptState.CANCELED)

    }

    override fun onNotificationChangeDecisionClick() {
        notification.acceptState = Notification.AcceptState.NONE
        viewState.setData(notification)
    }

    override fun onNotificationRateClick() {
        viewState.showRatingChooser()
    }

    override fun onNotificationRatingChosen(rating: Int) {
        val event = notification.rateId ?: return
        compositeDisposable += eventRepository.setEventRating(event, rating)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    notification.wasRead = true
                    viewState.setData(notification)
                }, {
                    it.printStackTrace()
                    viewState.showToast(it.message ?: it.localizedMessage)
                })
    }

    private fun updateNotificationInvite(request: Completable, newState: Notification.AcceptState) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    notification.apply {
                        wasRead = true
                        acceptState = newState
                    }
                    viewState.setData(notification)
                }
    }
}
