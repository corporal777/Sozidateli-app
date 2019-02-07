package com.example.ui.auth.welcome

import call
import com.arellomobile.mvp.InjectViewState
import com.example.ui.base.BasePresenter
import io.reactivex.Completable
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class WelcomePresenter
@Inject constructor() : BasePresenter<WelcomeContract.View>(), WelcomeContract.Presenter {

    override fun attachView(view: WelcomeContract.View?) {
        super.attachView(view)
        Completable.timer(3, TimeUnit.SECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe { viewState.showMain() }
                .call(compositeDisposable)
    }
}
