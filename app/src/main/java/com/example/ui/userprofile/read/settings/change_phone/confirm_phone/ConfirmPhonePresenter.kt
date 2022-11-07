package com.example.ui.userprofile.read.settings.change_phone.confirm_phone

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.register.email.finishregister.FinishRegisterPresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneContract
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withProgressBarLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ConfirmPhonePresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository
) : BaseBottomSheetPresenter<ConfirmPhoneContract.View>(appData),
    ConfirmPhoneContract.Presenter {

    private val timerCompositeDisposable = CompositeDisposable()
    var mobilePhone = ""

    private var isFirstLaunch = true
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.showProgressLoading()
        sendCode()
    }

    override fun sendCode() {
        compositeDisposable += authRepository.registerPhoneResend("personal", mobilePhone)
            .performOnBackgroundOutOnMain()
            .let {
                if (!isFirstLaunch) it.withCustomProgressBarLoadingDialog(viewState)
                else it
            }
            .subscribeSimple {
                if (isFirstLaunch) {
                    viewState.hideProgressLoading()
                    viewState.setContentVisible(true)
                    isFirstLaunch = false
                }
                startTimerForResendCode()
            }
    }

    override fun startTimerForResendCode() {
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft =
                    FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
                viewState.setResendButtonEnable(timeLeft)
                if (timeLeft < 0) {
                    timerCompositeDisposable.clear()
                }
            }, {
                it.printStackTrace()
            })
    }

}