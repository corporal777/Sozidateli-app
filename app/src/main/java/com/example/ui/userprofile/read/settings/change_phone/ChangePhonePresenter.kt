package com.example.ui.userprofile.read.settings.change_phone

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.register.email.finishregister.FinishRegisterPresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.phoneToServer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ChangePhonePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BaseBottomSheetPresenter<ChangePhoneContract.View>(appData),
    ChangePhoneContract.Presenter {

    var mobilePhone: String = ""
    var oldMobilePhone: String = ""
    var isConfirmed = false
    var isVisible = false
    var phoneField: FieldDetails? = null

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.apply {
            setUserPhone(mobilePhone)
            setUserPhoneIsConfirmed(isConfirmed)
            setUserPhoneIsVisible(isVisible)
        }
    }


    override fun setNewPhone(phone: String) {
        this.mobilePhone = phone
    }

    override fun setNewPhoneIsConfirmed() {
        if (isConfirmed) {
            this.isConfirmed = mobilePhone == oldMobilePhone
            viewState.setUserPhoneIsConfirmed(isConfirmed)
        }
    }

    override fun setNewPhoneIsVisible(isVisible: Boolean) {
        this.isVisible = isVisible
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone, ConfirmType.SAVE)
                },
                onComplete = {
                    updatePhoneData()
                })
    }

    override fun updatePhoneData() {
        if (phoneField?.isConfirmed == true) {
            onSendCodeClick()
        } else {
            compositeDisposable += userRepository.updateUserProfile(
                appData.getId(),
                getDataToSave()
            )
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        onReceiveError(it)
                    },
                    onSuccess = {
                        viewState.setPhoneIsUpdatedSuccessfully()
                    })
        }
    }

    override fun onConfirmPhoneClick(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone, ConfirmType.CONFIRM)
                },
                onComplete = {
                    onSendCodeClick()
                })
    }

    override fun onConfirmCodeClick(code: String) {
        compositeDisposable += authRepository.confirmPhone(
            ConfirmCodeBody(
                "personal",
                mobilePhone ?: "",
                code ?: ""
            )
        )
            .doOnComplete { this.isConfirmed = true }
            .andThen(
                userRepository.updateUserProfile(
                    appData.getId(),
                    getDataToSave()
                )
            )
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.setPhoneIsUpdatedSuccessfully()
            }
    }


    override fun onSendCodeClick() {
        compositeDisposable += authRepository.registerPhoneResend("personal", mobilePhone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onComplete = {
                    viewState.showConfirmPhoneDialog(mobilePhone)
                })
    }


    override fun startTimerForResendCode(phone: String) {
        timerCompositeDisposable.clear()
        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft =
                    FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
                viewState.setTimerForResendConfirmCode(timeLeft)
                if (timeLeft < 0) {
                    timerCompositeDisposable.clear()
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mapOf(
            USER_PHONE to arrayListOf(
                FieldDetails(
                    value = mobilePhone.phoneToServer(),
                    type = phoneField?.type,
                    isVisible = isVisible,
                    isConfirmed = isConfirmed,
                    absent = phoneField?.absent,
                    onConfirmation = phoneField?.onConfirmation,
                    additional = phoneField?.additional

                )
            )
        )
    }

    enum class ConfirmType {
        CONFIRM, SAVE
    }

}