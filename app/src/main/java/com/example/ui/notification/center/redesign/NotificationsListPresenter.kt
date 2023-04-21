package com.example.ui.notification.center.redesign

import android.app.NotificationManager
import android.net.Uri
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
import com.example.ui.notification.center.NotificationsContract
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class NotificationsListPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager
) : BasePresenter<NotificationsListContract.View>(appData), NotificationsListContract.Presenter {

    private var firstLaunch = true
    private var notifications: List<Notification?> = emptyList()
    private var blockInvalidation = false

    private var totalUnread = 0
    private var totalUnreadInvites = 0
    private var isHasUnreadNotifications = false

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.getUserNotifications(buildParams(limit, offset))
            .doOnSuccess {
                totalUnread = it.totalUnread ?: 0
                totalUnreadInvites = it.totalUnreadInvites ?: 0
                isHasUnreadNotifications = totalUnread > 0
            }
            .map { PaginationResponse(it.totalCount, it.data) }
    }
        .applyErrorHandler { viewState.showRequestErrorMessage() }
        .buildList(enablePlaceholders = false, initialSize = 30)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!blockInvalidation) pagination.invalidate()
            }

        compositeDisposable += appData.notificationReadSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!blockInvalidation) pagination.invalidate()
            }

        loadNotifications()
    }

    private fun loadNotifications() {
        viewState.setPlaceholder(List(20) { null })
        compositeDisposable += Observable.create(pagination)
            .map { it.groupBy { x -> x.date?.split(" ")?.get(0) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    if (!it.isNullOrEmpty()) viewState.setDataNew(it)
                    else viewState.showEmptyListPlaceholder()

                    viewState.setNotReadButtonEnabled(isHasUnreadNotifications)
                })
    }

    override fun attachView(view: NotificationsListContract.View?) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }


    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/")) {
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment)
        } else viewState.showUrl(url)
    }

    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()


    override fun onNotificationAcceptClick(notification: Notification) {
        //updateNotification(userRepository.notificationsInviteAccept(notification.id), notification.id)
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                approvePgrf(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                approveAssistance(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                approveOrgMember(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                approveEventMember(notification.entity?.id ?: 0)
            }
        }
    }

    private fun approveEventMember(id: Int) {
        updateNotification(userRepository.approveEventMember(id.toString()), id)
    }

    private fun approveOrgMember(id: Int) {
        updateNotification(
            userRepository.approveOrgMember(
                id.toString(),
                ApproveBody(appData.getId())
            ), id
        )
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
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                declinePgrf(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                declineAssistance(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                declineOrgMember(notification.entity?.id ?: 0)
            }
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                declineEventMember(notification.entity?.id ?: 0)
            }
        }
    }

    private fun declineOrgMember(id: Int) {
        updateNotification(
            userRepository.declineOrgMember(
                id.toString(),
                DeclineBody(appData.getId())
            ), id
        )
    }

    private fun declinePgrf(id: Int) {
        updateNotification(userRepository.declinePgrf(id.toString()), id)
    }

    private fun declineAssistance(id: Int) {
        updateNotification(userRepository.declineAssistance(id.toString()), id)
    }

    private fun declineEventMember(id: Int) {
        updateNotification(userRepository.declineEventMember(id.toString()), id)
    }

    private fun cancelEvMember(id: Int) {
        updateNotification(
            userRepository.cancelEventMember(
                id.toString(),
                CancelBody(appData.getId())
            ), id
        )
    }


    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    override fun onReadAllNotificationsClick() {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(userRepository.markAllNotificationsAsRead(null))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    blockInvalidation = false
                    onReceiveError(it)
                },
                onSuccess = {
                    blockInvalidation = false
                    pagination.invalidate()
                    if (it.unAcceptedInvites > 0) viewState.showInvitesBottomSheet()
                }
            )
    }

    override fun onNotificationRateClick(eventId: String) {
    }

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(request)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    blockInvalidation = false
                    onReceiveError(it)
                }, onComplete = {
                    blockInvalidation = false
                    pagination.invalidate()
                    notificationManager.cancel(notificationId)
                })
    }


    private fun buildParams(limit: Int, offset: Int): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            put(NotificationModel.NOTIFICATION_LIMIT, limit)
            put(NotificationModel.NOTIFICATION_OFFSET, offset)
            put(NotificationModel.NOTIFICATION_USER, appData.getId())
            put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
            put(NotificationModel.NOTIFICATION_SORT, "desc")
        }
    }
}