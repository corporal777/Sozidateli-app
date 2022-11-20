package com.example.ui.auth.register.email.finishregister.newbuild

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.EmailCodeBody
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.UserDetail
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.auth.register.email.finishregister.FinishRegisterPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.*
import com.example.util.Utils.validatePhoneBeforeSend
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class FinishRegisterNewPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val userRepository: UserRepository,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<FinishRegisterNewContract.View>(authRepository, snAuthManager, appData),
    FinishRegisterNewContract.Presenter {

    var firstName: String = ""
    var lastName: String = ""
    var middleName: String = ""
    var email: String = ""
    var phone: String = ""
    var noMiddleNameChecked = false
    private var code: String = ""
    var loginType = "email"
    private var deviceId = appData.deviceId
    private var deviceModel = getDeviceName()
    private var appVersion = getAppVersion()
    private var appCode = getAppVersionCode()

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.setData(email, phone, firstName, lastName, middleName)
        startTimer()
    }


    override fun attachView(view: FinishRegisterNewContract.View?) {
        super.attachView(view)
    }

    private fun startTimer() {
        timerCompositeDisposable.clear()
        viewState.apply {
            setCanResend(false)
            setTimeLeft(FinishRegisterPresenter.TIMER_SECONDS_COUNT)
        }

        timerCompositeDisposable += Observable.interval(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribe({
                val timeLeft = FinishRegisterPresenter.TIMER_SECONDS_COUNT - (it.toInt() + 1)
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


    override fun sendCodeAgain() {
        compositeDisposable += if (loginType == "email") {
            authRepository.registerEmailResend(email)
        } else {
            authRepository.registerPhoneResend(
                "personal",
                validatePhoneBeforeSend(phone)
            )
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                startTimer()
            }, { it.printStackTrace() })
    }

    override fun onHandleAuthLink() {
        viewState.showCustomProgressDialog()
        compositeDisposable += confirmCodeRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.apply {
                        hideCustomProgressDialog()
                        codeError()
                    }
                }, onComplete = {
                    userRepository.updateUserProfile(
                        appData.getId(),
                        getUpdateRequestBody()
                    )
                        .performOnBackgroundOutOnMain()
                        .subscribe({
                            viewState.apply {
                                hideCustomProgressDialog()
                                openHome()
                            }
                        }, {
                            viewState.hideCustomProgressDialog()
                            onReceiveError(it)
                        })
                        .call(compositeDisposable)
                })

    }

    override fun onChangeCodeText(code: String) {
        this.code = code
        viewState.enableRegisterBtn(code.length == 6)
    }

    override fun onChangeNameText(name: String) {
        this.firstName = name
    }

    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
    }

    override fun onChangeMiddleNameText(lastName: String) {
        this.middleName = lastName
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
    }


    override fun onClickClose() {
        viewState.navigateUp()
    }


    override fun logout() {
        appData.isSubscribedToPush = false
        appData.logout()
        viewState.logout()
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
    }

    private fun confirmCodeRequest(): Completable {
        return if (loginType == "email") {
            userRepository.confirmEmailCodeNew(
                appData.getId(),
                EmailCodeBody(code = code, email = email)
            )
        } else {
            authRepository.confirmPhone(
                ConfirmCodeBody(
                    "personal",
                    validatePhoneBeforeSend(phone),
                    code
                )
            )
        }
    }

    private fun getUpdateRequestBody(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            if (loginType == "email") {
                put(
                    UserDetail.USER_EMAIL,
                    FieldDetails(value = email, isVisible = true, isConfirmed = true)
                )
            } else {
                put(
                    UserDetail.USER_PHONE,
                    arrayListOf(
                        FieldDetails(
                            value = validatePhoneBeforeSend(phone),
                            type = PHONE_PERSONAL,
                            isVisible = true,
                            isConfirmed = true
                        )
                    )
                )
            }
            put(UserDetail.USER_NAME, firstName)
            put(UserDetail.USER_LAST_NAME, lastName)
            put(
                UserDetail.USER_MIDDLE_NAME,
                FieldDetails(value = middleName, absent = noMiddleNameChecked)
            )
            put(UserDetail.USER_REGISTRATION_FINISH, true)
        }
    }
}
