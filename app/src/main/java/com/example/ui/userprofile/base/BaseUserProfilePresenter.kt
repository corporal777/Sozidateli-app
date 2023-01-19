package com.example.ui.userprofile.base

import androidx.annotation.CallSuper
import com.example.data.AppData
import com.example.data.models.Optional
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseUserProfilePresenter<V : BaseUserProfileContract.View>(
    private val appData: AppData
) : BasePresenter<V>(appData), BaseUserProfileContract.Presenter {

    protected val user: UserDetail
        get() = appData.getUserNew()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val updated = kotlin.runCatching { onUserUpdated(user) }.isSuccess

        compositeDisposable += appData.userNewChangeSubject
            .skip(if (updated) 1 else 0)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onNext = ::onUserUpdated
            )


    }

    private fun onUserUpdated(optionalUser: Optional<UserDetail>) {
        onUserUpdated(optionalUser.value)
    }

    protected open fun onUserUpdated(user: UserDetail?) {
        viewState.onUserUpdated(
            user,
            if (appData.hasMaxState && appData.hasBaseState) "Максимальный" else "Минимальный"
        )
    }

    protected fun updateUserInternal(update: UserDetail.() -> Unit) = appData.updateUserNew(update)
}
