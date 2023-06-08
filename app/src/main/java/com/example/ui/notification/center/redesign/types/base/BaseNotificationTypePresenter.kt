package com.example.ui.notification.center.redesign.types.base

import android.app.NotificationManager
import android.net.Uri
import android.util.Log
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.UnacceptedInviteNotification
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildList
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.notification.center.redesign.NotificationType
import com.example.ui.notification.center.redesign.NotificationsListContract
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.observable.PaginationDataSourceFactory
import com.example.util.pagination.observable.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog

abstract class BaseNotificationTypePresenter<V : BaseNotificationTypeContract.View>(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), BaseNotificationTypeContract.Presenter {

    private var firstLaunch = true
    var totalUnread = 0
    var totalUnreadInvites = 0
    var isHasUnreadNotifications = false
    var blockInvalidation = false

    protected val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.getUserNotifications(buildParams(limit, offset))
            .doOnSuccess {
                totalUnread = it.totalUnread ?: 0
                totalUnreadInvites = it.totalUnreadInvites ?: 0
                isHasUnreadNotifications = totalUnread > 0
            }
            .map { PaginationResponse(it.totalCount, it.data) }
    }
        .applyErrorHandler { viewState.showRequestErrorMessage() }
        .buildList(enablePlaceholders = false)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder(List(20) { null })
        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (blockInvalidation) blockInvalidation = false
                else pagination.invalidate()
            }
    }


    override fun attachView(view: V) {
        super.attachView(view)
        if (firstLaunch) firstLaunch = false
        else pagination.invalidate()
    }

    fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += Completable.fromAction { blockInvalidation = true }
            .andThen(request)
            .andThen(socket.connectToUpdates())
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    pagination.invalidate()
                    notificationManager.cancel(notificationId)
                })
    }


    fun readAllNotificationsRequest(notificationsType: NotificationType): Maybe<UnacceptedInviteNotification> {
        return Completable.fromAction { blockInvalidation = true }
            .andThen(userRepository.markAllNotificationsAsRead(notificationsType))
            .flatMap {
                socket.connectToUpdates()
                    .andThen(Maybe.just(it))
            }
    }


    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/")) {
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment)
        } else viewState.showUrl(url)
    }

    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)

    abstract fun buildParams(limit: Int, offset: Int): Map<String, Any>

    fun List<Notification>.transformList(): Map<String, List<Notification>> {
        return groupBy { x -> x.date?.split(" ")?.get(0) }
    }

}