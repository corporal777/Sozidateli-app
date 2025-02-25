package com.example.ui.notification.types.organizator

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.repository.UserRepository
import com.example.ui.notification.NotificationType
import com.example.ui.notification.types.base.BaseNotificationTypePresenter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class OrganizerNotificationsPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseNotificationTypePresenter<OrganizerNotificationsContract.View>(appData, userRepository, notificationManager, socket), OrganizerNotificationsContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setNotifications(it) }
            )

        compositeDisposable += appData.notificationsTypesSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val count = it.value?.org ?: 0
                viewState.setReadAllButton(count > 0)
            }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    override fun onNotificationAcceptClick(notification: NotificationLocal) {
        val entityId = notification.entity?.id.toString()
        updateNotification(userRepository.approveOrgMember(entityId), notification.id)
    }

    override fun onNotificationCancelClick(notification: NotificationLocal) {
        val entityId = notification.entity?.id.toString()
        updateNotification(userRepository.declineOrgMember(entityId), notification.id)
    }



    override fun buildParams(limit: Int, offset: Int): Map<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
            put(NotificationModel.NOTIFICATION_SORT, "desc")

            put(NotificationModel.NOTIFICATION_TYPE, "org")
        }
    }

}