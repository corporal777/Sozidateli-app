package com.example.ui.userprofile.base

import androidx.annotation.CallSuper
import com.example.data.AppData
import com.example.data.models.Optional
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseUserProfilePresenter<V : BaseUserProfileContract.View>(
        private val appData: AppData
) : BasePresenter<V>(), BaseUserProfileContract.Presenter {

    protected val user: User
        get() = appData.getUser()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val updated = kotlin.runCatching { onUserUpdated(user) }.isSuccess

        compositeDisposable += appData.userChangeSubject
                .skip(if (updated) 1 else 0)
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                        onNext = ::onUserUpdated
                )
    }

    private fun onUserUpdated(optionalUser: Optional<User>) {
        onUserUpdated(optionalUser.value)
    }

    protected open fun onUserUpdated(user: User?) {
        viewState.onUserUpdated(user)
    }

    protected fun updateUserInternal(update: User.() -> Unit) = appData.updateUser(update)
}
