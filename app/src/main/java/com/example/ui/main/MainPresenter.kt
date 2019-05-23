package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.UserEventData
import com.example.data.models.LocalNotification
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.chat.ChatNotificationHelper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import performOnBackground
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.SocketRepository
import ru.houseofapps.chat.models.NewMessage
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val chatNotificationHelper: ChatNotificationHelper,
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
        private val chatRepository: ChatRepository,
        private val socketRepository: SocketRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private val chatCompositeDisposable = CompositeDisposable()

    private var isAuthRequired = false

    override var isNeedErrorHandler: Boolean
        get() = false
        set(value) {}

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onTokenChange
                .performOnBackgroundOutOnMain()
                .subscribe { token ->
                    unsubscribeChat()

                    if (token.value == null) {
                        isAuthRequired = true
                        viewState.showLogin()
                    } else {
                        userRepository.getUserShort()
                                .flatMapCompletable {
                                    connectToSocket(it.user_id)
                                    subscribeToNotifications()
                                }
                                .andThen(
                                        if (isAuthRequired) {
                                            isAuthRequired = false
                                            Completable.fromAction { viewState.showGreetings() }
                                                    .subscribeOn(AndroidSchedulers.mainThread())
                                                    .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
                                                    .andThen(Maybe.just(true))
                                        } else Maybe.just(false))
                                .performOnBackgroundOutOnMain()
                                .withLoadingDialog(viewState)
                                .subscribe({
                                    viewState.apply {
                                        val event = appData.getUser().default_event
                                        if (event != null) {
                                            eventData.event = event
                                            showEvent()
                                        } else {
                                            showEventList(if (it) R.id.welcome_fragment else R.id.splash_fragment)
                                        }
                                        checkIntent()
                                    }
                                }, {
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

    override fun onHandleChat(userId: String, chatId: String, userName: String, notificationId: String) {
        if (isAuthRequired) return
        viewState.showChat(userId, chatId, userName)
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
        socketRepository.connect(userId.toString())
                .performOnBackgroundOutOnMain()
                .subscribe({
                    subscribeChatNewMessage()
                    subscribeChatUnreadCount()
                    chatNotificationHelper.isConnectingToSocket = true
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    private fun subscribeToNotifications(): Completable {
        return userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                .doOnComplete { appData.isSubscribedToPush = true }
                .doOnError { appData.isSubscribedToPush = false }
                .onErrorComplete()
    }

    private fun subscribeChatUnreadCount() {
        socketRepository.subscribeToAllUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.chatUnreadMessageCount = it
                }, {
                    it.printStackTrace()
                    appData.chatUnreadMessageCount = 0
                }).call(compositeDisposable)
    }

    private fun subscribeChatNewMessage() {
        chatCompositeDisposable += socketRepository.subscribeToNewMessage()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    processNewMessageMessage(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun processNewMessageMessage(newMessage: NewMessage) {
        val chatId = newMessage.room
        val messageId = newMessage.message._id
        val message = newMessage.message.message ?: ""
        val senderId = newMessage.message.senderKey.toInt()

        userRepository.getUserById(senderId)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    chatNotificationHelper.showNotificationIfCan(
                            chatId = chatId,
                            messageId = messageId,
                            message = message,
                            senderId = senderId,
                            senderName = it.fullName,
                            avatarUrl = it.user_avatar)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    private fun unsubscribeChat() {
        chatCompositeDisposable.clear()
        if(socketRepository.isConnected()) {
            socketRepository.disconnect()
            chatNotificationHelper.isConnectingToSocket = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeChat()
        chatNotificationHelper.currentChatId = null
    }

    override fun onOpenStartDestination() {
        viewState.showBackButton(false)
        chatNotificationHelper.currentChatId = null
    }

    override fun onOpenNotStartDestination() {
        viewState.showBackButton(true)
        chatNotificationHelper.currentChatId = null
    }

    override fun onOpenChatDestination(chatId: String?) {
        viewState.showBackButton(true)
        chatNotificationHelper.currentChatId = chatId
    }
}
