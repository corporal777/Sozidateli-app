package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.database.Db
import com.example.events.OnSocketConnectEvent
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.ACTION_REQUEST_COUNT
import com.example.util.UserEventLoadingHelper
import com.example.util.ChatHelper
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import ru.houseofapps.chat.models.ChatConnectionStatus
import ru.houseofapps.chat.models.Message
import ru.houseofapps.chat.models.NewMessage
import withLoadingDialog
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
        private val eventRepository: EventRepository,
        private val db: Db,
        private val appData: AppData,
        private val chatRepository: ChatRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    lateinit var photoMessageText: String

    private val chatCompositeDisposable = CompositeDisposable()

    private var isAuthRequired = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.tokenChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe { token ->
                    unsubscribeChat()
                    if (token.value == null) {
                        isAuthRequired = true
                        viewState.showLogin()
                    } else {
                        userRepository.getUserShort()
                                .flatMapCompletable { subscribeToNotifications() }
                                .andThen(checkShowGreetings())
                                .flatMap { checkUserEvent(it) }
                                .performOnBackgroundOutOnMain()
                                .withLoadingDialog(viewState)
                                .subscribe({ showAction ->
                                    viewState.apply {
                                        connectToSocket(appData.getUser().user_id)

                                        when (showAction) {
                                            SHOW_EVENT_LIST -> showEventList(R.id.splash_fragment)
                                            SHOW_EVENT_LIST_AFTER_GREETINGS -> showEventList(R.id.welcome_fragment)
                                            SHOW_USER_EVENT -> showEvent()
                                        }

                                        checkIntent()
                                    }
                                }, {
                                    it.printStackTrace()
                                    isAuthRequired = true
                                    viewState.apply {
                                        showLogin()
                                        checkIntent()
                                    }
                                })
                                .call(compositeDisposable)
                    }
                }
                .call(compositeDisposable)
    }

    private fun checkShowGreetings(): Maybe<Boolean> {
        return if (isAuthRequired) {
            isAuthRequired = false
            Completable.fromAction { viewState.showGreetings() }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
                    .andThen(Maybe.just(true))
        } else {
            Maybe.just(false)
        }
    }

    private fun checkUserEvent(isGreetingShown: Boolean): Maybe<Int> {
        return appData.getUser().default_event?.let { event ->
            UserEventLoadingHelper(userEventData, eventRepository, db.userEventDao()).load(event)
                    .doOnSuccess { if (it) userEventData.event = event }
                    .flatMap {
                        if (it) Maybe.just(if (isGreetingShown) SHOW_USER_EVENT_AFTER_GREETINGS else SHOW_USER_EVENT)
                        else Maybe.just(if (isGreetingShown) SHOW_EVENT_LIST_AFTER_GREETINGS else SHOW_EVENT_LIST)
                    }
        } ?: Maybe.just(if (isGreetingShown) SHOW_EVENT_LIST_AFTER_GREETINGS else SHOW_EVENT_LIST)
    }

    override fun onHandleChat(chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired) return
        viewState.showChat(chatId, userName)
    }

    override fun onHandleAuthLink(email: String, code: String) {
        if (appData.token != null) return
        isAuthRequired = true
        authRepository.registerConfirm(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({}, { viewState.showLogin() })
                .call(compositeDisposable)
    }

    override fun onHandleRecoverPasswordLink(email: String, code: String) {
        authRepository.checkRecoveryCode(email, code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe {
                    viewState.showDialogRecoverPassword(email, code)
                }.call(compositeDisposable)
    }

    override fun onHandleChangeEmailConfirm(email: String, code: String) {
        userRepository.changeEmailConfirm(email, code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showDialogChangeEmailSuccess()
                }, {
                    viewState.showDialogChangeEmailError()
                })
                .call(compositeDisposable)
    }

    override fun onHandleSocialNetworkConfirm(snType: String, id: String, code: String) {
        authRepository.confirmEmailSocialNetwork(snType, id, code)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({}, {}).call(compositeDisposable)
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

                    }
                    if (connected && chatCompositeDisposable.size() == 1) {
                        subscribeChatNewMessage()
                        subscribeChatUnreadCount()
                        subscribeChatRequestsCount()
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
                    processNewMessageMessage(it)
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

    private fun processNewMessageMessage(newMessage: NewMessage) {
        val chatId = newMessage.room
        val messageId = newMessage.message._id
        val message = when (newMessage.message.type) {
            Message.Type.IMAGE -> photoMessageText
            else -> newMessage.message.message
        }

        val senderId = newMessage.message.senderKey

        compositeDisposable += userRepository.getUserById(senderId)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    chatHelper.showNotificationIfCan(
                            chatId = chatId,
                            messageId = messageId,
                            message = message,
                            senderId = senderId,
                            senderName = it.fullName,
                            avatarUrl = it.user_avatar)
                }, {
                    it.printStackTrace()
                })
    }

    private fun unsubscribeChat() {
        haChat.disconnect()
        chatCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeChat()
        chatHelper.currentChatId = null
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

    companion object {
        private const val SHOW_EVENT_LIST = 0
        private const val SHOW_EVENT_LIST_AFTER_GREETINGS = 1
        private const val SHOW_USER_EVENT = 2
        private const val SHOW_USER_EVENT_AFTER_GREETINGS = 3
    }
}
