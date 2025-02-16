package com.example.ui.auth.confirm.phone

import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.data.models.AuthResponse
import com.example.exceptions.CodeInvalidException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.confirm.email.ConfirmEmailCodeContract
import com.example.ui.auth.confirm.email.ConfirmEmailCodePresenter
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
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
import withInfinityCustomLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ConfirmPhoneCodePresenter
@Inject constructor(
    val appData: AppData,
    val authRepository: AuthRepository,
    val userRepository: UserRepository
) : BasePresenter<ConfirmPhoneCodeContract.View>(appData), ConfirmPhoneCodeContract.Presenter {

    var mobilePhone = ""
    var authResponse: AuthResponse? = null

    private val userId get() = authResponse?.id ?: appData.getId()
    private var code = ""

    private val timerCompositeDisposable = CompositeDisposable().apply {
        compositeDisposable += this
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setConfirmButton(code.length == CODE_SIZE)
            setMobilePhone(formatMobilePhone(mobilePhone))
        }
        onSendCallAgain()
    }

    override fun onConfirmMobilePhone() {
        compositeDisposable += actionConfirmCodeRequest()
            .andThen(actionAfterConfirmRequest())
            .performOnBackgroundOutOnMain()
            .withLoading(1)
            .subscribeSimple(
                onError = {
                    if (it is CodeInvalidException) viewState.showCodeError(true)
                    else onReceiveError(it)
                },
                onComplete = { if (authResponse == null) viewState.navigateUp() }
            )
    }

    override fun onSendCallAgain() {
        compositeDisposable += authRepository.registerPhoneResend(userId, mobilePhone)
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
            setConfirmButton(code.length == CODE_SIZE)
        }
    }

    private fun actionConfirmCodeRequest(): Completable {
        return authRepository.confirmPhoneCode(userId, ConfirmCodeBody(mobilePhone, code))
            .onErrorResumeNext { Completable.error(CodeInvalidException()) }
    }

    private fun actionAfterConfirmRequest(): Completable {
        return if (authResponse == null) {
            userRepository.getUserInternal()
                .doOnSuccess { new -> appData.updateUser { this.phone = new.phone } }
                .ignoreElement()
        } else {
            Completable.fromAction {
                viewState.setFinishRegister(true)
                appData.saveId(authResponse?.id)
                appData.login(authResponse?.token!!)
            }.doOnError { viewState.setFinishRegister(false) }
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
        return this.let { if (type == 1) it else it.doFinally(actionHide) }
            .doOnDispose(actionHide).doOnError(actionConsumer())
    }


    companion object {
        const val TIMER_SECONDS_COUNT = 60
        const val CODE_SIZE = 4
    }
}