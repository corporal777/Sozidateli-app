package com.example.ui.auth.welcome

import com.example.data.AppData
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class WelcomePresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<WelcomeContract.View>(appData), WelcomeContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val name = appData.getUser().name?: ""
        viewState.setUserName(name)
    }
}
