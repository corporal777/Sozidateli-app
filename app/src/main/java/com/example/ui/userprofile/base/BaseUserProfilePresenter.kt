package com.example.ui.userprofile.base

import androidx.annotation.CallSuper
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain

abstract class BaseUserProfilePresenter<V : BaseUserProfileContract.View>(
    private val appData: AppData
) : BasePresenter<V>(appData), BaseUserProfileContract.Presenter {

    val user: UserDetail
        get() = appData.getUser()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val updated = kotlin.runCatching { onUserUpdated(user) }.isSuccess

        compositeDisposable += appData.userChangeSubject
            .skip(if (updated) 1 else 0)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val user = it.value
                if (user != null) onUserUpdated(user)
            }
    }

    protected open fun onUserUpdated(user: UserDetail) {
        viewState.setUserData(user, appData.getStateValue())
    }
}
