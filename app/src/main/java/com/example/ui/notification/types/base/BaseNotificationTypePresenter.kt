package com.example.ui.notification.types.base

import android.app.NotificationManager
import android.net.Uri
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.AppData
import com.example.data.models.NotificationLocal
import com.example.data.socket.SocketIOManager
import com.example.extensions.buildFlow
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.notification.NotificationType
import com.example.util.pagination.PaginationResponse
import com.example.util.paginationNew.PagingDataSourceFactory
import com.example.util.paginationNew.applyErrorHandler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomLoading
import kotlin.math.abs

abstract class BaseNotificationTypePresenter<V : BaseNotificationTypeContract.View>(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val notificationManager: NotificationManager,
    private val socket: SocketIOManager,
) : BasePresenter<V>(appData), BaseNotificationTypeContract.Presenter {

    var totalUnread = 0
    var totalUnreadInvites = 0
    var isHasUnreadNotifications = false


    private var notificationTitleDate = ""

    protected val pagination = PagingDataSourceFactory { limit, offset ->
        userRepository.getUserNotifications(buildParams(limit, offset))
            .doOnSuccess {
                totalUnread = it.totalUnread ?: 0
                totalUnreadInvites = it.totalUnreadInvites ?: 0
                isHasUnreadNotifications = totalUnread > 0
            }.map { PaginationResponse(it.totalCount, it.data) }

    }.applyErrorHandler { onReceivePagingError(it) }.buildFlow(initialSize = 30, distance = 5)


    protected fun updateNotification(request: Completable, notificationId: Int) {
        compositeDisposable += request
            //.andThen(socket.connectToUpdates())
            .andThen(userRepository.getNotificationDetail(notificationId.toString(), true))
            .map { NotificationLocal.fromRemoteNotification(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.updateNotification(notificationId)
                },
                onSuccess = { viewState.updateNotification(it) }
            )
    }


    override fun onReadAllNotifications(type: NotificationType) {
        compositeDisposable += userRepository.markAllNotificationsAsRead(type)
            .flatMap { Maybe.just(it) }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    pagination.invalidateStart()
                    if (type != NotificationType.SYSTEM && it.unAcceptedInvites > 0)
                        viewState.showInvitesBottomSheet(type)
                }
            )
    }


    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/")) {
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment)
        } else viewState.showBrowser(url)
    }


    override fun onRefreshRequest() {
        notificationTitleDate = ""
        pagination.invalidate()
    }

    override fun changeScrollingElevation(value: Int) = viewState.setAppBarElevation(abs(value / 10f))

    abstract fun buildParams(limit: Int, offset: Int): Map<String, Any>

    fun transformData(data: PagingData<NotificationLocal>): PagingData<NotificationLocal> {
        return data.map {
            val notificationDate = it.date
            if (notificationTitleDate == notificationDate) it.titleDate = ""
            else it.titleDate = notificationDate

            notificationTitleDate = notificationDate
            it
        }
    }
}