package com.example.ui.main

import android.Manifest
import android.app.NotificationManager
import android.location.Location
import android.util.Log
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EmailCodeBody
import com.example.data.bodies.EventsCalendarListBody
import com.example.data.models.ChatMessageAdditionalData
import com.example.data.models.Notification
import com.example.data.models.RemoteNotification
import com.example.events.OnSocketConnectEvent
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.tbruyelle.rxpermissions2.RxPermissions
import fromJson
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.ChatConnectionStatus
import ru.houseofapps.chat.models.Message
import ru.houseofapps.chat.models.NewMessage
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
        private val haChat: HAChat,
        private val appData: AppData,
        private val chatRepository: ChatRepository,
        private val locationProviderClient: FusedLocationProviderClient,
        private val rxPermissions: RxPermissions,
        private val notificationManager: NotificationManager,
        private val connectivityProvider: ConnectivityProvider,
        private val eventRepository: EventRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    lateinit var newMessageTitleText: String
    lateinit var photoMessageText: String
    lateinit var chatAcceptMessageText: String

    private val chatCompositeDisposable = CompositeDisposable()
    private val errorMessageDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    private var isAuthRequired = false
    private var inappList: Deque<RemoteNotification>? = null

    private var isDoNotCheckConnectionFragmentOpened = false
    private var isInternetConnected = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (!appData.isStoriesShown) {
            viewState.showStories()
        } else {
            onStoriesComplete()
        }
    }

    override fun onStoriesComplete() {
        appData.isStoriesShown = true
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
                        loadUser()
                    }
                }
    }

    private fun loadUser() {
        if (isAuthRequired) viewState.showLoadingDialog()
        val loadUser = userRepository.getUserShortNew()
                //.doOnSuccess { inappList = LinkedList(it.inapps) }
                .ignoreElement()
        val loadCalendar = checkUserLocation()
        compositeDisposable += Completable.merge(listOf(loadUser, loadCalendar))
                .andThen(Completable.defer { checkInternetConnected() })
                //.andThen(subscribeToNotifications())
                //.doOnComplete { connectToSocket(appData.getId()) }
                //.andThen(Completable.defer { checkShowGreetings() })
                .andThen(Maybe.defer { checkUserEvent() })
                .performOnBackgroundOutOnMain()
                .subscribe({ isMustShowEvent ->
                    viewState.apply {
                        hideLoadingDialog()
                        if (isMustShowEvent) showEvent()
                        else showRecommendations()
                        checkIntent()
                        showNextInapp()
                    }

                    initInternetConnectionCheck()
//                    AuthBackground.clear()
                }, {
                    it.printStackTrace()
                    isAuthRequired = true
                    viewState.apply {
                        hideLoadingDialog()
                        showLogin()
                        checkIntent()
                    }
                    initInternetConnectionCheck()
                })
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
                    showGreetings()
                }
            }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
        } else {
            Completable.complete()
        }
    }

    private fun checkUserEvent(): Maybe<Boolean> {
        return /*appData.getUser().default_event?.let { event ->
            userEventData.load(event.id)
                    .andThen(Maybe.just(true))
                    .onErrorReturn { false }
        } ?:*/ Maybe.just(false)
    }

    private fun checkUserLocation(): Completable {
        return userRepository.getEventCalendar(EventsCalendarListBody())
                .flatMapObservable { Observable.fromIterable(it.data) }
                .filter { calendar ->
                    val now = System.currentTimeMillis() / 1000
                    //calendar.time.any { time -> time.end > now && time.start <= now }
                    serverDateToMilliseconds(calendar.holdingDate?.to?: "", DATE_FORMAT_SERVER_TIMESTAMP) > now &&
                            serverDateToMilliseconds(calendar.holdingDate?.from?: "", DATE_FORMAT_SERVER_TIMESTAMP) <= now
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
                        checkUserLocationInEventArea(location, calendarItem.address?.lat?: 0.0, calendarItem.address?.lon?: 0.0)
                    }
                    userRepository.setUserAtEvent(ids, atEvents, location.latitude, location.longitude)
                }
                .onErrorComplete()
    }

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
                            locationProviderClient.requestLocationUpdates(locationRequest, callback, null)
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

    private fun checkUserLocationInEventArea(userLocation: Location, areaLat: Double, areaLon: Double): Boolean {
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
        inappList?.pollFirst()?.let { viewState.showInapp(Notification.fromRemoteNotification(it)) }
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

    override fun onInappOkClick() {
        viewState.hideInapp()
    }

    override fun onHandleChat(chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired) return
        viewState.showChat(chatId, userName)
    }

    override fun onHandleEvent(event: String) {
        if (isAuthRequired) return
        compositeDisposable += eventRepository.getEventByCode(event)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showEvent(it.event.id)
                }, {
                    it.printStackTrace()
                })
        //viewState.showEvent(event)
    }

    override fun onInviteRegister(email: String, code: String) {
        viewState.showInviteRegister(email, code)
    }

    override fun onHandleAuthLink(/*email: String, */code: String) {
        if (appData.token != null) return
        isAuthRequired = true
        /*authRepository.registerConfirm(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.showFinishRegister() }, { viewState.showLogin() })
                .call(compositeDisposable)*/

        userRepository.confirmEmailCode(appData.getId(), EmailCodeBody(code = code))
                .performOnBackgroundOutOnMain()
                .subscribe({ appData.getUserNew().apply {
                    viewState.showFinishRegister(name?: "",
                            lastName?: "", middleName?.value,
                            phone?.get(0)?.value, email?.value?: "", code,
                            phone?.get(0)?.isConfirmed ?: false, middleName?.value == USER_DATA_EMPTY)
                } }, { viewState.showLogin() })
                .call(compositeDisposable)

        /*authRepository.registerData(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.showFinishRegister(it.user?.user_name?: "",
                        it.user?.user_last_name?: "", it.user?.user_middle_name,
                it.user?.user_phone, it.user?.user_email?: "", code,
                        it.user?.user_phone_confirmed?: false, it.user?.user_middle_name == USER_DATA_EMPTY) }, { viewState.showLogin() })
                .call(compositeDisposable)*/
    }

    override fun onHandleRecoverPasswordLink(email: String, code: String) {
        authRepository.checkRecoveryCode(email, code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe {
                    viewState.showDialogRecoverPassword(email, code)
                }.call(compositeDisposable)
    }

    override fun onHandleChangeEmailConfirm(code: String) {
        if (isAuthRequired) return
        userRepository.confirmEmailCode(appData.getId(), EmailCodeBody(code = code))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showDialogChangeEmailSuccess()
                }, {
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
        compositeDisposable += userRepository.getNotification(notificationId)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple { viewState.showNotification(Notification.fromRemoteNotification(it)) }
    }

    override fun onSetPassword(email: String, code: String, password: String) {
        authRepository.setPassword(email, code, password)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({}, { viewState.showDialogRecoverPassword(email, code) }).call(compositeDisposable)
    }


    private fun connectToSocket(userId: Int) {
        chatCompositeDisposable += haChat.connect(userId.toString())
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
                })
    }

    private fun subscribeToNotifications(): Completable {
        return userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                .doOnComplete { appData.isSubscribedToPush = true }
                .doOnError { appData.isSubscribedToPush = false }
                .onErrorComplete()
    }

    private fun subscribeChatUnreadCount() {
        chatCompositeDisposable += haChat.subscribeToAllUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.chatUnreadMessageCount = it
                }, {
                    it.printStackTrace()
                    appData.chatUnreadMessageCount = 0
                })
    }

    private fun subscribeChatNewMessage() {
        chatCompositeDisposable += haChat.subscribeToNewMessage()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    processNewChatMessage(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun subscribeChatRequestsCount() {
        compositeDisposable += Flowable.create<Int>({ emitter ->
            val disposables = CompositeDisposable()
            disposables += chatRepository.getChatInvitesCount().subscribe({ emitter.onNext(it.count) }, { emitter.onError(it) })
            disposables += haChat.subscribeTo<Number>(ACTION_REQUEST_COUNT).subscribe({ emitter.onNext(it.toInt()) }, { emitter.onError(it) })
            disposables += haChat.subscribeToExcludeFlagChange()
                    .flatMapSingle { chatRepository.getChatInvitesCount() }
                    .subscribe({ emitter.onNext(it.count) }, { emitter.onError(it) })
            emitter.setDisposable(disposables)
        }, BackpressureStrategy.LATEST)
                .performOnBackgroundOutOnMain()
                .subscribe({ appData.chatRequestsCount = it }, { appData.chatRequestsCount = 0 })
    }

    private fun processNewChatMessage(newMessage: NewMessage) {
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
                .onErrorResumeNext(userRepository.getUserById(newMessage.message.senderKey).map {
                    ChatMessageAdditionalData(it.user_id, it.user_name, it.user_last_name, it.user_middle_name, it.user_avatar)
                })
                .performOnBackgroundOutOnMain()
                .subscribeSimple { messageData ->
                    val senderName = "${messageData.name} ${messageData.lastName}${messageData.middleName?.let { if (it == "-") "" else " $it" }
                            ?: ""}"
                    val title = "$newMessageTitleText $senderName"
                    chatHelper.showNotificationIfCan(chatId, messageId, title, message, title, messageData.avatar)
                }
    }

    private fun unsubscribeChat() {
        haChat.disconnect()
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

    companion object {
        private const val EVENT_AREA_DISTANCE = 500
    }
}
