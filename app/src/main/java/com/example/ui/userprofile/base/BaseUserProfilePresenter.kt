package com.example.ui.userprofile.base

import androidx.annotation.CallSuper
import com.example.data.AppData
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withLoadingDialog

abstract class BaseUserProfilePresenter<V : BaseUserProfileContract.View>(
        private val appData: AppData
) : BasePresenter<V>(), BaseUserProfileContract.Presenter {

    protected val user: User
        get() = appData.getUser()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                        },
                        onNext = {
                            viewState.onUserUpdated(it.value)
                        }
                )
    }

    protected fun updateUser(update: User.() -> Unit) = appData.updateUser(update)
}
