package com.example.ui.notification.types.projects.archive

import android.app.NotificationManager
import com.example.data.AppData
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
import javax.inject.Inject

@InjectViewState
class ArchiveInvitesPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseNotificationTypePresenter<ArchiveInvitesContract.View>(
    appData,
    userRepository,
    notificationManager,
    socket
), ArchiveInvitesContract.Presenter {

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
                val count = it.value?.pgrf ?: 0
                viewState.setReadAllButton(count > 0)
            }
    }


    override fun onReadAllNotifications(type: NotificationType) {
        if (totalUnreadInvites > 0) viewState.showInvitesBottomSheet(type)
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
            put(NotificationModel.NOTIFICATION_IS_ARCHIVE, true)
        }
    }
}