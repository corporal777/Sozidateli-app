package com.example.ui.auth.confirm

import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.ui.snAuth.SnAuth
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class EmailConfirmPresenter
@Inject constructor(
        private val authRepository: AuthRepository
) : BasePresenter<EmailConfirmContract.View>(), EmailConfirmContract.Presenter {

    var snAuth: SnAuth? = null
    lateinit var email: String

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.setEmail(email)
        startTimer()
    }

    override fun onResendClick() {
        val snAuth = this.snAuth
        val request = if (snAuth != null) {
            authRepository.registerSnResend(snAuth.snType.code, email, snAuth.token)
        } else {
            authRepository.registerEmailResend(email)
        }

        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    startTimer()
                }, {
                    it.printStackTrace()
                })
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

    override fun onCloseClick() {
        viewState.navigateUp()
    }

    companion object {
        private const val TIMER_SECONDS_COUNT = 30
    }
}
