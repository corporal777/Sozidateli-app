package com.example.ui.notification.center.redesign.invites

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildList
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.NotificationsSortedData
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class InviteNotificationsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BaseBottomSheetPresenter<InviteNotificationsContract.View>(appData),
    InviteNotificationsContract.Presenter {

    val notificationsList = mutableMapOf<String, MutableList<Notification>>()
    var type : NotificationType? = null

    private var unreadInvitesCount = 0
    private var groupedNotifications = mutableListOf<NotificationsSortedData>()
    private var titleDatesCount = 0

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.getUserNotifications(
            mutableMapOf<String, Any>().apply {
                put(NotificationModel.NOTIFICATION_LIMIT, limit)
                put(NotificationModel.NOTIFICATION_OFFSET, offset)
                put(NotificationModel.NOTIFICATION_USER, appData.getId())
                put(NotificationModel.NOTIFICATION_LOAD_MODEL, true)
                put(NotificationModel.NOTIFICATION_SORT, "desc")

                put(NotificationModel.NOTIFICATION_IS_INVITE, true)
                put(NotificationModel.NOTIFICATION_ACKNOWLEDGED, false)

                when(type) {
                    NotificationType.ORGANIZER -> put(NotificationModel.NOTIFICATION_TYPE, "org")
                    NotificationType.ESTIMATES -> put(NotificationModel.NOTIFICATION_TYPE, "evaluate")
                    NotificationType.EVENTS -> put(NotificationModel.NOTIFICATION_TYPE, "event")
                    NotificationType.SYSTEM -> put(NotificationModel.NOTIFICATION_TYPE, "system")
                    NotificationType.PROJECTS -> put(NotificationModel.NOTIFICATION_TYPE, "pgrf")
                    else -> {}
                }
            }
        )
            .doOnSuccess { unreadInvitesCount = it.totalCount ?: 0 }
            .map { PaginationResponse(it.totalCount, it.data) }
    }.buildList(enablePlaceholders = false, initialSize = 30)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUnreadInvitesLabel(unreadInvitesCount)
        compositeDisposable += Observable.create(pagination)
            .map { transformList(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    viewState.apply {
                        setUnreadInvitesLabel(unreadInvitesCount)
                        setNotifications(it)
                    }
                })
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
        when (notification.notificationMainType) {
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

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request
            .andThen(socket.connectToUpdates())
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    pagination.invalidate()
                    notificationManager.cancel(notificationId)
                }
            )
    }

    override fun onItemTake(position: Int) = pagination.onItemTake(position)

    private fun transformList(list : List<Notification>): ArrayList<NotificationsSortedData> {
        val notificationsList = arrayListOf<NotificationsSortedData>()
        var titleDate : String? = ""
        var wasRead = false
        list.forEach { note ->
            val noteDate = note.date?.split(" ")?.get(0)
            if (titleDate == noteDate && wasRead == note.wasRead) titleDate = ""
            else titleDate = noteDate

            notificationsList.add(NotificationsSortedData(titleDate, note))
            titleDate = noteDate
            wasRead = note.wasRead
        }
        groupedNotifications = notificationsList
        titleDatesCount = groupedNotifications.filter { x -> !x.titleDate.isNullOrEmpty() }.size
        return notificationsList
    }

    fun getTitleDatesCount(): Int = titleDatesCount
    fun getUnreadInvitesCount() = unreadInvitesCount
    override fun onNotificationUrlClick(url: String) = viewState.showUrl(url)

}