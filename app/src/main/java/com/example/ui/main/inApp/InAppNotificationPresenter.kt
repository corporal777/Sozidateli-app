package com.example.ui.main.inApp

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBSPresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class InAppNotificationPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseBSPresenter<InAppNotificationContract.View>(appData), InAppNotificationContract.Presenter {

    val notificationsList = mutableListOf<Notification>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setNotifications(notificationsList)
    }


    override fun onNotificationAcceptClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                updateNotification(userRepository.approvePgrf(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                updateNotification(userRepository.approveAssistance(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                updateNotification(userRepository.approveOrgMember(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                updateNotification(userRepository.approveEventMember(entityId), notification.id)
            }
        }
    }


    override fun onNotificationCancelClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                updateNotification(userRepository.declinePgrf(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                updateNotification(userRepository.declineAssistance(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                updateNotification(userRepository.declineOrgMember(entityId), notification.id)
            }
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                updateNotification(userRepository.declineEventMember(entityId), notification.id)
            }
        }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }


    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request
            //.andThen(socket.connectToUpdates())
            .andThen(userRepository.getNotificationDetail(notificationId.toString(),true))
            .map { Notification.fromRemoteNotification(it) }
            .doOnSuccess { newNote ->
                notificationsList.apply {
                    val oldNote = find { x -> x.id == newNote.id }
                    set(indexOf(oldNote), newNote)
                }
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeBy(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.setNotifications(notificationsList)
                    notificationManager.cancel(notificationId)
                }
            )
    }

    override fun onNotificationRateClick(eventId: String) {}
    override fun onNotificationUrlClick(url: String) = viewState.showUrl(url)

}