package com.example.ui.auth.confirm

import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.register.email.finish.FinishRegisterPresenter.Companion.TIMER_SECONDS_COUNT
import com.example.ui.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Predicate
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class EmailConfirmPresenter
@Inject constructor(
        private val authRepository: AuthRepository,
        appData: AppData
) : BasePresenter<EmailConfirmContract.View>(appData), EmailConfirmContract.Presenter {

    var snUser: SnUser? = null
    lateinit var email: String
    lateinit var password: String

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        startTimer()
        onResendClick()
        //checkConfirmed()
    }

    override fun onResendClick() {
        val snAuth = this.snUser?.snAuth
        /*val request = if (snAuth != null) {
            authRepository.registerSnResend(email, snAuth.token)
        } else {*/
        val request = authRepository.registerEmailResend(email)
        //}

        compositeDisposable += request
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple { startTimer() }
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.apply {
            setCanResend(false)
            setTimeLeft(TIMER_SECONDS_COUNT)
        }

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                    if (timeLeft < 0) {
                        timerCompositeDisposable.clear()
                        viewState.setCanResend(true)
                    } else {
                        viewState.setTimeLeft(timeLeft)
                    }
                }, {
                    it.printStackTrace()
                })
    }

    private fun checkConfirmed() {
        compositeDisposable += Single.timer(5, TimeUnit.SECONDS)
                .flatMap { authRepository.checkRegisterStatus(snUser?.snAuth?.snType?.code, snUser?.snUserData?.id, email) }
                .map { it.user_by_email_confirmed_email || it.user_by_social_confirmed_email }
                .doOnSuccess { if (!it) throw Throwable() }
                .retry(Predicate { true })
                .flatMapCompletable {
                    val snUser = this.snUser
                    if (snUser != null) authRepository.authSocialNetwork(snUser.snAuth.snType.code, snUser.snAuth.token)
                    else authRepository.authEmailOrPhone(AuthBody(LoginModel(email, "email"), LoginModel(password, "common")))
                }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {}
    }

    override fun onCloseClick() {
        viewState.navigateUp()
    }
}
