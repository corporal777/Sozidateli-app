package com.example.ui.notification

import android.app.NotificationManager
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
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class NotificationPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager,
    private val eventRepository: EventRepository
) : BasePresenter<NotificationContract.View>(appData), NotificationContract.Presenter {

    lateinit var notification: Notification

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += userRepository.getNotificationDetail(notification.id.toString(), true)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                },
                onSuccess = {
                    notification = Notification.fromRemoteNotification(it)
                    viewState.setData(notification)
                    if (!notification.wasRead && notification.type != Notification.Type.RATE) {
                        compositeDisposable += userRepository.markAsRead(notification.id.toString())
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
            )
    }


    override fun onNotificationUrlClick(url: String) {
        viewState.showUrl(url)
    }

    override fun onNotificationAcceptClick() {
        compositeDisposable += userRepository.checkUserProfile()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                }, onSuccess = {
                    val fields = it.fields?.filter { f -> f.filled == false }
                    if (fields?.isEmpty() == true) {
                        //updateNotificationInvite(userRepository.notificationsInviteAccept(notification.id), notification.id)
                        when (notification.notificationMainType) {
                            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> approvePgrf(
                                notification.entity?.id ?: 0
                            )
                            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> approveAssistance(
                                notification.entity?.id ?: 0
                            )
                            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> approveOrgMember(
                                notification.entity?.id ?: 0
                            )
                        }
                        //viewState.showSuccessAccepted()
                    } else {
                        val errors = mutableListOf<String>()
                        fields?.forEach { f -> errors.add("-" + f.name) }
                        viewState.showErrorDialog(errors, notification.project_name ?: "")
                    }
                })

    }

    override fun onNotificationCancelClick() {
        //updateNotificationInvite(userRepository.notificationsInviteDecline(notification.id), notification.id)

        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> declinePgrf(
                notification.entity?.id ?: 0
            )
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> declineAssistance(
                notification.entity?.id ?: 0
            )
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> declineOrgMember(
                notification.entity?.id ?: 0
            )
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> cancelEvMember(
                notification.entity?.id ?: 0
            )

////            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> declinePgrf(notification.id)
////            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> declineAssistance(notification.id)
////            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> declineOrgMember(notification.id)
////            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> cancelEvMember(notification.id)
        }
        //viewState.showSuccessCanceled()
    }

    private fun approveOrgMember(id: Int) {
        //updateNotificationInvite(
        acceptNotificationInvite(
            userRepository.approveOrgMember(
                id.toString(),
                ApproveBody(appData.getId())
            ), id
        )
    }

    private fun approvePgrf(id: Int) {
        //updateNotificationInvite(userRepository.approvePgrf(id.toString()), id)
        acceptNotificationInvite(userRepository.approvePgrf(id.toString()), id)
    }

    private fun approveAssistance(id: Int) {
        acceptNotificationInvite(userRepository.approveAssistance(id.toString()), id)
        //updateNotificationInvite(userRepository.approveAssistance(id.toString()), id)
    }


    private fun declineOrgMember(id: Int) {
        //updateNotificationInvite(
        cancelNotificationInvite(
            userRepository.declineOrgMember(
                id.toString(),
                DeclineBody(appData.getId())
            ), id
        )
    }

    private fun declinePgrf(id: Int) {
        //updateNotificationInvite(userRepository.declinePgrf(id.toString()), id)
        cancelNotificationInvite(userRepository.declinePgrf(id.toString()), id)
    }

    private fun declineAssistance(id: Int) {
        cancelNotificationInvite(userRepository.declineAssistance(id.toString()), id)
        //updateNotificationInvite(userRepository.declineAssistance(id.toString()), id)
    }

    private fun cancelEvMember(id: Int) {
       // updateNotificationInvite(
        cancelNotificationInvite(
            userRepository.cancelEventMember(
                id.toString(),
                CancelBody(appData.getId())
            ), id
        )
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
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple { notificationManager.cancel(notificationId) }
    }

    private fun acceptNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                },
                onComplete = {
                    viewState.showSuccessAccepted()
                    notificationManager.cancel(notificationId)
                })
    }

    private fun cancelNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                },
                onComplete = {
                    viewState.showSuccessCanceled()
                    notificationManager.cancel(notificationId)
                })
    }
}
