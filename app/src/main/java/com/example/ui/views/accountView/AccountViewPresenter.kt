package com.example.ui.views.accountView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class AccountViewPresenter @Inject constructor(
        private val appData: AppData
) : MvpPresenter<AccountViewContract.View>(), AccountViewContract.Presenter {

    override var canShowBadge: Boolean = true
        set(value) {
            field = value
            viewState.showCounter(value)
        }

    private val compositeDisposable = CompositeDisposable()

    private var userAvatar: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showCounter(false)
        compositeDisposable += appData.notificationsCountSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.apply {
                        if (it > 0) {
                            showCounter(canShowBadge)
                        } else {
                            showCounter(false)
                        }
                    }
                }, {
                    viewState.showCounter(false)
                })

        compositeDisposable += appData.userChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val avatar = it.value?.user_avatar
                    if (userAvatar != avatar) {
                        userAvatar = avatar
                        viewState.setAvatar(avatar)
                    }
                }, {
                    viewState.setAvatar(null)
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
