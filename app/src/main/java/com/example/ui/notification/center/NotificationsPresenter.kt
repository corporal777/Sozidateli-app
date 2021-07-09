package com.example.ui.notification.center

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.CancelBody
import com.example.data.bodies.DeclineBody
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
        userRepository.getNotificationsList(mapOf(NotificationModel.NOTIFICATION_LIMIT to limit,
                NotificationModel.NOTIFICATION_OFFSET to offset,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_LOAD_MODEL to true))
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

    override fun onNotificationAcceptClick(notification: Notification) {
        //updateNotification(userRepository.notificationsInviteAccept(notification.id), notification.id)
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> approvePgrf(notification.id)
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> approveAssistance(notification.id)
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> approveOrgMember(notification.id)
        }
    }

    private fun approveOrgMember(id: Int) {
        updateNotification(userRepository.approveOrgMember(id.toString(), ApproveBody(appData.getId())), id)
    }

    private fun approvePgrf(id: Int) {
        updateNotification(userRepository.approvePgrf(id.toString()), id)
    }

    private fun approveAssistance(id: Int) {
        updateNotification(userRepository.approveAssistance(id.toString()), id)
    }

    override fun onNotificationCancelClick(notification: Notification) {
        //updateNotification(userRepository.notificationsInviteDecline(notification.id), notification.id)
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> declinePgrf(notification.id)
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> declineAssistance(notification.id)
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> declineOrgMember(notification.id)
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> cancelEvMember(notification.id)
        }
    }

    private fun declineOrgMember(id: Int) {
        updateNotification(userRepository.declineOrgMember(id.toString(), DeclineBody(appData.getId())), id)
    }

    private fun declinePgrf(id: Int) {
        updateNotification(userRepository.declinePgrf(id.toString()), id)
    }

    private fun declineAssistance(id: Int) {
        updateNotification(userRepository.declineAssistance(id.toString()), id)
    }

    private fun cancelEvMember(id: Int) {
        updateNotification(userRepository.cancelEvMember(id.toString(), CancelBody(appData.getId())), id)
    }

    override fun onNotificationChangeDecisionClick(notification: Notification) {
        notifications.find { it?.id == notification.id }?.apply {
            acceptState = Notification.AcceptState.NONE
        }
        viewState.onNotificationNeedUpdate(notification.id)
    }

    override fun onNotificationReadMoreClick(id: Int) {
        notifications.find { it?.id == id }?.apply {
            viewState.showNotification(this)
        }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
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
                    pagination.invalidate()
                    notificationManager.cancel(notificationId)
                })
    }
}
