package com.example.ui.base.bottomSheet

import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.ui.base.BaseContract
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import moxy.MvpPresenter
import retrofit2.HttpException

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