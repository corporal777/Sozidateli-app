package com.example.ui.auth.confirm.email

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.exceptions.CodeInvalidException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.confirm.phone.ConfirmPhoneCodeContract
import com.example.ui.base.BasePresenter
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
class ConfirmEmailCodePresenter
@Inject constructor(
    val appData: AppData,
    val authRepository: AuthRepository,
    val userRepository: UserRepository
) : BasePresenter<ConfirmEmailCodeContract.View>(appData), ConfirmEmailCodeContract.Presenter {

    var isFromRegistration = true
    var email = ""
    private var code = ""
    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }
    private var timeLeft = TIMER_SECONDS_COUNT
    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setConfirmButton(code.length == CODE_SIZE)
            setEmail(email)
        }
        onSendCodeAgain()
    }

    override fun attachView(view: ConfirmEmailCodeContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else {
            if (timeLeft <= 0) viewState.apply {
                setCanSendAgain(true)
                setTimeLeft(timeLeft)
            }
        }
    }

    override fun onConfirmEmail() {
        viewState.apply {
            if (isFromRegistration) setFinishRegister(true)
            else setIgnoreTokenListener(true)
        }
        compositeDisposable += actionConfirmCodeRequest()
            .andThen(actionAfterConfirmRequest())
            .performOnBackgroundOutOnMain()
            .withLoading(1)
            .subscribeSimple(
                onError = {
                    viewState.apply {
                        setFinishRegister(false)
                        setIgnoreTokenListener(false)
                        if (it is CodeInvalidException) showCodeError(true)
                        else onReceiveError(it)
                    }
                },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        if (!isFromRegistration) navigateUp()
                    }
                })
    }

    override fun onSendCodeAgain() {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .withLoading(0)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { startTimer() }
            )
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.apply {
            setCanSendAgain(false)
            setTimeLeft(TIMER_SECONDS_COUNT)
        }
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                timeLeft = TIMER_SECONDS_COUNT - (it.toInt() + 1)
                viewState.setTimeLeft(timeLeft)
                if (timeLeft <= 0) {
                    viewState.setCanSendAgain(true)
                    timerCompositeDisposable.clear()
                }
            }
    }

    override fun onChangeCode(code: String) {
        this.code = code
        viewState.apply {
            showCodeError(false)
            setConfirmButton(code.length == CODE_SIZE)
        }
    }


    private fun actionConfirmCodeRequest(): Completable {
        return authRepository.confirmEmailCode(EmailCodeBody(code, email))
            .onErrorResumeNext{ Completable.error(CodeInvalidException()) }
    }

    private fun actionAfterConfirmRequest(): Completable {
        return if (isFromRegistration) Completable.complete()
        else userRepository.getUserInternal().doOnSuccess { new ->
            appData.updateUser { this.email = new.email }
        }.ignoreElement()
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
        return this.let { if (type == 1) it else it.doFinally(actionHide) }
            .doOnDispose(actionHide).doOnError(actionConsumer())
    }


    companion object {
        const val TIMER_SECONDS_COUNT = 60
        const val CODE_SIZE = 6
    }
}