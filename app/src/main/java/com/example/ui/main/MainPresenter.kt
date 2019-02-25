package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
        private val chatRepository: ChatRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private var isAuthRequired = false

    private var userId: String? = null
    private var chatId: String? = null
    private var userName: String? = null
    private var wasOpen = false
    private var chatUnreadCountDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onTokenChange
                .performOnBackgroundOutOnMain()
                .subscribe { token ->
                    unsubscribeChatUnreadCount()

                    if (token.value == null) {
                        isAuthRequired = true
                        viewState.showLogin()
                    } else {
                        userRepository.getUser()
                                .flatMapCompletable {
                                    Completable.mergeArray(
                                            subscribeToNotifications().onErrorComplete(),
                                            chatRepository.singInFirebase().onErrorComplete()
                                                    .doOnComplete { subscribeChatUnreadCount() }
                                    )
                                }
                                .andThen(
                                        if (isAuthRequired) {
                                            isAuthRequired = false
                                            Completable.fromAction { viewState.showGreetings() }
                                                    .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
                                                    .andThen(Maybe.just(true))
                                        } else Maybe.just(false))
                                .performOnBackgroundOutOnMain()
                                .withLoadingDialog(viewState)
                                .subscribe({
                                    viewState.apply {
                                        showEventList(if (it) R.id.welcome_fragment else R.id.splash_fragment)
                                        openChat()
                                        wasOpen = true
                                    }
                                }, {
                                    viewState.showLogin()
                                    wasOpen = true
                                })
                                .call(compositeDisposable)
                    }
                }
                .call(compositeDisposable)

        appData.onErrorHandlerListener.
                performOnBackgroundOutOnMain()
                .subscribe {
                    it.value?.let {
                        it.errors?.let { messages->
                            viewState.showToast(messages.joinToString(separator = "\n"))
                        }
                    }
                }.call(compositeDisposable)
    }

    override fun onHandleChat(userId: String, chatId: String, userName: String, notificationId: String) {
        if (appData.openedNotificationId === null || appData.openedNotificationId != notificationId) {
            this.userId = userId
            this.chatId = chatId
            this.userName = userName
            appData.openedNotificationId = notificationId
            if (wasOpen) {
                openChat()
            }
        }

    }

    private fun openChat() {
        chatId?.let {
            viewState.showChat(userId!!, chatId!!, userName!!)
            userId = null
            chatId = null
            userName = null
        }
    }

    override fun onHandleAuthLink(email: String, code: String) {
        if (appData.token != null) return
        isAuthRequired = true
        authRepository.registerConfirm(email, code)
                .performOnBackgroundOutOnMain()
                .subscribe({}, { viewState.showLogin() })
                .call(compositeDisposable)
    }

    private fun subscribeToNotifications(): Completable {
        return userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                .doOnComplete { appData.isSubscribedToPush = true }
                .doOnError { appData.isSubscribedToPush = false }
                .onErrorComplete()
    }

    private fun subscribeChatUnreadCount() {
        chatUnreadCountDisposable = chatRepository.subscribeChatUnreadMessageCount()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.chatUnreadMessageCount = it
                }, {
                    appData.chatUnreadMessageCount = 0
                }).apply {
                    call(compositeDisposable)
                }
    }

    private fun unsubscribeChatUnreadCount() {
        chatUnreadCountDisposable?.dispose()
    }

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)
}
