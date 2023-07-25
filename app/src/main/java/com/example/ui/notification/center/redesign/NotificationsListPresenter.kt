package com.example.ui.notification.center.redesign

import android.app.NotificationManager
import android.net.Uri
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.CancelBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildList
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.notification.center.NotificationsContract
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withDelay
import withProgressBarLoadingDialog
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.abs

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

    private var groupedNotifications = mutableListOf<NotificationsSortedData>()
    private var notifications = arrayListOf<Notification>()

    private var blockInvalidation = false
    private var isOnResume = false

    private var totalUnread = 0
    private var totalUnreadInvites = 0
    private var isHasUnreadNotifications = false
    private var titleDatesCount = 0


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
        viewState.setNotificationsPlaceholder()

        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!blockInvalidation && isOnResume) pagination.invalidate()
                else blockInvalidation = false
            }
        loadNotifications()
    }

    private fun loadNotifications() {
        compositeDisposable += Observable.create(pagination)
            .map {
                //groupData(it)
                transformData(it)
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    if (!it.isNullOrEmpty()) viewState.setData(it)
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

    override fun onNotificationRateClick(eventId: String) {
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(request)
            .andThen(socket.connectToUpdates())
            .andThen(userRepository.getNotificationDetail(notificationId.toString(), true))
            .map { Notification.fromRemoteNotification(it) }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    //pagination.invalidate()
                    notificationManager.cancel(notificationId)
                    viewState.onNotificationNeedUpdate(it)
                })
    }

    override fun onReadAllNotificationsClick() {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(userRepository.markAllNotificationsAsRead(null))
            .flatMap { socket.connectToUpdates().andThen(Maybe.just(it)) }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    pagination.invalidate()
                    if (it.unAcceptedInvites > 0) viewState.showInvitesBottomSheet()
                }
            )
    }

    fun setFragmentOnResume(onResume: Boolean) {
        this.isOnResume = onResume
    }

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


    private fun transformData(list: List<Notification>): MutableList<NotificationsSortedData> {
        val notificationsList = arrayListOf<NotificationsSortedData>()
        var titleDate : String? = ""
        var wasRead = false
        list.forEach { note ->
            val noteDate = note.date.split(" ")[0]
            if (titleDate == noteDate && wasRead == note.wasRead) titleDate = ""
            else titleDate = noteDate

            notificationsList.add(NotificationsSortedData(titleDate, note))
            titleDate = noteDate
            wasRead = note.wasRead
        }
        groupedNotifications = notificationsList
        titleDatesCount = groupedNotifications.filter { x -> !x.titleDate.isNullOrEmpty() }.size
        return groupedNotifications
    }

    fun getTitleDatesCount(): Int {
        return titleDatesCount
    }
}

data class NotificationsSortedData(
    var titleDate: String?,
    var data: Notification
) {
}