package com.example.ui.notification.center.redesign.types.event

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypeContract
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypePresenter
import com.example.ui.notification.center.redesign.types.projects.ProjectNotificationsContract
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class EventNotificationsPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseNotificationTypePresenter<EventNotificationsContract.View>(
    appData,
    userRepository,
    notificationManager,
    socket
), EventNotificationsContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
            .map { transformList(it)}
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    viewState.apply {
                        setReadAllButton(isHasUnreadNotifications)

                        if (it.isNullOrEmpty()) showEmptyListPlaceholder()
                        else setNotifications(it)
                    }
                })
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    override fun onNotificationAcceptClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        updateNotification(userRepository.approveEventMember(entityId), notification.id)
    }

    override fun onNotificationCancelClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        updateNotification(userRepository.declineEventMember(entityId), notification.id)
    }


    override fun onReadAllClick() {
        compositeDisposable += readAllNotificationsRequest(NotificationType.EVENTS)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    pagination.invalidate()
                    if (it.unAcceptedInvites > 0) viewState.showInvitesBottomSheet(NotificationType.EVENTS)
                }
            )
    }


    override fun buildParams(limit: Int, offset: Int): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
            put(NotificationModel.NOTIFICATION_SORT, "desc")

            put(NotificationModel.NOTIFICATION_TYPE, "event")
        }
    }

}