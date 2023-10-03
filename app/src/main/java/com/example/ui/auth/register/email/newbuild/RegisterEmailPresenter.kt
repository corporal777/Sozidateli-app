package com.example.ui.auth.register.email.newbuild

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.exceptions.EmailNotUniqueException
import com.example.exceptions.PhoneNotUniqueException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.*
import com.example.util.Utils.validatePhoneBeforeSend
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class RegisterEmailPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<RegisterEmailContract.View>(authRepository, snAuthManager, appData),
    RegisterEmailContract.Presenter {

    private var firstName: String = ""
    private var lastName: String = ""
    private var middleName: String = ""
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY
    private var email: String = ""
    private var password: String = ""
    private var isPasswordValid: Boolean = false
    private var isAgree: Boolean = false

    private var loginType = "email"

    private val deviceId = appData.deviceId
    private val deviceModel = getDeviceName()
    private val appVersion = getAppVersion()
    private val appCode = getAppVersionCode()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        performDataChange()
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


    private fun performDataChange() {
        viewState?.apply { enableRegisterBtn(isDataValid()) }
    }

    override fun onClickRegister() {
        if (isDataValid()) register(true)
        else showErrors()
    }

    override fun register(withCheck: Boolean) {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += checkPhoneEmailIsUnique(withCheck)
            .andThen(authRepository.registerUser(getRegisterBody()))
            .andThen(Completable.defer {
                if (loginType == "email") authRepository.registerEmailResend(email)
                else authRepository.registerPhoneResend("personal", validatePhoneBeforeSend(email))
            })
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = {
                    viewState.setIgnoreTokenListener(false)
                    when (it) {
                        is PhoneNotUniqueException -> viewState.showPhoneNotUnique(email)
                        is EmailNotUniqueException -> viewState.showEmailNotUnique(email)
                        else -> onReceiveError(it)
                    }
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

    private fun checkPhoneEmailIsUnique(withCheck: Boolean): Completable {
        return if (withCheck) Completable.create { emitter ->
            val disposable = CompositeDisposable()
            disposable += if (loginType == "email") {
                userRepository.checkEmailPhone(email, null)
                    .subscribeSimple(
                        onError = { emitter.onError(EmailNotUniqueException()) },
                        onComplete = { emitter.onComplete() })
            } else {
                userRepository.checkEmailPhone(null, validatePhoneBeforeSend(email))
                    .subscribeSimple(
                        onError = { emitter.onError(PhoneNotUniqueException()) },
                        onComplete = { emitter.onComplete() })
            }
            emitter.setDisposable(disposable)
        } else Completable.complete()
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

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val passwordValid = password.let { AuthValidateUtil.isValidPassword(it) }
        val emailValid =
            if (loginType == "email") (AuthValidateUtil.isValidEmail(email))
            else Utils.newPhoneValidator(email)

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
