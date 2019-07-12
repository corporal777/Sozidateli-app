package com.example.ui.views.accountView

import call
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class AccountViewPresenter @Inject constructor(
        private val appData: AppData
) : MvpPresenter<AccountViewContract.View>(), AccountViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showCounter(false)
        appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.apply {
                        if (it > 0) {
                            setCount(if (it > 99) "99+" else it.toString())
                            showCounter(true)
                        } else {
                            setCount("")
                            showCounter(false)
                        }
                    }
                }, {
                    viewState.showCounter(false)
                })
                .call(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
