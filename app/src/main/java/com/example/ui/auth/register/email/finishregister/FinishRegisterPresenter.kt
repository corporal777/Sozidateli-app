package com.example.ui.auth.register.email.finishregister

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.UserDetail.Companion.USER_EMAIL
import com.example.data.models.UserDetail.Companion.USER_LAST_NAME
import com.example.data.models.UserDetail.Companion.USER_MIDDLE_NAME
import com.example.data.models.UserDetail.Companion.USER_NAME
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.PHONE_PERSONAL
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class FinishRegisterPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil,
        private val userRepository: UserRepository,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<FinishRegisterContract.View>(authRepository, snAuthManager), FinishRegisterContract.Presenter {

    private var firstName: String? = null
    private var defFirstName: String? = null
    private var lastName: String? = null
    private var defLastName: String? = null
    private var email: String? = null
    private var middleName: String? = null
    private var defMiddleName: String? = null
    var phone: String? = null
    private var isAgree: Boolean = true
    private var code: String = ""
    private var noAgreeChecked = false
    private var noMiddleNameChecked = false
    private var phoneCode: String? = null
    var loginType = "email"

    var snUser: SnUser? = null
    private var phoneVerified: Boolean = false
    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        compositeDisposable += appData.userPhoneConfirmedSubject
                .performOnBackgroundOutOnMain()
                .subscribeBy {
                    phoneVerified = it
                    viewState.updatePhoneConfirmationStatus(it)
                }
    }

    fun startTimer() {
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

    override fun attachView(view: FinishRegisterContract.View?) {
        super.attachView(view)
        initData()
    }

    private fun initData() {
        viewState.setData(email, firstName, middleName, lastName, phone, isAgree, phoneVerified)
        if (!phoneVerified)
            viewState.phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickUserAgreement() {
        viewState.showUserAgreement()
    }

    override fun onClickAgree(isAgree: Boolean) {
        //this.isAgree = isAgree
        //viewState.showAgreementError(false)
        viewState.enableRegisterBtn(true)
    }

    override fun getData() {
        val login = if (loginType == "email") email else phone
        compositeDisposable += authRepository.authEmailOrPhone(AuthBody(LoginModel(loginType, login?: ""), LoginModel("temporary", code)))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {

                        },
                        onComplete = {
                            compositeDisposable += userRepository.getUserShortData()
                                    .performOnBackgroundOutOnMain()
                                    .subscribe({
                                        firstName = it.name
                                        defFirstName = it.name
                                        lastName = it.lastName
                                        defLastName = it.lastName
                                        middleName = it.getMiddleName()
                                        defMiddleName = if (middleName.isNullOrEmpty()) "" else middleName
                                        viewState.setData(
                                                it.email?.value, it.name, it.middleName?.value,
                                                it.lastName, it.phone?.get(0)?.value, isAgree, false
                                        )
                                        viewState.unblockTokenListener()
                                    }, {  })
                        }
                )
    }

    override fun onChangePhoneText(phone: String) {
        viewState.apply {
            if (phoneVerified) {
                updatePhoneConfirmationStatus(this@FinishRegisterPresenter.phone == phone)
            }
            phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
        }
        this.phone = phone
    }

    override fun onChangeMiddleNameText(middleName: String) {
        this.middleName = middleName
    }

    override fun onChangeCodeText(code: String) {
        this.phoneCode = code
        if (code.length == 6) viewState.enableRegisterBtn(true) else viewState.enableRegisterBtn(false)
    }

    override fun authVk() {
        super.authVk()
    }

    override fun onSaveCode(code: String) {
        this.code = code
        viewState.setData(email, firstName, middleName, lastName, phone, isAgree, phoneVerified)
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
    }

    override fun phoneConfirmed(isConfirmed: Boolean) {
        //this.phoneVerified = isConfirmed
        //viewState.phoneConfirmEnabled(phoneVerified)
    }

    override fun onChangeNameText(name: String) {
        this.firstName = name
    }

    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
    }

    override fun sendCodeAgain() {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone?: "")
                .performOnBackgroundOutOnMain()
                .subscribe({
                    startTimer()
                    viewState.codeSuccess()
                }, { it.printStackTrace() })
    }

    override fun logout() {
        appData.isSubscribedToPush = false
        appData.logout()
        viewState.logedout()
    }

    override fun onHandleAuthLink() {

        when (loginType) {
            "phone" -> {
                compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone?: "", phoneCode?: ""))
                        .performOnBackgroundOutOnMain()
                        .subscribe({
                            userRepository.updateUserProfile(appData.getId(),
                                    mutableMapOf<String, Any>().apply {
                                        put(USER_PHONE, arrayListOf(FieldDetails(value = phone?.replace(" ", ""), type = PHONE_PERSONAL, isVisible = true, isConfirmed = true)))
                                        if (defFirstName != firstName) put(USER_NAME, firstName?: "")
                                        if (defLastName != lastName) put(USER_LAST_NAME, lastName?: "")
                                        if (defMiddleName != middleName) put(USER_MIDDLE_NAME, FieldDetails(value = middleName, absent = noMiddleNameChecked))
                                    }
                                    /*mapOf(USER_NAME to firstName,
                                    USER_LAST_NAME to lastName, USER_MIDDLE_NAME to FieldDetails(value = middleName, absent = noMiddleNameChecked),
                                    USER_PHONE to arrayListOf(FieldDetails(value = phone?.replace(" ", ""), type = PHONE_PERSONAL, isVisible = true, isConfirmed = true)))*/
                            )
                                    .performOnBackgroundOutOnMain()
                                    .withLoadingDialog(viewState)
                                    .subscribe({ viewState.openHome() }, { })
                                    .call(compositeDisposable)
                        }, {
                            viewState.codeError()
                        })
            }
            "email" -> {
                userRepository.confirmEmailCode(appData.getId(), EmailCodeBody(code = code, email = email?: ""))
                        .performOnBackgroundOutOnMain()
                        .subscribe({

                            userRepository.updateUserProfile(appData.getId(),
                                    mutableMapOf<String, Any>().apply {
                                        put(USER_EMAIL, FieldDetails(value = email, isVisible = true))
                                        if (defFirstName != firstName) put(USER_NAME, firstName?: "")
                                        if (defLastName != lastName) put(USER_LAST_NAME, lastName?: "")
                                        if (defMiddleName != middleName) put(USER_MIDDLE_NAME, FieldDetails(value = middleName, absent = noMiddleNameChecked))
                                    }
                                   /* mapOf(USER_EMAIL to FieldDetails(value = email, isVisible = true), USER_NAME to firstName,
                                    USER_LAST_NAME to lastName, USER_MIDDLE_NAME to FieldDetails(value = middleName, absent = noMiddleNameChecked))*/
                            )
                                    .performOnBackgroundOutOnMain()
                                    .withLoadingDialog(viewState)
                                    .subscribe({ viewState.openHome() }, { })
                                    .call(compositeDisposable)
                        }, {
                            viewState.codeError()
                        })
            }
        }
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }

    override fun onPhoneConfirmClick() {
        val phone = this.phone
        val phoneValid = phone.isValidPhoneNumber(phoneNumberUtil)
        viewState.apply {
            showWrongPhoneError(!phoneValid)
            if (phone != null) showPhoneConfirm(phone)
        }
    }

    companion object {
        const val TIMER_SECONDS_COUNT = 60
    }
}
