package com.example.ui.notifications

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class NotificationsPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val eventRepository: EventRepository,
        private val appData: AppData
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    private var notifications: List<Notification> = emptyList()

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.getNotifications(limit, offset).map { response ->
            PaginationResponse(response.totalCount, response.data.map {
                Notification(
                        it.id,
                        it.type,
                        it.text,
                        it.time,
                        when (it.type) {
                            RemoteNotification.TYPE_RATE -> Notification.Type.RATE
                            RemoteNotification.TYPE_INVITE -> Notification.Type.ACCEPTABLE
                            else -> Notification.Type.SIMPLE
                        },
                        it.status != RemoteNotification.STATUS_NONE,
                        when (it.status) {
                            RemoteNotification.STATUS_ACKNOWLEDGED,
                            RemoteNotification.STATUS_NONE -> Notification.AcceptState.NONE
                            RemoteNotification.STATUS_ACCEPTED -> Notification.AcceptState.ACCEPTED
                            RemoteNotification.STATUS_DECLINED -> Notification.AcceptState.CANCELED
                            RemoteNotification.STATUS_CANCELLED -> Notification.AcceptState.DISABLED
                            else -> Notification.AcceptState.NONE
                        },
                        it.event_id
                )
            })
        }
    }
            .applyErrorHandler { viewState.showToast(it.message ?: it.localizedMessage) }
            .buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .subscribe({
                    notifications = it
                    viewState.apply { setData(it) }
                }, {
                    it.printStackTrace()
                })
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onNotificationAcceptClick(id: Int) {
        dummyCall {
            notifications.find { it.id == id }?.apply {
                acceptState = Notification.AcceptState.ACCEPTED
            }
            viewState.onNotificationNeedUpdate(id)
        }
    }

    override fun onNotificationCancelClick(id: Int) {
        dummyCall {
            notifications.find { it.id == id }?.apply {
                acceptState = Notification.AcceptState.CANCELED
            }
            viewState.onNotificationNeedUpdate(id)
        }
    }

    override fun onNotificationChangeDecisionClick(id: Int) {
        dummyCall {
            notifications.find { it.id == id }?.apply {
                acceptState = Notification.AcceptState.NONE
            }
            viewState.onNotificationNeedUpdate(id)
        }
    }

    override fun onNotificationReadMoreClick(id: Int) {

    }

    override fun onNotificationReadClick(id: Int) {
        compositeDisposable += readNotificationRequest(id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    notifications.find { it.id == id }?.apply {
                        wasRead = true
                    }
                    viewState.onNotificationNeedUpdate(id)
                }, {
                    it.printStackTrace()
                    viewState.showToast(it.message ?: it.localizedMessage)
                })
    }

    override fun onNotificationRateClick(id: Int) {
        viewState.showRatingChooser(id)
    }

    override fun onNotificationRatingChosen(id: Int, rating: Int) {
        val event = notifications.find { it.id == id }?.rateId ?: return
        compositeDisposable += eventRepository.setEventRating(event, rating)
                .andThen(readNotificationRequest(id))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    notifications.find { it.id == id }?.apply {
                        wasRead = true
                    }
                    viewState.onNotificationNeedUpdate(id)
                }, {
                    it.printStackTrace()
                    viewState.showToast(it.message ?: it.localizedMessage)
                })
    }

    private fun readNotificationRequest(id: Int): Completable {
        return userRepository.markNotificationsAsRead(listOf(id))
                .doOnSuccess { appData.notificationsCount -= it.countMarked }
                .flatMapCompletable { Completable.complete() }
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
