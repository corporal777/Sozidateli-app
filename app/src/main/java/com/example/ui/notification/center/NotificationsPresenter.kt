package com.example.ui.notification.center

import android.app.NotificationManager
import android.util.Log
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
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class NotificationsPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val appData: AppData,
    private val notificationManager: NotificationManager
) : BasePresenter<NotificationsContract.View>(appData), NotificationsContract.Presenter {

    private var firstLaunch = true
    private var notifications: List<Notification?> = emptyList()
    private var blockInvalidation = false
    private var mDy = 0f

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.getNotificationsList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to limit,
                NotificationModel.NOTIFICATION_OFFSET to offset,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_LOAD_MODEL to true,
                NotificationModel.NOTIFICATION_SORT to "desc"
            )
        )
    }
        .applyErrorHandler { viewState.showRequestErrorMessage() }
        .buildList(enablePlaceholders = true)
    //.buildListNew(enablePlaceholders = true)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setAppBarElevation(mDy)
            setNotificationsList()
            setData(List(20) { null })
        }

        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!blockInvalidation) {
                    pagination.invalidate()
                }
            }

        compositeDisposable += appData.notificationReadSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val id = it.first
                val state = it.second
                notifications.find { notification -> notification?.id == id }?.apply {
                    wasRead = true
                    acceptState = state
                    viewState.onNotificationNeedUpdate(id)
                }
            }

        compositeDisposable += Observable.create(pagination)
            //compositeDisposable += Flowable.create(pagination, BackpressureStrategy.BUFFER)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onNext = {
                    notifications = it
                    if (!it.isNullOrEmpty()) viewState.setData(it)
                    else viewState.showEmptyListPlaceholder()
                })
    }

    override fun attachView(view: NotificationsContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onNotificationUrlClick(url: String) = viewState.showUrl(url)
    override fun onItemTake(position: Int) = pagination.onItemTake(position)
    override fun onRefreshRequest() = pagination.invalidate()


    override fun onNotificationAcceptClick(notification: Notification) {
        //updateNotification(userRepository.notificationsInviteAccept(notification.id), notification.id)
        when (notification.notificationMainType) {
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> approvePgrf(notification.id)
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> approveAssistance(notification.id)
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> approveOrgMember(notification.id)
        }
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
            NotificationModel.NOTIFICATION_TYPE_INVITE_PGFR -> declinePgrf(notification.id)
            NotificationModel.NOTIFICATION_TYPE_INVITE_ASSISTANCE -> declineAssistance(notification.id)
            NotificationModel.NOTIFICATION_TYPE_ORGANIZATION_MEMBER -> declineOrgMember(notification.id)
            NotificationModel.NOTIFICATION_TYPE_EVENT_MEMBER -> cancelEvMember(notification.id)
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

    private fun cancelEvMember(id: Int) {
        updateNotification(
            userRepository.cancelEvMember(
                id.toString(),
                CancelBody(appData.getId())
            ), id
        )
    }

    override fun onNotificationChangeDecisionClick(notification: Notification) {
        notifications.find { it?.id == notification.id }?.apply {
            acceptState = Notification.AcceptState.NONE
        }
        viewState.onNotificationNeedUpdate(notification.id)
    }

    override fun onNotificationReadMoreClick(id: Int) {
        notifications.find { it?.id == id }?.apply {
            viewState.showNotification(this)
        }
    }

    override fun onNotificationReadClick(id: Int) {
        updateNotification(userRepository.markAsRead(id.toString()), id)
    }

    override fun onNotificationRateClick(eventId: String) {
        viewState.showRating(eventId)
    }

    private fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(request)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(onError = {
                blockInvalidation = false
                onReceiveError(it)
            }, onComplete = {
                blockInvalidation = false
                pagination.invalidate()
                notificationManager.cancel(notificationId)
            })
    }
}
