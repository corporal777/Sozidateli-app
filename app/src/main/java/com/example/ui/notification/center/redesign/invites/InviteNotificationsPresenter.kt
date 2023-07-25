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
    }.buildList(enablePlaceholders = false)

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
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> {
                declinePgrf(notification.entity?.id)
            }
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> {
                declineAssistance(notification.entity?.id)
            }
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> {
                declineOrgMember(notification.entity?.id)
            }
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> {
                declineEventMember(notification.entity?.id)
            }
        }
    }

    private fun declineOrgMember(id: Int?) {
        updateNotification(
            userRepository.declineOrgMember(
                id.toString(),
                DeclineBody(appData.getId())
            ), id ?: 0
        )
    }

    private fun declinePgrf(id: Int?) {
        updateNotification(userRepository.declinePgrf(id.toString()), id ?: 0)
    }

    private fun declineAssistance(id: Int?) {
        updateNotification(userRepository.declineAssistance(id.toString()), id ?: 0)
    }

    private fun declineEventMember(id: Int?) {
        updateNotification(userRepository.declineEventMember(id.toString()), id ?: 0)
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