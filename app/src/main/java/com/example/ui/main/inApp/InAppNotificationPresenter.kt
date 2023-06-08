package com.example.ui.main.inApp

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class InAppNotificationPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager
) : BaseBottomSheetPresenter<InAppNotificationContract.View>(appData),
    InAppNotificationContract.Presenter {

    val notificationsList = mutableListOf<Notification>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setNotifications(notificationsList)
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


    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }


    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request
            .andThen(userRepository.getNotificationDetail(notificationId.toString(),true))
            .map { Notification.fromRemoteNotification(it) }
            .doOnSuccess { newNote ->
                notificationsList.apply {
                    val oldNote = find { x -> x.id == newNote.id }
                    set(indexOf(oldNote), newNote)
                }
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.setNotifications(notificationsList)
                    notificationManager.cancel(notificationId)
                }
            )
    }

    override fun onNotificationRateClick(eventId: String) {
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

}