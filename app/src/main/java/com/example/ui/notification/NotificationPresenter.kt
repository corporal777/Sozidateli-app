package com.example.ui.notification

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ApproveBody
import com.example.data.bodies.CancelBody
import com.example.data.bodies.DeclineBody
import com.example.data.models.Notification
import com.example.data.models.NotificationModel
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class NotificationPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val appData: AppData,
        private val notificationManager: NotificationManager,
        private val eventRepository: EventRepository
) : BasePresenter<NotificationContract.View>(), NotificationContract.Presenter {

    lateinit var notification: Notification

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setData(notification)
        if (!notification.wasRead && notification.type != Notification.Type.RATE) {
            compositeDisposable += userRepository.markAsRead(notification.id.toString())
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        notification.wasRead = true
                    }
        }

        compositeDisposable += appData.notificationReadSubject
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    val id = it.first
                    val state = it.second
                    if (id == notification.id) {
                        notification.apply {
                            wasRead = true
                            acceptState = state
                        }
                        viewState.setData(notification)
                    }
                }
    }

    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationAcceptClick() {
        compositeDisposable += userRepository.checkUserProfile()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    val fields = it.fields?.filter { f -> f.filled == false }
                    if (fields?.isEmpty() == true) {
                        //updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id), notification.id)
                        when (notification.notificationMainType) {
                            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> approvePgrf(notification.entity?.id?:0)
                            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> approveAssistance(notification.entity?.id?:0)
                            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> approveOrgMember(notification.entity?.id?:0)
                        }
                    }
                    else {
                        val errors = mutableListOf<String>()
                        fields?.forEach { f -> errors.add("-" + f.name) }
                        viewState.showErrorDialog(errors, notification.project_name?: "")
                    }
                }, { it.printStackTrace() })

    }

    private fun approveOrgMember(id: Int) {
        updateNotificationInvite(userRepository.approveOrgMember(id.toString(), ApproveBody(appData.getId())), id)
    }

    private fun approvePgrf(id: Int) {
        updateNotificationInvite(userRepository.approvePgrf(id.toString()), id)
    }

    private fun approveAssistance(id: Int) {
        updateNotificationInvite(userRepository.approveAssistance(id.toString()), id)
    }

    override fun onNotificationCancelClick() {
        //updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id), notification.id)
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> declinePgrf(notification.id)
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> declineAssistance(notification.id)
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> declineOrgMember(notification.id)
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> cancelEvMember(notification.id)
        }
    }

    private fun declineOrgMember(id: Int) {
        updateNotificationInvite(userRepository.declineOrgMember(id.toString(), DeclineBody(appData.getId())), id)
    }

    private fun declinePgrf(id: Int) {
        updateNotificationInvite(userRepository.declinePgrf(id.toString()), id)
    }

    private fun declineAssistance(id: Int) {
        updateNotificationInvite(userRepository.declineAssistance(id.toString()), id)
    }

    private fun cancelEvMember(id: Int) {
        updateNotificationInvite(userRepository.cancelEvMember(id.toString(), CancelBody(appData.getId())), id)
    }

    override fun onNotificationChangeDecisionClick() {
        notification.acceptState = Notification.AcceptState.NONE
        viewState.setData(notification)
    }

    override fun onNotificationRateClick() {
        notification.rateId?.let { viewState.showRating(it) }
    }

    private fun updateNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { notificationManager.cancel(notificationId) }
    }
}
