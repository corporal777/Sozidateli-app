package com.example.ui.userprofile.common.password.confirm

import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.Utils.formatMobilePhone
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class PhoneConfirmPasswordPresenter
@Inject constructor(
    val appData: AppData,
    val authRepository: AuthRepository,
) : BasePresenter<PhoneConfirmPasswordContract.View>(appData),
    PhoneConfirmPasswordContract.Presenter {

    var mobilePhone = ""
    var userId = ""
    private var code = ""
    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setConfirmButton(code.length == 4)
            setMobilePhone(formatMobilePhone(mobilePhone))
        }
        startTimer()
    }

    override fun onSendCallAgain() {
        compositeDisposable += authRepository.sendRecoveryEmail("phone", mobilePhone)
            .doOnSuccess { userId = it.userId }.ignoreElement()
            .performOnBackgroundOutOnMain()
            .withLoading(0)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { startTimer() }
            )
    }

    override fun onConfirmMobilePhone() {
        compositeDisposable += authRepository.checkPasswordRecoveryCode("phone", code)
            .performOnBackgroundOutOnMain()
            .withLoading(1)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showCodeError(true)
                },
                onComplete = {
                    viewState.showResetPasswordFragment(code, userId)
                })
    }



    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.apply {
            setCanCallAgain(false)
            setTimeLeft(TIMER_SECONDS_COUNT)
        }
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                viewState.setTimeLeft(timeLeft)
                if (timeLeft <= 0) {
                    timerCompositeDisposable.clear()
                    viewState.setCanCallAgain(true)
                }
            }
    }

    override fun onChangeCode(code: String) {
        this.code = code
        viewState.apply {
            showCodeError(false)
            setConfirmButton(code.length == 4)
        }
    }


    private fun Completable.withLoading(type: Int): Completable {
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
        return this.doFinally(actionHide)
            .doOnDispose(actionHide).doOnError(actionConsumer())
    }


    companion object {
        const val TIMER_SECONDS_COUNT = 60
    }
}