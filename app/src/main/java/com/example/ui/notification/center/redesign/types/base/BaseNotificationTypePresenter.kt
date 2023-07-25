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
import com.example.ui.notification.center.redesign.NotificationsSortedData
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
import kotlin.math.abs

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

    private var groupedNotifications = mutableListOf<NotificationsSortedData>()
    private var titleDatesCount = 0

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
        .buildList(enablePlaceholders = false, initialSize = 30)


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
            .andThen(userRepository.getNotificationDetail(notificationId.toString(), true))
            .map { Notification.fromRemoteNotification(it) }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    notificationManager.cancel(notificationId)
                    viewState.onNotificationNeedUpdate(it)
                })
    }


    fun readAllNotificationsRequest(notificationsType: NotificationType): Maybe<UnacceptedInviteNotification> {
        return Completable.fromAction { blockInvalidation = true }
            .andThen(userRepository.markAllNotificationsAsRead(notificationsType))
            .flatMap { socket.connectToUpdates().andThen(Maybe.just(it)) }
    }


    override fun onNotificationUrlClick(url: String) {
        if (url.contains("/organization/")) {
            viewState.showAboutOrganization(Uri.parse(url).lastPathSegment)
        } else viewState.showUrl(url)
    }

    override fun onRefreshRequest() = pagination.invalidate()
    override fun onItemTake(position: Int) = pagination.onItemTake(position)

    abstract fun buildParams(limit: Int, offset: Int): Map<String, Any>

    fun transformList(list: List<Notification>): ArrayList<NotificationsSortedData> {
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

    override fun getTitleDatesCount(): Int {
        return titleDatesCount
    }

    override fun changeScrollingElevation(value: Int) {
        viewState.setAppBarElevation(abs(value / 10f))
    }

}