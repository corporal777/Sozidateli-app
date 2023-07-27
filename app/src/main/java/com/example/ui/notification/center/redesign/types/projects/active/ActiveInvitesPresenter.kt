package com.example.ui.notification.center.redesign.types.projects.active

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.repository.UserRepository
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypePresenter
import com.example.ui.notification.center.redesign.types.projects.ProjectNotificationsContract
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class ActiveInvitesPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseNotificationTypePresenter<ActiveInvitesContract.View>(
    appData,
    userRepository,
    notificationManager,
    socket
), ActiveInvitesContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
            .map { transformList(it) }
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
        updateNotification(userRepository.approvePgrf(entityId), notification.id)
    }

    override fun onNotificationCancelClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        updateNotification(userRepository.declinePgrf(entityId), notification.id)
    }

    override fun onReadAllClick() {
        if (totalUnreadInvites > 0){
            viewState.showInvitesBottomSheet(NotificationType.PROJECTS)
        }
    }

    override fun buildParams(limit: Int, offset: Int): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)

            put(NotificationModel.NOTIFICATION_SORT_FIELD, "createdDate")
            put(NotificationModel.NOTIFICATION_SORT, "desc")
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)


            put(NotificationModel.NOTIFICATION_IS_INVITE, true)
            put(NotificationModel.NOTIFICATION_IS_ARCHIVE, false)
        }
    }

}