package com.example.ui.main

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
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
                viewState.initWithAuth()
            } else {
                loadUser()
            }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.addOnTokenChangeListener(tokenChangeListener)
        viewState.apply {
            if (appData.token == null) {
                initWithAuth()
            } else {
                loadUser(onLoad = {
                    if (it.default_event != null) initWithEvent()
                    else initWithEventList()
                },
                        onError = {
                            appData.token = null
                        })
            }
        }
    }

    private fun loadUser(onLoad: ((User) -> Unit)? = null, onError: ((Throwable) -> Unit)? = null) {
        userRepository.getUser()
                .performOnBackgroundOutOnMain()
                .subscribe({ onLoad?.invoke(it) }, { onError?.invoke(it) })
                .call(compositeDisposable)
    }

    override fun onOpenStartDestination() = viewState.showBackButton(false)

    override fun onOpenNotStartDestination() = viewState.showBackButton(true)

    override fun onDestroy() {
        super.onDestroy()
        appData.removeOnTokenChangeListener(tokenChangeListener)
    }
}
