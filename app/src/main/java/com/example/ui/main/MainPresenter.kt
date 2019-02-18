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

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onTokenChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    if (it.value == null) {

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

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)
}
