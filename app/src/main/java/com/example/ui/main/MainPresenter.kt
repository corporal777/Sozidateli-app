package com.example.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EmailCodeBody
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
import com.example.ui.base.BasePresenter
import com.example.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    private val socket: SocketIOManager
) : BasePresenter<MainContract.View>(appData), MainContract.Presenter {

    private var isRegister = false
    lateinit var newMessageTitleText: String
    lateinit var photoMessageText: String
    lateinit var chatAcceptMessageText: String

    private val chatCompositeDisposable = CompositeDisposable()
    private val errorMessageDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    private var isAuthRequired = false
    private var isFromQr = false
    private var canShowBrowser = false
    private var inappList: Deque</*RemoteNotification*/NotificationModel>? = null

    private var isDoNotCheckConnectionFragmentOpened = false
    private var isInternetConnected = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.deviceId = UUID.randomUUID().toString()
        if (!appData.isStoriesShown) {
            viewState.showStories()
        } else {
            onStoriesComplete()
        }

        compositeDisposable += appData.notificationsCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.showBadgeNotification(it > 0)
            }, {
                viewState.showBadgeNotification(false)
            })

        compositeDisposable += appData.chatMessageCountSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.showBadgeChat(it > 0)
            }, {
                viewState.showBadgeChat(false)
            })


    }

    override fun onStoriesComplete() {
        subscribeToTokenUpdates()
    }

    private fun subscribeToTokenUpdates() {
        compositeDisposable += appData.tokenChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribe { token ->
                unsubscribeChat()
                if (token.value == null) {
                    isAuthRequired = true
                    viewState.apply {
                        showLogin()
                        checkIntent()
                    }
                } else {
                    if (!isRegister) loadUser()
                }
            }
    }

    var isEditingPhone = false
    private fun loadUser() {
        val loadUser = userRepository.getUserShortNew().ignoreElement()
        val inApp = userRepository.getInAppList(
            mapOf(
                NotificationModel.NOTIFICATION_LIMIT to 50,
                NotificationModel.NOTIFICATION_USER to appData.getId(),
                NotificationModel.NOTIFICATION_IS_IN_APP to true,
                NotificationModel.NOTIFICATION_ACKNOWLEDGED to false
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
                                emitter.onSuccess(location.lastLocation)
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

    private fun showNextInapp() {
        inappList?.pollFirst()?.let {
            val notification = Notification.fromRemoteNotification(it)
            viewState.showInapp(notification)
            onInappOkClick(notification)
        }
    }

    override fun onInappHidden() {
        showNextInapp()
    }

    override fun onInappAcceptClick(inapp: Notification) {
        updateNotificationInvite(userRepository.notificationsInviteAccept(inapp.id), inapp.id)
    }

    override fun onInappCancelClick(inapp: Notification) {
        updateNotificationInvite(userRepository.notificationsInviteDecline(inapp.id), inapp.id)
    }

    private fun updateNotificationInvite(request: Completable, notificationId: Int) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple {
                notificationManager.cancel(notificationId)
                viewState.hideInapp()
            }
    }

    override fun onInappOkClick(inapp: Notification) {
        //updateNotificationInvite(userRepository.markAsRead(inapp.id.toString()), inapp.id)
        //userRepository.markAsRead(id.toString())
        viewState.hideInapp()
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

    override fun onHandleAuthLink(emaill: String, code: String) {
        //if (appData.token != null) return
        isAuthRequired = true
        /*authRepository.registerConfirm(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.showFinishRegister() }, { viewState.showLogin() })
                .call(compositeDisposable)*/
        isRegister = true
        userRepository.confirmEmailCode(appData.getId(), EmailCodeBody(code = code, email = emaill))
            .performOnBackgroundOutOnMain()
            .subscribe({
                compositeDisposable += userRepository.getUserShortNew()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        viewState.showFinishRegister(
                            it.name ?: "",
                            it.lastName ?: "",
                            it.middleName?.value,
                            it.phone?.get(0)?.value,
                            it.email?.value ?: "",
                            code,
                            it.phone?.get(0)?.isConfirmed ?: false,
                            it.middleName?.value == USER_DATA_EMPTY,
                            it.state?.nameEdited ?: true
                        )
                    }, { viewState.showLogin() })
                /*try {
                    appData.getUserNew().apply {
                        viewState.showFinishRegister(name?: "",
                                lastName?: "", middleName?.value,
                                phone?.get(0)?.value, email?.value?: "", code,
                                phone?.get(0)?.isConfirmed ?: false, middleName?.value == USER_DATA_EMPTY)
                    }
                } catch (e: Exception) {
                    viewState.showLogin()
                }*/
                isRegister = false
            }, {
                viewState.showLogin()
                isRegister = false
            })
            .call(compositeDisposable)

        /*authRepository.registerData(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.showFinishRegister(it.user?.user_name?: "",
                        it.user?.user_last_name?: "", it.user?.user_middle_name,
                it.user?.user_phone, it.user?.user_email?: "", code,
                        it.user?.user_phone_confirmed?: false, it.user?.user_middle_name == USER_DATA_EMPTY) }, { viewState.showLogin() })
                .call(compositeDisposable)*/
    }

    override fun onHandleRecoverPasswordLink(/*email: String, */code: String) {
        authRepository.checkRecoveryCodeNew("email", code)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe {
                viewState.showDialogRecoverPassword(/*email,*/ code)
            }.call(compositeDisposable)
    }

    override fun onHandleChangeEmailConfirm(code: String, email: String) {
        //if (isAuthRequired) return
        userRepository.confirmEmailCode(appData.getId(), EmailCodeBody(code = code, email = email))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                userRepository.getUserShortData()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        appData.updateUserNew {
                            this.email = it.email
                        }
                    }, {})
                compositeDisposable += userRepository.checkUserProfileSingle()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        if (appData.hasMaxState && appData.hasBaseState) {
                            viewState.showDialogHasMaxState()
                        } else if (!appData.hasMaxState && appData.hasBaseState) {
                            viewState.showDialogHasBaseState()
                        }
                    }, {})
                viewState.showDialogChangeEmailSuccess()
            }, {
                userRepository.getUserShortData()
                    .performOnBackgroundOutOnMain()
                    .subscribe({
                        appData.updateUserNew {
                            this.email = it.email
                        }
                    }, {})
                viewState.showDialogChangeEmailError()
            })
            .call(compositeDisposable)
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
//        val eventId = notification.event_id
//        val organizationId = notification.organization_id
//        when {
//            notification.type == TYPE_INVITE -> showNotification(notification.id)
//            notification.type == TYPE_RATE && eventId != 0 -> viewState.showRating(eventId.toString())
//            eventId != 0 -> viewState.showEvent(eventId.toString())
//            organizationId != 0 -> viewState.showOrganization(organizationId.toString())
//            else -> showNotification(notification.id)
//        }

        showNotification(notification.id)
    }

    private fun showNotification(notificationId: Int) {
        compositeDisposable += userRepository.getNotificationDetail(notificationId.toString(), true)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
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

    override fun onSetPassword(/*email: String, */code: String, password: String) {
        authRepository.recoverPasswordNew(RecoverPasswordBody("email", code, password))
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({

            }, {
                viewState.showDialogRecoverPassword(/*email,*/ code)
            })
            .call(compositeDisposable)
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
        /*chatCompositeDisposable += haChat.connect(userId.toString())
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val connected = it == ChatConnectionStatus.CONNECTED
                    chatHelper.isConnectingToSocket = connected
                    if (connected) {
                        EventBus.getDefault().post(OnSocketConnectEvent())

                        if (chatCompositeDisposable.size() == 1) {
                            subscribeChatNewMessage()
                            subscribeChatUnreadCount()
                            subscribeChatRequestsCount()
                        }
                    }
                }, {
                    it.printStackTrace()
                })*/
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
            .subscribe({
                Log.e("CHAT NEW MESSAGE", it.data.toString())
                it.data.forEach { message ->
                    chatHelper.showNotificationIfCan(
                        message.chat.toString(),
                        message.id.toString(),
                        message.sender?.name + " " + message.sender?.lastName,
                        message.message ?: "",
                        "",
                        message.sender?.avatar
                    )
                }
            }, {
                it.printStackTrace()
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

    /*private fun processNewChatMessage(newMessage: NewMessage) {
        val chatId = newMessage.room
        val messageId = newMessage.message._id
        val message = when (newMessage.message.type) {
            Message.Type.SERVICE -> if (newMessage.message.message == CHAT_SERVICE_MESSAGE_ACCEPT) chatAcceptMessageText else return
            Message.Type.IMAGE -> photoMessageText
            else -> newMessage.message.message
        }

        compositeDisposable += Maybe.fromCallable {
            newMessage.message.additionalData?.fromJson<ChatMessageAdditionalData>()
                    ?: throw NullPointerException("Additional data is null")
        }
                .onErrorResumeNext(userRepository.getUserByIdNew(newMessage.message.senderKey).map {
                    ChatMessageAdditionalData(it.id, it.name, it.lastName, it.middleName?.value, it.image?.uri)
                })
                .performOnBackgroundOutOnMain()
                .subscribeSimple { messageData ->
                    val senderName = "${messageData.name} ${messageData.lastName}${messageData.middleName?.let { if (it == "-") "" else " $it" }
                            ?: ""}"
                    val title = "$newMessageTitleText $senderName"
                    chatHelper.showNotificationIfCan(chatId, messageId, title, message, title, messageData.avatar)
                }
    }*/

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
        viewState.showBackButton(false)
        chatHelper.currentChatId = null
    }

    override fun onOpenNotStartDestination() {
        viewState.showBackButton(true)
        chatHelper.currentChatId = null
    }

    override fun onOpenChatDestination(chatId: String?) {
        viewState.showBackButton(true)
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
        isRegister = isIgnore
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

    companion object {
        private const val EVENT_AREA_DISTANCE = 500
    }
}
