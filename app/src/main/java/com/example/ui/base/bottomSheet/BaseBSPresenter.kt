package com.example.ui.base.bottomSheet

import com.examle.data.AppData
import io.reactivex.disposables.CompositeDisposable
import moxy.MvpPresenter

abstract class BaseBSPresenter<V : BaseBSContract.View>(appData: AppData) : MvpPresenter<V>(),
    BaseBSContract.Presenter {

    protected val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected open fun onReceiveError(error: Throwable) {
        error.printStackTrace()
        viewState.showRequestErrorMessage()
    }
}