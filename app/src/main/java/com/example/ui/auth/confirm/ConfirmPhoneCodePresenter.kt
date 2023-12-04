package com.example.ui.auth.confirm

import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BaseContract
import com.example.ui.base.BasePresenter
import com.example.util.Utils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withDelay
import withInfinityCustomLoading
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ConfirmPhoneCodePresenter
@Inject constructor(
    val appData: AppData,
    val authRepository: AuthRepository,
) : BasePresenter<ConfirmPhoneCodeContract.View>(appData), ConfirmPhoneCodeContract.Presenter {

    var mobilePhone = ""
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
        onSendCallAgain()
    }

    override fun onConfirmMobilePhone() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += authRepository.confirmPhoneCode(ConfirmCodeBody(mobilePhone, code))
            .performOnBackgroundOutOnMain()
            .withLoading(1)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        showCodeError(true)
                    }
                },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        showHomeFragment()
                    }
                })
    }

    override fun onSendCallAgain() {
        compositeDisposable += authRepository.registerPhoneResend("personal", mobilePhone)
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
            setConfirmButton(code.length == 4)
        }
    }

    private fun formatMobilePhone(phone: String): String {
        return if (phone.length == 12) {
            StringBuilder(phone)
                .insert(2, " ")
                .insert(6, " ")
                .insert(10, " ")
                .insert(13, " ").toString()
        } else phone
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