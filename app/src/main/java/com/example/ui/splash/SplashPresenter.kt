package com.example.ui.splash

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SplashPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<SplashContract.View>(), SplashContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            if (appData.token == null) {
                initWithAuth()
            } else {
                userRepository.getUser()
                        .performOnBackgroundOutOnMain()
                        .subscribe({
                            if (it.default_event != null) initWithEvent()
                            else initWithEventList()
                        }, {
                            appData.token = null
                            initWithAuth()
                        })
                        .call(compositeDisposable)
            }
        }
    }
}
