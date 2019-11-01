package com.example.ui.notification.center

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ApiError
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
import javax.inject.Inject

@InjectViewState
class NotificationsPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val eventRepository: EventRepository,
        private val appData: AppData
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    private var firstLaunch = true
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

    override fun attachView(view: NotificationsContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun onReceiveApiError(apiError: ApiError) {
        viewState.showToast(apiError.toErrorsString())
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onNotificationAcceptClick(id: Int) {
        updateNotificationInvite(userRepository.notificationsInviteAccept(id.toString()), id, Notification.AcceptState.ACCEPTED)
    }

    override fun onNotificationCancelClick(id: Int) {
        updateNotificationInvite(userRepository.notificationsInviteDecline(id.toString()), id, Notification.AcceptState.CANCELED)
    }

    private fun updateNotificationInvite(request: Completable, id: Int, newState: Notification.AcceptState) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    notifications.find { it.id == id }?.apply {
                        wasRead = true
                        acceptState = newState
                    }
                    viewState.onNotificationNeedUpdate(id)
                }
    }

    override fun onNotificationChangeDecisionClick(id: Int) {
        notifications.find { it.id == id }?.apply {
            acceptState = Notification.AcceptState.NONE
        }
        viewState.onNotificationNeedUpdate(id)
    }

    override fun onNotificationReadMoreClick(id: Int) {
        notifications.find { it.id == id }?.apply {
            viewState.showNotification(this)
        }
    }

    override fun onNotificationReadClick(id: Int) {
        compositeDisposable += readNotificationRequest(id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    notifications.find { it.id == id }?.apply {
                        wasRead = true
                    }
                    viewState.onNotificationNeedUpdate(id)
                }
    }

    override fun onNotificationRateClick(id: Int) {
        viewState.showRatingChooser(id)
    }

    override fun onNotificationRatingChosen(id: Int, rating: Int) {
        val event = notifications.find { it.id == id }?.rateId ?: return
        compositeDisposable += eventRepository.setEventRating(event, rating)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    notifications.find { it.id == id }?.apply {
                        wasRead = true
                    }
                    viewState.onNotificationNeedUpdate(id)
                }
    }

    private fun readNotificationRequest(id: Int): Completable {
        return userRepository.markNotificationsAsRead(listOf(id))
                .doOnSuccess { appData.notificationsCount -= it.countMarked }
                .flatMapCompletable { Completable.complete() }
    }
}
