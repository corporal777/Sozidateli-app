package com.example.ui.auth.authorization

import android.content.Context
import android.util.Log
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.data.models.SnAuth
import com.example.ui.auth.snAuth.SnAuthCallbackHelper
import com.example.data.models.SnType
import com.example.data.models.SnUser
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class AuthorizationPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    private val appData: AppData
) : BaseAuthPresenter<AuthorizationContract.View>(authRepository, appData),
    AuthorizationContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += authRepository.getStories()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.setStories(it)
            }
    }


    override fun onLoginClick() {
        viewState.showLogin()
    }

    override fun onRegisterClick() {
        viewState.showRegistration()
    }

    override fun onAuthVkClick(context: Context) {
        compositeDisposable += SnAuthCallbackHelper.start(context, SnType.VK)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { sn ->
                    authRepository.authWithVk(sn.token, sn.uuid)
                        .performOnBackgroundOutOnMain()
                        .withLoading(1)
                        .subscribeSimple {
                            if (it.accessData != null && !it.accessData.token.isNullOrEmpty()){
                                appData.login(it.accessData.token)
                                appData.saveId(it.accessData.id)
                            } else viewState.showSnAuthorization(SnUser(sn, it.personalData))
                        }
                }
            )
    }

    private fun <T> Single<T>.withLoading(type: Int): Single<T> {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showCustomLoading(type) }
            .doOnDispose { viewState.hideCustomLoading(type) }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.hideCustomLoading(type)
            else loadingDisposable.dispose()
        }
        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.hideCustomLoading(type)
            else loadingDisposable.dispose()
        }
        return this.doOnDispose(actionHide).doOnError(actionConsumer())
    }

    override fun detachView(view: AuthorizationContract.View?) {
        viewState.hideAllLoadings()
        super.detachView(view)
    }

}
