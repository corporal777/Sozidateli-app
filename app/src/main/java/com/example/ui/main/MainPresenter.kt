package com.example.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log
import call
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.models.*
import com.example.data.models.Notification
import com.example.data.socket.SocketConnectionState
import com.example.data.socket.SocketIOManager
import com.example.events.OnSocketConnectEvent
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.accountChange.data.AuthType
import com.example.ui.auth.register.email.finish.FinishRegisterPresenter
import com.example.ui.base.BasePresenter
import com.example.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import withProgressBarLoadingDialog
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class MainPresenter
@Inject constructor(
    private val userEventData: UserEventData,
    private val chatHelper: ChatHelper,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val chatRepository: ChatRepository,
    private val locationProviderClient: FusedLocationProviderClient,
    private val rxPermissions: RxPermissions,
    private val notificationManager: NotificationManager,
    private val connectivityProvider: ConnectivityProvider,
    private val eventRepository: EventRepository,
    private val socket: SocketIOManager,
    private val context: Context,
) : BasePresenter<MainContract.View>(appData), MainContract.Presenter {

    private var isIgnoreToken = false
    lateinit var newMessageTitleText: String
    lateinit var photoMessageText: String
    lateinit var chatAcceptMessageText: String

    private val chatCompositeDisposable = CompositeDisposable()
    private val errorMessageDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }
    private val timerCompositeDisposable = CompositeDisposable()

    private var isAuthRequired = false
    private var isFromQr = false
    private var canShowBrowser = false
    private var inappList: Deque<NotificationModel>? = null
    private val inAppListNew = arrayListOf<Notification>()

    private var isDoNotCheckConnectionFragmentOpened = false
    private var isInternetConnected = true
    var isSplashShown = true


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable

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
        subscribeToTokenUpdates()
        checkAppUpdate()
    }

    private fun checkAppUpdate() {
        compositeDisposable += authRepository.checkAppUpdate(BuildConfig.VERSION_NAME)
            //.flatMap { checkAppUpdateAvailable(it) }
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
            .performOnBackgroundOutOnMain()
            .subscribeSimple { token ->
                unsubscribeChat()
                if (!isIgnoreToken) {
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
    }

    var isEditingPhone = false
    private fun loadUser() {
        compositeDisposable += Completable.create { emitter ->
            val disposable = CompositeDisposable()
            disposable += userRepository.getUserShortNew().subscribeSimple(
                onError = { emitter.onError(it) },
                onSuccess = { user ->
                    updateUserInShake(user)
                    disposable += Completable.merge(
                        listOf(
                            getInAppRequest(),
                            checkUserLocation(),
                            getAdditionalData()
                        )
                    )
                        .andThen(Completable.defer { checkInternetConnected() })
                        .doOnComplete { connectToSocket(appData.getId()) }
                        .andThen(Completable.defer { checkShowGreetings() })
                        .subscribeSimple(
                            onError = { emitter.onError(it) },
                            onComplete = { emitter.onComplete() }
                        )
                })
            emitter.setDisposable(disposable)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.hideSplashScreen()
                    isAuthRequired = true
                    viewState.apply {
                        hideLoadingDialog()
                        showLogin()
                        checkIntent()
                    }
                    initInternetConnectionCheck()
                },
                onComplete = {
                    viewState.hideSplashScreen()
                    if (!isEditingPhone) {
                        viewState.apply {
                            hideLoadingDialog()
                            showRecommendations()
                            checkIntent()
                        }
                        showNextInApp()
                        initInternetConnectionCheck()
                    }
                    isEditingPhone = false
                }
            )
        /*
        val loadUser = userRepository.getUserShortNew().ignoreElement()
        val inApp = userRepository.getInAppList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 50,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_IS_IN_APP to true,
                NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
//                NotificationModel.NOTIFICATION_IS_IN_APP to 0,
//                NotificationModel.NOTIFICATION_ACKNOWLEDGED to 0
            )
        ).doOnSuccess { inappList = LinkedList(it) }.ignoreElement()

        compositeDisposable += Completable.merge(listOf(loadUser, checkUserLocation(), inApp))
            .andThen(Completable.defer { checkInternetConnected() })
            //.andThen(subscribeToNotifications())
            .doOnComplete { connectToSocket(appData.getId()) }
            .andThen(Completable.defer { checkShowGreetings() })
            //.andThen(Maybe.defer { checkUserEvent() })
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    isAuthRequired = true
                    viewState.apply {
                        hideLoadingDialog()
                        showLogin()
                        checkIntent()
                    }
                    initInternetConnectionCheck()
                },
                onComplete = {
                    if (!isEditingPhone) {
                        viewState.apply {
                            hideLoadingDialog()
                            showRecommendations()
                            checkIntent()
                        }
                        showNextInapp()
                        initInternetConnectionCheck()
                    }
                    isEditingPhone = false
                },
                //onSuccess = {}
            )
         */
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

    private fun checkInternetConnected(): Completable {
        return Completable.create { emitter ->
            val connection = connectivityProvider.observeNetworkConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    isInternetConnected = it
                    if (it || appData.getUser().default_event != null) {
                        if (!emitter.isDisposed) emitter.onComplete()
                    } else {
                        checkInternetConnection()
                    }
                }, {
                    if (!emitter.isDisposed) emitter.onError(it)
                })

            emitter.setDisposable(connection)
        }
    }

    private fun checkShowGreetings(): Completable {
        return if (isAuthRequired) {
            isAuthRequired = false
            Completable.fromAction {
                viewState.apply {
                    hideAllLoadingDialogs()
                    if (!isEditingPhone) {
                        showGreetings()
                    }
                }
            }
                .subscribeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
        } else {
            Completable.complete()
        }
    }

    private fun checkUserEvent(): Maybe<Boolean> {
        return appData.defaultEvent?.let { event ->
            userEventData.load(event.toString())
                .andThen(Maybe.just(true))
                .onErrorReturn { false }
        } ?: Maybe.just(false)
        /*return appData.getUser().default_event?.let { event ->
            userEventData.load(event.id)
                    .andThen(Maybe.just(true))
                    .onErrorReturn { false }
        } ?: Maybe.just(false)*/
    }

    private fun checkUserLocation(): Completable {
        /*return userRepository.userEventCalendar()
                .flatMapObservable { Observable.fromIterable(it) }
                .filter { calendar ->
                    val now = System.currentTimeMillis() / 1000
                    calendar.time.any { time -> time.end > now && time.start <= now }
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapMaybe { calendar ->
                    if (calendar.isEmpty()) Maybe.empty()
                    else getLocation()
                            .timeout(5, TimeUnit.SECONDS)
                            .map { calendar to it }
                }
                .observeOn(Schedulers.io())
                .onErrorComplete()
                .flatMapCompletable {
                    val calendar = it.first
                    val location = it.second
                    val ids = calendar.map { calendarItem -> calendarItem.eventId }
                    val atEvents = calendar.map { calendarItem ->
                        checkUserLocationInEventArea(location, calendarItem.eventPlaceGpsLat, calendarItem.eventPlaceGpsLon)
                    }
                    userRepository.setUserAtEvent(ids, atEvents, location.latitude, location.longitude)
                }
                .onErrorComplete()*/

        return eventRepository.getUserCalendarEvent(EventCalendarBody.CALENDAR_EVENT)
            .flatMapObservable { Observable.fromIterable(it.data) }
            .filter { calendar ->
                val now = System.currentTimeMillis() / 1000
                appData.defaultEvent = calendar.entity?.id
                serverDateToMilliseconds(
                    calendar.date?.to ?: "",
                    DATE_FORMAT_SERVER_TIMESTAMP
                ) > now &&
                        serverDateToMilliseconds(
                            calendar.date?.from ?: "",
                            DATE_FORMAT_SERVER_TIMESTAMP
                        ) <= now
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapMaybe { calendar ->
                if (calendar.isEmpty()) Maybe.empty()
                else getLocation()
                    .timeout(5, TimeUnit.SECONDS)
                    .map { calendar to it }
            }
            .observeOn(Schedulers.io())
            //.flatMapCompletable { Completable.complete() }
            //.onErrorComplete()
            .flatMapCompletable {
                val calendar = it.first
                val location = it.second
                val ids = calendar.map { calendarItem -> calendarItem.id }
                val atEvents = calendar.map { calendarItem ->
                    checkUserLocationInEventArea(
                        location, /*calendarItem.address?.lat?:*/
                        0.0, /*calendarItem.address?.lon?:*/
                        0.0
                    )
                }
                userRepository.setUserAtEvent(ids, atEvents, location.latitude, location.longitude)
            }
            .onErrorComplete()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Maybe<Location> {
        return rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .firstElement()
            .flatMap { isGranted ->
                if (isGranted) {
                    Maybe.create<Location> { emitter ->
                        val locationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setNumUpdates(1)

                        val callback = object : LocationCallback() {
                            override fun onLocationResult(location: LocationResult) {
                                location.lastLocation?.let {
                                    emitter.onSuccess(it)
                                }
                            }
                        }
                        locationProviderClient.requestLocationUpdates(
                            locationRequest,
                            callback,
                            Looper.myLooper()!!
                        )
                            .addOnFailureListener {
                                emitter.onError(it)
                                it.printStackTrace()
                            }

                        emitter.setCancellable {
                            locationProviderClient.removeLocationUpdates(callback)
                        }
                    }
                } else Maybe.empty()
            }
    }

    private fun checkUserLocationInEventArea(
        userLocation: Location,
        areaLat: Double,
        areaLon: Double
    ): Boolean {
        val distance = FloatArray(1).apply {
            Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                areaLat,
                areaLon,
                this
            )
        }

        return distance[0] <= EVENT_AREA_DISTANCE
    }

    private fun showNextInApp() {
        if (!inAppListNew.isNullOrEmpty()) {
            viewState.showInAppNew(inAppListNew)
        }
//        inappList?.pollFirst()?.let {
//            val notification = Notification.fromRemoteNotification(it)
//            viewState.showInApp(notification)
//            onInappOkClick(notification)
//        }
    }

    override fun onHandleChat(chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired) return
        viewState.showChat(chatId, userName)
    }

    override fun onHandleEventCode(event: String) {
        if (isAuthRequired) {
            viewState.showLogin()
        } else {
            compositeDisposable += eventRepository.getEventsList(
                mapOf(
                    EventNew.EVENT_LIMIT to 1, EventNew.EVENT_OFFSET to 0,
                    EventNew.EVENT_BINDS to "rights,organization,tag,page,activity,user-registration,user-form-result,current-user-registration,destination-scheme,eventRegistrationState",
                    EventNew.EVENT_CODE to event
                )
            )
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribe({
                    if (it.data.isNotEmpty())
                        viewState.showAboutEvent(it.data[0]?.id.toString())
                }, {
                    it.printStackTrace()
                })
        }

    }

    override fun onHandleEvent(event: String) {
        if (isAuthRequired) {
            viewState.showLogin()
        } else {
            if (!event.isNullOrEmpty()) {
                viewState.showAboutEvent(event)
            }
        }
    }

    override fun onHandleAuthToOtherPlatform(url: String, type: AuthType) {
        if (isAuthRequired) {
            viewState.showLogin()
            canShowBrowser = true
        } else {
            if (canShowBrowser) {
                observeDeeplink(url, type)
            } else {
                viewState.showAccountChangeFragment(url, type)
            }
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
        viewState.showInviteRegister(email, code, name, lastName, middleName, invite)
    }

    override fun onHandleRecoverPasswordLink(userId: String, code: String) {
        authRepository.checkRecoveryCodeNew("email", code)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.showDialogRecoverPassword(userId, code)
            }.call(compositeDisposable)
    }


    override fun onHandleSocialNetworkConfirm(userId: String, code: String) {
        compositeDisposable += authRepository.confirmEmailSocialNetwork(userId, code)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                // do nothing
            }, {
                it.printStackTrace()
            })
    }

    override fun onHandleNotification(notification: RemoteNotification) {
        if (isAuthRequired) return
        showNotification(notification.id)
    }

    private fun showNotification(notificationId: Int) {
        compositeDisposable += userRepository.getNotificationDetail(notificationId.toString(), true)
            .performOnBackgroundOutOnMain()
            .withProgressBarLoadingDialog(viewState)
            .subscribeSimple { viewState.showNotification(Notification.fromRemoteNotification(it)) }
    }

    override fun openPgrfFromInvite(inviteId: String) {
        compositeDisposable += userRepository.getNotificationsList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 5,
                NotificationModel.NOTIFICATION_OFFSET to 0,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_LOAD_MODEL to true,
                NotificationModel.NOTIFICATION_SORT to "desc",
                NotificationModel.NOTIFICATION_ENTITY_TYPE to "invitePgfr",
                NotificationModel.NOTIFICATION_EVENT_ID to inviteId
            )
        )
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (!it.data.isNullOrEmpty()) {
                    viewState.showNotification(it.data[0])
                }
            }
    }

    override fun openAuthWebsiteFragment(code: String) {
        if (!isAuthRequired) {
            viewState.showAuthWebsiteFragment(code)
        } else {
            isFromQr = true
            viewState.showLogin()
        }
    }


    private fun connectToSocket(userId: Int) {
        chatCompositeDisposable += socket.connect()
            .performOnBackgroundOutOnMain()
            .subscribe({
                val connected = it == SocketConnectionState.CONNECTED
                chatHelper.isConnectingToSocket = connected
                if (connected) {
                    EventBus.getDefault().post(OnSocketConnectEvent())
                    if (chatCompositeDisposable.size() == 1) {
                        subscribeChatNewMessage()
                        subscribeToNotifications()
                        subscribeChatUnreadCount()
                        subscribeChatRequestsCount()
                        emitValueUpdates()
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun subscribeToNotifications() {
        compositeDisposable += userRepository.getNotificationNotReadedSize(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 1,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
            )
        ).performOnBackgroundOutOnMain()
            .subscribe({
                appData.notificationsCount = it
                chatCompositeDisposable += socket.subscribeToTotalNotificationsCount()
                    .performOnBackgroundOutOnMain()
                    .subscribe({ nCount ->
                        appData.notificationsCount = nCount
                    }, {})
            }, {
                it.printStackTrace()
                appData.notificationsCount = 0
            })
        /*return userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                .doOnComplete { appData.isSubscribedToPush = true }
                .doOnError { appData.isSubscribedToPush = false }
                .onErrorComplete()*/
    }

    private fun subscribeChatUnreadCount() {
        chatCompositeDisposable += socket.subscribeToTotalMessagesCount()
            .performOnBackgroundOutOnMain()
            .subscribe({
                Log.e("CHAT MESSAGE COUNT", it.toString())
                appData.chatUnreadMessageCount = it
            }, {
                it.printStackTrace()
                appData.chatUnreadMessageCount = 0
            })
    }

    private fun subscribeChatNewMessage() {
        //chatCompositeDisposable += socket.subscribeToChatUpdate()
        chatCompositeDisposable += socket.subscribeNewChatMessage()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onNext = {
                    Log.e("CHAT NEW MESSAGE", it.data.toString())
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

    private fun emitValueUpdates() {
        compositeDisposable += socket.connectToUpdates()
            .performOnBackgroundOutOnMain()
            .subscribe()
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

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeChat()
        chatHelper.currentChatId = null
//        AuthBackground.clear()
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

    override fun onBackClick() {
        if (!appData.isLoggedOut) viewState.navigateUp()
    }

    fun startUpdateTimer(time: Long?, isRequired: Boolean) {
        var counter = time?:0
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

    private fun observeDeeplink(url: String, type: AuthType) {
        when (type) {
            AuthType.OTHER_PLATFORM -> {
                val uri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("new_session", "true")
                    .appendQueryParameter("access_token", appData.token)
                    .build()
                viewState.showBrowser(uri.toString())
            }
            else -> {
            }
        }
    }

    private fun getInAppRequest(): Completable {
        return userRepository.getInAppList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 50,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_IS_IN_APP to true,
                NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
                //NotificationModel.NOTIFICATION_ACKNOWLEDGED to true
            )
        ).doOnSuccess {
            inappList = LinkedList(it)
            inAppListNew.addAll(it.map { n ->
                Notification.fromRemoteNotification(n)
            })
        }.ignoreElement()
    }

    private fun getAdditionalData(): Completable {
        return Single.merge(
            userRepository.getEducationLevel(),
            userRepository.getSpeciality(),
            userRepository.getAcademicDegrees()
        ).ignoreElements()
    }

    fun changeScrollingOffset(value: Int) = viewState.setAppBarElevation(abs(value / 10f))

    private fun checkAppUpdateAvailable(update: AppUpdateModel): Maybe<AppUpdateModel> {
        return Maybe.create { emitter ->
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                update.isAvailable =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                emitter.onSuccess(update)
            }
            appUpdateManager.appUpdateInfo.addOnFailureListener {
                emitter.onSuccess(update)
            }
        }
    }

    companion object {
        private const val EVENT_AREA_DISTANCE = 500
    }
}
