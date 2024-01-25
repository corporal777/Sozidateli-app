package com.example.ui.main

import android.app.NotificationManager
import android.net.Uri
import android.util.Log
import call
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.Notification
import com.example.data.socket.SocketConnectionState
import com.example.data.socket.SocketIOManager
import com.example.repository.*
import com.example.ui.base.BasePresenter
import com.example.util.ChatHelper
import com.example.util.ConnectivityProvider
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class MainPresenter
@Inject constructor(
    private val chatHelper: ChatHelper,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val chatRepository: ChatRepository,
    private val locationProviderClient: FusedLocationProviderClient,
    private val notificationManager: NotificationManager,
    private val connectivityProvider: ConnectivityProvider,
    private val eventRepository: EventRepository,
    private val commonRepository: CommonRepository,
    private val socket: SocketIOManager,
) : BasePresenter<MainContract.View>(appData), MainContract.Presenter {

    private var isIgnoreToken = false

    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }
    private val chatCompositeDisposable = CompositeDisposable()
    private val errorMessageDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    var isFinishRegister = false
    private var isAuthRequired = false
    private var canShowBrowser = false
    private var inAppList: Deque<NotificationModel>? = null
    private val inAppListNew = arrayListOf<Notification>()

    private var isDoNotCheckConnectionFragmentOpened = false
    private var isInternetConnected = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.deviceId = UUID.randomUUID().toString()
        if (!appData.isStoriesShown) {
            viewState.hideSplashScreen()
            viewState.showStories()
        } else onStoriesComplete()

        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe(
                { viewState.showBadgeNotification(it) },
                { viewState.showBadgeNotification(0) }
            )

        compositeDisposable += appData.chatMessageCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe(
                { viewState.showBadgeChat(it) },
                { viewState.showBadgeChat(0) }
            )
    }

    override fun onStoriesComplete() {
        initInternetConnectionCheck()
        subscribeToTokenUpdates()
        checkAppUpdate()
    }

    private fun checkAppUpdate() {
        compositeDisposable += authRepository.checkAppUpdate(BuildConfig.VERSION_NAME)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                appData.isNeedUpdateApp = it.hasUpdate()
                if (it.hasUpdate()) {
                    if (it.isUpdateRequired()) viewState.showUpdateApp(it.isUpdateRequired())
                    else {
                        if (appData.isTimeToUpdate()) {
                            appData.updateTime = System.currentTimeMillis()
                            viewState.showUpdateApp(it.isUpdateRequired())
                        } else startUpdateTimer(appData.getUpdateMinutes(), it.isUpdateRequired())
                    }
                }
            }
    }

    private fun subscribeToTokenUpdates() {
        compositeDisposable += appData.tokenChangeSubject
            .flatMap {
                if (!isIgnoreToken) Observable.just(it)
                else Observable.empty()
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { token ->
                unsubscribeChat()
                if (token.value == null) {
                    isAuthRequired = true
                    viewState.apply {
                        hideSplashScreen()
                        showLogin()
                        checkIntent()
                    }
                } else loadUser()
            }
    }

    private fun loadUser() {
        compositeDisposable += userRepository.getUserShortData()
            .flatMapSingle { userRepository.checkUserProfileSingle() }
            .flatMapCompletable { getAdditionalData() }
            .doOnComplete { connectToSocket() }
            .andThen(Completable.defer { checkShowGreetings() })
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    isAuthRequired = true
                    viewState.apply {
                        hideSplashScreen()
                        hideLoadingDialog()
                        showLogin()
                        checkIntent()
                    }
                },
                onComplete = {
                    showNextInApp()
                    viewState.apply {
                        hideSplashScreen()
                        hideLoadingDialog()
                        showRecommendations()
                        checkIntent()
                    }
                }
            )
    }

    fun connectToSocket() {
        chatCompositeDisposable += socket.connect()
            .performOnBackgroundOutOnMain()
            .subscribe({
                val connected = it == SocketConnectionState.CONNECTED
                chatHelper.isConnectingToSocket = connected
                if (connected) {
                    if (chatCompositeDisposable.size() == 1) {
                        subscribeChatNewMessage()
                        subscribeToNotifications()
                        subscribeChatUnreadCount()
                        subscribeChatRequestsCount()
                        updateEmitValues()
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun updateEmitValues() {
        compositeDisposable += socket.connectToUpdates()
            .performOnBackgroundOutOnMain()
            .subscribe()
    }

    private fun subscribeToNotifications() {
        chatCompositeDisposable += socket.subscribeToTotalNotificationsCount()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    appData.notificationsCount = 0
                },
                onNext = { nCount ->
                    Log.e("TOTAL NOTES COUNT", nCount.toString())
                    appData.notificationsCount = nCount
                }
            )
        chatCompositeDisposable += socket.subscribeTotalNotificationsTypesCount()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    Log.e("TOTAL TYPES COUNT", it.toString())
                    appData.setNotificationsTypes(it)
                }
            )
        chatCompositeDisposable += socket.subscribeNotificationsInvitesCount()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onNext = {
                    Log.e("TOTAL INVITES COUNT", it.toString())
                    appData.setNotificationsInvites(it)
                })
    }

    private fun subscribeChatUnreadCount() {
        chatCompositeDisposable += socket.subscribeToTotalMessagesCount()
            .performOnBackgroundOutOnMain()
            .subscribe({
                Log.e("TOTAL MESSAGES COUNT", it.toString())
                appData.chatUnreadMessageCount = it
            }, {
                it.printStackTrace()
                appData.chatUnreadMessageCount = 0
            })
    }

    private fun subscribeChatNewMessage() {
        chatCompositeDisposable += socket.subscribeNewChatMessage()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onNext = {
                    Log.e("NEW CHAT MESSAGE", it.data.toString())
                    it.data.lastOrNull().let { message ->
                        chatHelper.showNotificationIfCan(
                            message?.chat.toString(),
                            message?.id.toString(),
                            message?.sender?.name + " " + message?.sender?.lastName,
                            message?.message ?: "",
                            "",
                            message?.sender?.avatar
                        )
                    }
                    appData.setNewChatMessage(it.data.lastOrNull())
                })
    }

    private fun subscribeChatRequestsCount() {
        compositeDisposable += Flowable.create<Int>({ emitter ->
            val disposables = CompositeDisposable()
            disposables += chatRepository.getChatInvitesCount()
                .subscribe({ emitter.onNext(it.count) }, { emitter.onError(it) })
            disposables += socket.subscribeToInvitesCount()
                .subscribe({ emitter.onNext(it) }, { emitter.onError(it) })
            emitter.setDisposable(disposables)
        }, BackpressureStrategy.LATEST)
            .performOnBackgroundOutOnMain()
            .subscribe({
                Log.e("CHAT REQUEST COUNT", it.toString())
                appData.chatRequestsCount = it
            }, {
                appData.chatRequestsCount = 0
            })
    }


    private fun unsubscribeChat() {
        socket.disconnectFromSocket()
        chatCompositeDisposable.clear()
    }

    private fun initInternetConnectionCheck() {
        compositeDisposable += connectivityProvider.observeNetworkConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribe({
                isInternetConnected = it
                checkInternetConnection()
            }, {
                it.printStackTrace()
            })
    }

    private fun checkShowGreetings(): Completable {
        return if (isAuthRequired) {
            isAuthRequired = false
            Completable.fromAction {
                viewState.apply {
                    hideAllLoadingDialogs()
                    showGreetings()
                }
            }
                .subscribeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
        } else Completable.complete()
    }


    private fun showNextInApp() {
        compositeDisposable += userRepository.getInAppList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 50,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_IS_IN_APP to true,
                NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
            )
        )
            .map { it.map { n -> Notification.fromRemoteNotification(n) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!it.isNullOrEmpty()) viewState.showInAppNew(it)
            }
    }

    override fun onHandleChat(chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else viewState.apply {
            showChat(chatId, userName)
            clearIntentData()
        }
    }

    override fun onHandleEventCode(event: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (event.isNullOrEmpty()) return
        else {
            compositeDisposable += eventRepository.getEventsList(
                mapOf(
                    EventNew.EVENT_LIMIT to 1,
                    EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_CODE to event
                )
            ).map { it.data }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (!it.isNullOrEmpty()) viewState.showAboutEvent(it.first()?.id.toString())
                    viewState.clearIntentData()
                }, { it.printStackTrace() })
        }

    }

    override fun onHandleEvent(event: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (event.isNullOrEmpty()) return
        else viewState.apply {
            showAboutEvent(event)
            clearIntentData()
        }
    }

    override fun onHandleUser(userId: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (userId.isNullOrEmpty()) return
        else viewState.apply {
            showUser(userId)
            clearIntentData()
        }
    }

    override fun onHandleAuthToOtherPlatform(url: String?) {
        if (isAuthRequired || appData.isLoggedOut) {
            viewState.showLogin()
            canShowBrowser = true
        } else if (url.isNullOrBlank()) return
        else {
            if (canShowBrowser) observeDeeplink(url)
            else viewState.showAccountChangeFragment(url)
            viewState.clearIntentData()
        }
    }

    override fun onHandleAuthWebsite(code: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (code.isNullOrBlank()) return
        else viewState.apply {
            showAuthWebsiteFragment(code)
            clearIntentData()
        }
    }

    override fun onHandleSupportQuestionLink(id: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (id.isNullOrEmpty()) return
        else commonRepository.getSupportQuestion(id)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    showSupportQuestion(it)
                    clearIntentData()
                }
            }.call(compositeDisposable)
    }

    override fun onHandleProfileSettingsLink() {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else viewState.apply {
            showProfileSettings()
            clearIntentData()
        }
    }

    override fun onHandleProfileLink() {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else viewState.apply {
            showCurrentUser()
            clearIntentData()
        }
    }

    override fun onInviteRegister(
        email: String,
        code: String,
        name: String,
        lastName: String,
        middleName: String,
        invite: Int
    ) {
        viewState.apply {
            showInviteRegister(email, code, name, lastName, middleName, invite)
            clearIntentData()
        }
    }

    override fun onHandleChangePasswordLink(userId: String, code: String) {
        authRepository.checkPasswordRecoveryCode("email", code)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    showChangePassword(userId, code)
                    clearIntentData()
                }
            }.call(compositeDisposable)
    }

    override fun onHandleRecoverPasswordLink() {
        viewState.apply {
            showPasswordRecovery()
            clearIntentData()
        }
    }


    override fun onHandleSocialNetworkConfirm(userId: String, code: String) {
    }

    override fun onHandleNotification(notification: RemoteNotification) {
        if (isAuthRequired) return
        viewState.clearIntentData()
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeChat()
        chatHelper.currentChatId = null
    }

    override fun onOpenStartDestination() {
        chatHelper.currentChatId = null
    }

    override fun onOpenNotStartDestination() {
        chatHelper.currentChatId = null
    }

    override fun onOpenChatDestination(chatId: String?) {
        chatHelper.currentChatId = chatId
    }

    override fun onOpenCheckConnectionDestination(check: Boolean) {
        isDoNotCheckConnectionFragmentOpened = check
        checkInternetConnection()
    }

    override fun onRetryConnectionClick() {
        compositeDisposable += connectivityProvider.checkInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribe({
                isInternetConnected = it
                checkInternetConnection()
            }, {
                it.printStackTrace()
            })
    }

    private fun checkInternetConnection() {
        viewState.showNoConnectionMessage(!isInternetConnected && !isDoNotCheckConnectionFragmentOpened)
    }

    override fun onRequestShowErrorMessage(message: String) {
        errorMessageDisposable.clear()
        errorMessageDisposable += Completable.fromAction { viewState.showErrorMessage(message) }
            .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.hideErrorMessage()
            }, {
                viewState.hideErrorMessage()
            })
    }

    override fun onRequestHideErrorMessage() {
        errorMessageDisposable.clear()
        viewState.hideErrorMessage()
    }

    fun ignoreTokenListener(isIgnore: Boolean) {
        isIgnoreToken = isIgnore
    }

    override fun onBackClick() = viewState.navigateUp()

    fun startUpdateTimer(time: Long?, isRequired: Boolean) {
        var counter = time ?: 0
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1, TimeUnit.MINUTES)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                counter += 1
                Log.e("UPDATE APP TIME", counter.toString())
                if (BuildConfig.DEBUG) {
                    if (counter >= 10) {
                        appData.updateTime = System.currentTimeMillis()
                        viewState.showUpdateApp(isRequired)
                        timerCompositeDisposable.clear()
                    }
                } else {
                    if (counter >= 2879) {
                        appData.updateTime = System.currentTimeMillis()
                        viewState.showUpdateApp(isRequired)
                        timerCompositeDisposable.clear()
                    }
                }
            }
    }

    private fun observeDeeplink(url: String) {
        val uri = Uri.parse(url)
            .buildUpon()
            .appendQueryParameter("new_session", "true")
            .appendQueryParameter("access_token", appData.token)
            .build()
        viewState.showBrowser(uri.toString())
    }

    private fun getAdditionalData(): Completable {
        return Single.merge(
            userRepository.getEducationLevel(),
            userRepository.getSpeciality(),
            userRepository.getAcademicDegrees()
        ).ignoreElements()
    }

    fun changeScrollingOffset(value: Int) = viewState.setAppBarElevation(abs(value / 10f))


    companion object {
        private const val EVENT_AREA_DISTANCE = 500
    }
}
