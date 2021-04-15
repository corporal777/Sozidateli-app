package com.example.ui.notification.center

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
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
        private val appData: AppData,
        private val notificationManager: NotificationManager
) : BasePresenter<NotificationsContract.View>(), NotificationsContract.Presenter {

    private var firstLaunch = true
    private var notifications: List<Notification?> = emptyList()
    private var blockInvalidation = false

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        /*userRepository.getNotifications(mapOf(NotificationModel.NOTIFICATION_LIMIT to limit,
                NotificationModel.NOTIFICATION_OFFSET to offset)).map { response ->
            PaginationResponse(response.totalCount, response.data.map {
                Notification.fromRemoteNotification(it)
            })
        }*/
        userRepository.getNotifications(limit, offset).map { response ->
            PaginationResponse(response.totalCount, response.data.map {
                Notification.fromRemoteNotification(it)
            })
        }
    }
            .applyErrorHandler { viewState.showRequestErrorMessage() }
            .buildList(enablePlaceholders = true)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribeSimple { if (!blockInvalidation) pagination.invalidate() }

        compositeDisposable += appData.notificationReadSubject
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    val id = it.first
                    val state = it.second
                    notifications.find { notification -> notification?.id == id }?.apply {
                        wasRead = true
                        acceptState = state
                        viewState.onNotificationNeedUpdate(id)
                    }
                }

        viewState.setData(List(20) { null })
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

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onRefreshRequest() {
        pagination.invalidate()
    }

    override fun onNotificationAcceptClick(id: Int) {
        updateNotification(userRepository.notificationsInviteAccept(id), id)
    }

    override fun onNotificationCancelClick(id: Int) {
        updateNotification(userRepository.notificationsInviteDecline(id), id)
    }

    override fun onNotificationChangeDecisionClick(id: Int) {
        notifications.find { it?.id == id }?.apply {
            acceptState = Notification.AcceptState.NONE
        }
        viewState.onNotificationNeedUpdate(id)
    }

    override fun onNotificationReadMoreClick(id: Int) {
        notifications.find { it?.id == id }?.apply {
            viewState.showNotification(this)
        }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markNotificationsAsRead(listOf(id)), id)
    }

    override fun onNotificationRateClick(eventId: String) {
        viewState.showRating(eventId)
    }

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
                .andThen(request)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(onError = {
                    blockInvalidation = false
                    onReceiveError(it)
                }, onComplete = {
                    blockInvalidation = false
                    notificationManager.cancel(notificationId)
                })
    }
}
