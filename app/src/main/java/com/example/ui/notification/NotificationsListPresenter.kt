package com.example.ui.notification

import android.app.NotificationManager
import android.net.Uri
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava2.mapAsync
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationLocal
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.exceptions.EmptyDataException
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.flow.PagingDataSourceFactory
import com.example.util.pagination.flow.applyErrorHandler
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withDelay
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class NotificationsListPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BasePresenter<NotificationsListContract.View>(appData), NotificationsListContract.Presenter {

    private var firstLaunch = true
    private var notificationTitleDate = ""

    private val pagination = PagingDataSourceFactory { limit, offset ->
        userRepository.getNotificationsList(buildParams(limit, offset))
    }.applyErrorHandler { onReceivePagingError(it) }.buildList(initialSize = 30, distance = 5)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setNotReadButtonEnabled(it > 0)
            }

        compositeDisposable += Flowable.create(pagination, BackpressureStrategy.LATEST)
            .map { transformData(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = { viewState.setData(it) }
            )
    }

    override fun attachView(view: NotificationsListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidateStart()
    }


    override fun onNotificationAcceptClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        when (notification.entity?.type) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                updateNotification(userRepository.approvePgrf(entityId), notification.id)
            }

            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                updateNotification(userRepository.approveAssistance(entityId), notification.id)
            }

            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                updateNotification(
                    userRepository.approveOrgMember(entityId, ApproveBody(appData.getId())),
                    notification.id
                )
            }

            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                updateNotification(userRepository.approveEventMember(entityId), notification.id)
            }
        }
    }


    override fun onNotificationCancelClick(notification: Notification) {
        val entityId = notification.entity?.id.toString()
        when (notification.entity?.type) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                updateNotification(userRepository.declinePgrf(entityId), notification.id)
            }

            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                updateNotification(userRepository.declineAssistance(entityId), notification.id)
            }

            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                updateNotification(
                    userRepository.declineOrgMember(entityId, DeclineBody(appData.getId())),
                    notification.id
                )
            }

            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                updateNotification(userRepository.declineEventMember(entityId), notification.id)
            }
        }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    override fun onNotificationRateClick(eventId: String) {}

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request.andThen(socket.connectToUpdates())
            .andThen(userRepository.getNotificationDetail(notificationId.toString(), true))
            .map { NotificationLocal.fromRemoteNotification(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateNotification(null, notificationId)
                },
                onSuccess = { viewState.updateNotification(it, notificationId) }
            )
    }

    override fun onReadAllNotificationsClick() {
        compositeDisposable += userRepository.markAllNotificationsAsRead(null)
            .withDelay(3000)
            .flatMap { socket.connectToUpdates().andThen(Maybe.just(it)) }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    pagination.invalidateStart()
                    if (it.unAcceptedInvites > 0) viewState.showInvitesBottomSheet()
                }
            )
    }

    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/")) {
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment)
        } else viewState.showUrl(url)
    }

    override fun onRefreshRequest() = pagination.invalidateStart()


    private fun buildParams(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
            put(NotificationModel.NOTIFICATION_SORT, "desc")
            put(NotificationModel.NOTIFICATION_SORT_FIELD, "createdDate")
        }
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
}

data class NotificationsSortedData(
    var titleDate: String?,
    var data: Notification
) {
    override fun hashCode(): Int {
        return data.id
    }

    override fun equals(other: Any?): Boolean {
        other as NotificationsSortedData
        if (data != other.data) return false
        return true
    }
}