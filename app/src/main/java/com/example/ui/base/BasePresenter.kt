package com.example.ui.base

import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

open class BasePresenter<V : BaseContract.View>
@Inject constructor()
    : MvpPresenter<V>(), BaseContract.Presenter {

    protected val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
