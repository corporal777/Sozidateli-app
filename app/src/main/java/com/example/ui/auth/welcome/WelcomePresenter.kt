package com.example.ui.auth.welcome

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.prefs.AppPrefs
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class WelcomePresenter
@Inject constructor(
        private val appPrefs: AppPrefs
) : BasePresenter<WelcomeContract.View>(), WelcomeContract.Presenter {

    override fun attachView(view: WelcomeContract.View?) {
        super.attachView(view)
        Completable.fromAction { appPrefs.userToken = "testToken" }
                .delay(3, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe { viewState.showMain() }
                .call(compositeDisposable)
    }
}
