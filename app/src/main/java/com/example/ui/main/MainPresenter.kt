package com.example.ui.main

import android.app.NotificationManager
import android.net.Uri
import android.util.Log
import call
import com.example.app.BuildConfig
import com.example.data.AppData
import com.example.data.models.Notification
import com.example.data.models.Optional
import com.example.data.models.RemoteNotification
import com.example.data.socket.SocketConnectionState
import com.example.data.socket.SocketIOManager
import com.example.exceptions.InvalidTokenException
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.ChatHelper
import com.example.util.ConnectivityProvider
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withDelay
import java.util.UUID
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
            .flatMap { if (!isIgnoreToken) Observable.just(it) else Observable.empty() }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { token ->
                disconnectFromSocket()
                getAdditionalData()
                if (token.value.isNullOrEmpty()) {
                    isAuthRequired = true
                    viewState.hideSplashScreen()
                    viewState.showRecommendations()
                    viewState.checkIntent()
                } else loadUser()
            }
    }


    private fun loadUser() {
        compositeDisposable += checkUserTokenIsValid()
            .andThen(userRepository.getUserShortData())
            .flatMapSingle { userRepository.checkUserProfileSingle() }
            .flatMapCompletable { authRepository.sendFcmToken() }
            .doOnComplete { connectToSocket() }
            .andThen(Completable.defer { checkShowGreetings() })
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    isAuthRequired = true
                    hideLoadings()
                    viewState.showRecommendations()
                },
                onComplete = {
                    hideLoadings()
                    getInAppNotifications()
                    viewState.showRecommendations()
                    viewState.checkIntent()
                }
            )
    }

    fun connectToSocket() {
//        chatCompositeDisposable += socket.connect()
//            .performOnBackgroundOutOnMain()
//            .subscribeSimple(
//                onError = { it.printStackTrace() },
//                onNext = {
//                    val connected = it == SocketConnectionState.CONNECTED
//                    if (connected && chatCompositeDisposable.size() == 1) {
//                        subscribeToNotifications()
//                        subscribeChatUnreadCount()
//                        subscribeChatRequestsCount()
//                        updateSocketEmitValues()
//                    }
//                })
    }

    private fun updateSocketEmitValues() {
//        chatCompositeDisposable += socket.connectToUpdates()
//            .performOnBackgroundOutOnMain()
//            .subscribe()
    }

    private fun subscribeToNotifications() {
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
            .subscribeSimple(
                onError = { appData.chatUnreadMessageCount = 0 },
                onNext = { appData.chatUnreadMessageCount = it }
            )
    }


    private fun subscribeChatRequestsCount() {
        chatCompositeDisposable += Flowable.create<Int>({ emitter ->
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


    private fun disconnectFromSocket() {
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
        return if (isAuthRequired || appData.isNeedShowWelcome) {
            isAuthRequired = false
            appData.isNeedShowWelcome = false
            Completable.fromAction {
                viewState.apply {
                    hideAllLoadingDialogs()
                    showGreetings()
                }
            }.subscribeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
        } else Completable.complete()
    }


    override fun onHandleChat(chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else viewState.apply {
            showChat(chatId, userName)
            clearIntentData()
        }
    }

    override fun onHandleEventCode(event: String?) {
        if (event.isNullOrEmpty()) return
        else compositeDisposable += eventRepository.getEventByCode(event)
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.showAboutEvent(it.id.toString())
                viewState.clearIntentData()
            }, { it.printStackTrace() })

    }

    override fun onHandleEvent(event: String?) {
        if (event.isNullOrEmpty()) return
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

    override fun onHandleAuthWebsite(code: String?, socketId: String?) {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else if (code.isNullOrBlank()) return
        else viewState.apply {
            showAuthWebsiteFragment(code, socketId ?: "")
            clearIntentData()
        }
    }

    override fun onHandleSupportQuestion(id: String?) {
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

    override fun onHandleProfileSettings() {
        if (isAuthRequired || appData.isLoggedOut) viewState.showLogin()
        else viewState.apply {
            showProfileSettings()
            clearIntentData()
        }
    }

    override fun onHandleProfile() {
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

    override fun onHandleChangePassword(userId: String, code: String) {
        authRepository.checkPasswordRecoveryCode("email", code)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    showChangePassword(userId, code)
                    clearIntentData()
                }
            }.call(compositeDisposable)
    }

    override fun onHandleRecoverPassword() {
        viewState.apply {
            showPasswordRecovery()
            clearIntentData()
        }
    }


    override fun onHandleNotification(notification: RemoteNotification) {
        if (isAuthRequired) return
        viewState.clearIntentData()
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectFromSocket()
        chatHelper.currentChatId = null
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

    override fun onRequestShowErrorMessage() {
        errorMessageDisposable += Completable.complete()
            .withDelay(2000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.hideErrorMessage() },
                onComplete = { viewState.hideErrorMessage() }
            )
    }

    fun ignoreTokenListener(isIgnore: Boolean) {
        isIgnoreToken = isIgnore
    }


    fun startUpdateTimer(time: Long?, isRequired: Boolean) {
        var counter = time ?: 0
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1, TimeUnit.MINUTES)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                counter += 1
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

    private fun checkUserTokenIsValid(): Completable {
        return authRepository.checkUserAuth().onErrorResumeNext {
            if (it is HttpException && it.code() == 400) {
                appData.logoutInvalidation()
                Completable.error(InvalidTokenException())
            }
            else Completable.error(it)
        }
    }

    private fun getAdditionalData() {
//        compositeDisposable += userRepository.getUserProfileAdditionalData()
//            .subscribeSimple {}
    }

    private fun getInAppNotifications() {
        compositeDisposable += userRepository.getInAppList()
            .map { it.map { n -> Notification.fromRemoteNotification(n) } }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!it.isNullOrEmpty()) viewState.showInAppNew(it)
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

    private fun hideLoadings() {
        viewState.apply {
            hideSplashScreen()
            hideAllLoadingDialogs()
        }
    }


    companion object {
        private const val EVENT_AREA_DISTANCE = 500
    }
}
