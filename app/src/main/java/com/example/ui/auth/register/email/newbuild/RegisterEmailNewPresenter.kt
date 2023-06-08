package com.example.ui.auth.register.email.newbuild

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.*
import com.example.util.Utils.validatePhoneBeforeSend
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class RegisterEmailNewPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<RegisterEmailNewContract.View>(authRepository, snAuthManager, appData),
    RegisterEmailNewContract.Presenter {

    private var firstName: String = ""
    private var lastName: String = ""
    private var middleName: String = ""
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY
    private var email: String = ""
    private var password: String = ""
    private var passwordConfirm: String = ""

    private var isAgree: Boolean = false
    private var isPasswordValid: Boolean = false
    private var loginType = "email"

    private val deviceId = appData.deviceId
    private val deviceModel = getDeviceName()
    private val appVersion = getAppVersion()
    private val appCode = getAppVersionCode()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        performDataChange()
    }


    override fun onClickRegister() {
        if (isDataValid(firstName, lastName, email, password, isAgree))
            checkPhoneEmailIsUnique(email)
        else showErrors()
    }

    override fun checkPhoneEmailIsUnique(email: String) {
        compositeDisposable += Completable.defer {
            if (loginType == "email") userRepository.checkEmailPhone(email, null)
            else userRepository.checkEmailPhone(null, validatePhoneBeforeSend(email))
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    if (loginType == "email") viewState.showEmailNotUnique(email)
                    else viewState.showPhoneNotUnique(email)
                },
                onComplete = {
                    register()
                })
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
        viewState.showEmailError(false)
        loginType = if (Utils.isPhone(email) && !Utils.isContainLetters(email)) "phone" else "email"
        performDataChange()
    }

    override fun onChangeFirstNameText(firstName: String) {
        this.firstName = firstName
        viewState.showFirstNameError(false)
        performDataChange()
    }

    override fun onClickAgree(isAgree: Boolean) {
        this.isAgree = isAgree
        viewState.showAgreementError(false)
        performDataChange()
    }

    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
        viewState.showLastNameError(false)
        performDataChange()
    }

    override fun onChangeMiddleNameText(middleName: String) {
        this.middleName = middleName
        performDataChange()
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
        performDataChange()
    }


    override fun onChangePasswordText(password: String, isValid: Boolean) {
        this.password = password
        this.isPasswordValid = isValid
        viewState.showAgreementSelection(isPasswordValid)
        performDataChange()
    }

    override fun onChangePasswordConfirmText(password: String) {
        this.passwordConfirm = password
        performDataChange()
    }

    private fun performDataChange() {
        viewState?.apply {
            enableRegisterBtn(isDataValid(firstName, lastName, email, password, isAgree))
        }
    }


    override fun register() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += authRepository.registerUser(getRegisterBody())
            .andThen(Completable.defer {
                if (loginType == "email") authRepository.registerEmailResend(email)
                else authRepository.registerPhoneResend("personal", validatePhoneBeforeSend(email))
            })
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.setIgnoreTokenListener(false)
                    it.printStackTrace()
                },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)

                        showFinishRegister(
                            firstName,
                            lastName,
                            middleName,
                            email,
                            "code",
                            noMiddleNameChecked
                        )
                    }
                })

    }

    private fun getRegisterBody(): RegisterBody {
        val midName = if (middleName.isNullOrEmpty()) null
        else FieldDetails(value = middleName.removeAllDoubleSpaces())

        var phoneNumber: ArrayList<FieldDetails>? = null
        var em: FieldDetails? = null
        when (loginType) {
            "email" -> em = FieldDetails(value = email, isVisible = true)
            "phone" -> phoneNumber =
                FieldDetails(
                    value = validatePhoneBeforeSend(email),
                    type = PHONE_PERSONAL,
                    isVisible = true
                ).toList()
        }
        return RegisterBody(
            password = password,
            name = firstName.removeAllDoubleSpaces(),
            lastName = lastName.removeAllDoubleSpaces(),
            middleName = midName,
            email = em,
            phone = phoneNumber,
            deviceId = deviceId ?: "",
            deviceModel = deviceModel,
            build = appCode,
            version = appVersion
        )

    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }

    override fun onClickClose() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += Completable.fromAction {
            appData.isSubscribedToPush = false
            appData.logout()
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    setIgnoreTokenListener(false)
                    navigateUp()
                }
            }
    }

    private fun isDataValid(
        firstName: String?,
        lastName: String?,
        email: String?,
        password: String?,
        isAgree: Boolean
    ): Boolean {
        val firstNameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val passwordValid = password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
        val emailValid =
            if (loginType == "email") (AuthValidateUtil.isValidEmail(email.toString())) else Utils.newPhoneValidator(
                email ?: ""
            )
        val middleNameValid = if (noMiddleNameChecked) true else !middleName.isNullOrEmpty()
        return firstNameValid
                && lastNameValid
                && passwordValid
                && isPasswordValid
                && isAgree
                && emailValid
                && middleNameValid
    }

    private fun showErrors() {
        viewState.apply {
            showFirstNameError(firstName.isNullOrEmpty())
            showLastNameError(lastName.isNullOrEmpty())

            if (loginType == "email") showEmailError(AuthValidateUtil.isValidEmail(email))
            else showWrongPhoneError(Utils.isNewPhoneIsValid(email))

            showAgreementError(!isAgree)
        }
    }
}
