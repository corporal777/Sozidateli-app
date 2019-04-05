package com.example.ui.base

import call
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.util.ApiErrorParser
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import javax.inject.Inject

open class BasePresenter<V : BaseContract.View>
@Inject constructor()
    : MvpPresenter<V>(), BaseContract.Presenter {

    protected val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var appData: AppData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onErrorHandlerListener.performOnBackgroundOutOnMain()
                .subscribe {
                    it.value?.let {
                        it.errors?.let { messages ->
                            onError(messages)
                        }
                    }
                }.call(compositeDisposable)
    }


    override fun onError(errors: List<String>) {
        viewState.showToast(errors.joinToString(separator = "\n"))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
