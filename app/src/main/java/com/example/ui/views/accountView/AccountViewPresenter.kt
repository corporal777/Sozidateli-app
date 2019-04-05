package com.example.ui.views.accountView

import call
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class AccountViewPresenter @Inject constructor(
        private val appData:AppData
) : MvpPresenter<AccountViewContract.View>(), AccountViewContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setChatCount(0)
        appData.onUserChange.
                performOnBackgroundOutOnMain()
                .subscribe({
                    it.value?.let {
                        viewState.setChatCount(it.notification_unread)
                    }
                },{

                }).call(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onError(errors: List<String>) {

    }
}
