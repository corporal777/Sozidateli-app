package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private var isAuthRequired = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onTokenChange
                .performOnBackgroundOutOnMain()
                .subscribe { token ->
                    if (token.value == null) {
                        isAuthRequired = true
                        viewState.showLogin()
                    } else {
                        userRepository.getUser()
                                .flatMapCompletable { subscribeToNotifications() }
                                .observeOn(AndroidSchedulers.mainThread())
                                .andThen(
                                        if (isAuthRequired) {
                                            isAuthRequired = false
                                            Completable.fromAction { viewState.showGreetings() }
                                                    .andThen(Completable.timer(3, TimeUnit.SECONDS, Schedulers.io()))
                                                    .andThen(Maybe.just(true))
                                        } else Maybe.just(false))
                                .performOnBackgroundOutOnMain()
                                .subscribe({
                                    viewState.apply {
                                        showEventList(if (it) R.id.welcome_fragment else R.id.splash_fragment)
                                    }
                                }, {
                                    viewState.showLogin()
                                })
                                .call(compositeDisposable)
                    }
                }
                .call(compositeDisposable)

        viewState.initWithEventList()
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

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)
}
