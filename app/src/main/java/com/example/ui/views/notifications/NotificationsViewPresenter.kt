package com.example.ui.views.notifications

import com.example.data.AppData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class NotificationsViewPresenter @Inject constructor(
        private val appData: AppData
) : MvpPresenter<NotificationsViewContract.View>(), NotificationsViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showCounter(false)
        compositeDisposable += appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.showCounter(it > 0)
                }, {
                    viewState.showCounter(false)
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
