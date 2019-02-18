package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onTokenChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (it.value == null) {
                        viewState.initWithAuth()
                    } else {
                        userRepository.getUser()
                                .flatMap { userRepository.getFcmToken() }
                                .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                                .doOnComplete { appData.isSubscribedToPush = true }
                                .performOnBackgroundOutOnMain()
                                .subscribe({}, {})
                                .call(compositeDisposable)
                    }
                }, {

                })
                .call(compositeDisposable)
    }

    override fun onHandleAuthLink(email: String, code: String) {
        if (appData.token != null) return
        authRepository.registerConfirm(email, code)
                .andThen(appData.onUserChange)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value
                    viewState.apply {
                        when {
                            appData.token == null || user == null -> initWithAuth()
                            user.default_event != null -> initWithEvent()
                            else -> initWithEventList()
                        }
                    }
                }, {
                    viewState.initWithAuth()
                })
                .call(compositeDisposable)
    }

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)
}
