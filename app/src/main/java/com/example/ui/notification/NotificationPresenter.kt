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
import java.util.concurrent.TimeUnit
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
        dummyCall {
            notification.acceptState = Notification.AcceptState.ACCEPTED
            viewState.setData(notification)
        }
    }

    override fun onNotificationCancelClick() {
        dummyCall {
            notification.acceptState = Notification.AcceptState.CANCELED
            viewState.setData(notification)
        }
    }

    override fun onNotificationChangeDecisionClick() {
        dummyCall {
            notification.acceptState = Notification.AcceptState.NONE
            viewState.setData(notification)
        }
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

    private fun dummyCall(onComplete: () -> Unit) {
        compositeDisposable += Completable.complete()
                .delay(1, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe {
                    viewState.showToast("ОЖИДАЕТ РЕАЛИЗАЦИИ")
                    onComplete()
                }
    }
}
