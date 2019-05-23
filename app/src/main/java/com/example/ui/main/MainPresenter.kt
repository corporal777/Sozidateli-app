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
        private val chatRepository: ChatRepository
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
                                    Completable.mergeArray(
                                            subscribeToNotifications().onErrorComplete()
                                    )
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

                                    subscribeToChat(appData.getUser().user_id)

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

    private fun subscribeToNotifications(): Completable {
        return userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                .doOnComplete { appData.isSubscribedToPush = true }
                .doOnError { appData.isSubscribedToPush = false }
                .onErrorComplete()
    }

    private fun subscribeToChat(uid: Int) {
        compositeDisposable += chatRepository.connect(uid.toString())
                .performOnBackground()
                .subscribe {
//                    subscribeChatUnreadCount()
//                    subscribeChatLastMessage()
                }
    }

    private fun subscribeChatUnreadCount() {
        chatRepository.subscribeChatUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.chatUnreadMessageCount = it
                }, {
                    appData.chatUnreadMessageCount = 0
                }).call(chatCompositeDisposable)
    }

    private fun subscribeChatLastMessage() {
        chatCompositeDisposable += chatRepository.loadChatLastMessage()
                .flatMapCompletable { setMessageShowed(it.chatId, it.messageId) }
                .onErrorComplete()
                .andThen(chatRepository.subscribeChatLastMessage())
                .doOnNext { chatNotificationHelper.isConnectingToLastMessageDatabase = true }
                .doFinally { chatNotificationHelper.isConnectingToLastMessageDatabase = false }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    processLastChatMessageUpdate(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun processLastChatMessageUpdate(localMessage: LocalNotification) {
        if (localMessage.isShowed) return
        val chatId = localMessage.chatId ?: return
        val messageId = localMessage.messageId ?: return

        chatCompositeDisposable += setMessageShowed(chatId, messageId)
                .performOnBackgroundOutOnMain()
                .mergeWith(Completable.fromAction {
                    val message = localMessage.text ?: return@fromAction
                    val senderId = localMessage.senderId
                    val senderName = localMessage.userName ?: return@fromAction
                    chatNotificationHelper.showNotificationIfCan(
                            chatId = chatId,
                            messageId = messageId,
                            message = message,
                            senderId = senderId,
                            senderName = senderName,
                            avatarUrl = localMessage.avatar
                    )
                })
                .subscribe({}, {
                    it.printStackTrace()
                })
    }

    private fun setMessageShowed(chatId: String?, messageId: String?): Completable {
        return if (chatId != null && messageId != null) {
            chatRepository.setMessageShowed(appData.getUser().user_id.toString(), chatId, messageId)
        } else {
            val message = "Can not update message with null parameters: chatId: $chatId, messageId: $messageId"
            Completable.error(NullPointerException(message))
        }
    }

    private fun unsubscribeChat() {
        chatCompositeDisposable.clear()
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
