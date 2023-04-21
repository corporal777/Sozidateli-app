package com.example.ui.notification.center.redesign.types.organizator

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.types.base.BaseNotificationTypePresenter
import com.example.ui.notification.center.redesign.types.projects.ProjectNotificationsContract
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class OrganizerNotificationsPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager
) : BaseNotificationTypePresenter<OrganizerNotificationsContract.View>(
    appData,
    userRepository,
    notificationManager
), OrganizerNotificationsContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
            .map { it.transformList() }
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
        val id = notification.entity?.id ?: 0
        updateNotification(
            userRepository.approveOrgMember(
                id.toString(),
                ApproveBody(appData.getId())
            ), id
        )
    }

    override fun onNotificationCancelClick(notification: Notification) {
        val id = notification.entity?.id ?: 0
        updateNotification(
            userRepository.declineOrgMember(
                id.toString(),
                DeclineBody(appData.getId())
            ), id
        )
    }

    override fun onReadAllClick() {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(userRepository.markAllNotificationsAsRead(NotificationType.ORGANIZER))
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
                    if (it.unAcceptedInvites > 0) viewState.showInvitesBottomSheet(NotificationType.ORGANIZER)
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

            put(NotificationModel.NOTIFICATION_TYPE, "org")
        }
    }

}