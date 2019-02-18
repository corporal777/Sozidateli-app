package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class MainPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private val tokenChangeListener = object : AppData.OnTokenChangeListener {
        override fun onTokenChange(token: String?) {
            if (token == null) {

            } else {
                userRepository.getUser()
                        .flatMap { userRepository.getFcmToken() }
                        .flatMapCompletable { userRepository.notificationsRegister(it.token) }
                        .doOnComplete { appData.isSubscribedToPush = true }
                        .performOnBackgroundOutOnMain()
                        .subscribe({}, {})
                        .call(compositeDisposable)
            }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.addOnTokenChangeListener(tokenChangeListener)
    }

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)

    override fun onDestroy() {
        super.onDestroy()
        appData.removeOnTokenChangeListener(tokenChangeListener)
    }
}
