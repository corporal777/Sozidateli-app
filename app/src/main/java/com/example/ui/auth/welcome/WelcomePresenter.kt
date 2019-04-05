package com.example.ui.auth.welcome

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class WelcomePresenter
@Inject constructor() : BasePresenter<WelcomeContract.View>(), WelcomeContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUserName(appData.getUser().fullName)
    }
}
