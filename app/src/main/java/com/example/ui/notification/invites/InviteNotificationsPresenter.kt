package com.example.ui.notification.invites

import android.app.NotificationManager
import android.net.Uri
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.AppData
import com.example.data.models.NotificationLocal
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBSPresenter
import com.example.ui.notification.NotificationType
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class InviteNotificationsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseBSPresenter<InviteNotificationsContract.View>(appData),
    InviteNotificationsContract.Presenter {

    var type: NotificationType? = null

    private var unreadInvitesCount = 0
    private var notificationTitleDate = ""

    private val pagination = PagingDataSourceFactory { limit, offset ->
        userRepository.getUserNotifications(buildFilters(limit, offset))
            .doOnSuccess { unreadInvitesCount = it.totalCount ?: 0 }
            .map { PaginationResponse(it.totalCount, it.data) }
    }.applyErrorHandler { }.buildFlow(initialSize = 30, distance = 5)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUnreadInvitesLabel(unreadInvitesCount)
        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext = {
                    viewState.setNotifications(it)
                    viewState.setUnreadInvitesLabel(unreadInvitesCount)
                }
            )
    }


    override fun onNotificationAcceptClick(notification: NotificationLocal) {
        val entityId = notification.entity?.id.toString()
        when (notification.entity?.type) {
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

    override fun onNotificationCancelClick(notification: NotificationLocal) {
        val entityId = notification.entity?.id.toString()
        when (notification.entity?.type) {
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

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request.andThen(socket.connectToUpdates())
            .andThen(userRepository.getNotificationDetail(notificationId.toString(), true))
            .map { NotificationLocal.fromRemoteNotification(it) }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeBy(
                onError = {
                    onReceiveError(it)
                    viewState.updateNotification(null, notificationId)
                },
                onSuccess = {
                    viewState.updateNotification(null, notificationId)
                    notificationManager.cancel(notificationId)
                }
            )
    }

    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/"))
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment ?: "")
        else viewState.showUrl(url)
    }


    private fun transformData(data: PagingData<NotificationLocal>): PagingData<NotificationLocal> {
        return data.map {
            val notificationDate = it.date
            if (notificationTitleDate == notificationDate) it.titleDate = ""
            else it.titleDate = notificationDate

            notificationTitleDate = notificationDate
            it
        }
    }

    private fun buildFilters(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
            put(NotificationModel.NOTIFICATION_SORT, "desc")

            put(NotificationModel.NOTIFICATION_IS_INVITE, true)
            put(NotificationModel.NOTIFICATION_ACKNOWLEDGED, false)

            when (type) {
                NotificationType.ORGANIZER -> put(NotificationModel.NOTIFICATION_TYPE, "org")
                NotificationType.ESTIMATES -> put(NotificationModel.NOTIFICATION_TYPE, "evaluate")
                NotificationType.EVENTS -> put(NotificationModel.NOTIFICATION_TYPE, "event")
                NotificationType.SYSTEM -> put(NotificationModel.NOTIFICATION_TYPE, "system")
                NotificationType.PROJECTS -> put(NotificationModel.NOTIFICATION_TYPE, "pgrf")
                else -> {}
            }
        }
    }
}