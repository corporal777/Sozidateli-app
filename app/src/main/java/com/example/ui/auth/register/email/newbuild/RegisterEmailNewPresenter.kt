package com.example.ui.auth.register.email.newbuild

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
import com.example.util.Utils
import com.example.util.Utils.validatePhoneBeforeSend
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RegisterEmailNewPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil,
        private val context: Context,
        private val userRepository: UserRepository,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<RegisterEmailNewContract.View>(authRepository, snAuthManager, appData), RegisterEmailNewContract.Presenter {

    private var firstName: String? = null
    private var lastName: String? = null
    private var middleName: String? = null
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY
    private var email: String? = null
    private var emailAgain: String? = null
    private var password: String? = null
    private var passwordConfirm: String? = null
    private var phone: String? = null
    private var phoneVerified: Boolean = false
    private var isAgree: Boolean = false
    private var isPasswordValid: Boolean = false
    var loginType = "email"

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userPhoneConfirmedSubject
                .performOnBackgroundOutOnMain()
                .subscribeBy {
                    phoneVerified = it
                    //viewState.updatePhoneConfirmationStatus(it)
                }
    }

    override fun attachView(view: RegisterEmailNewContract.View?) {
        super.attachView(view)
        initData()
    }

    private fun initData() {
        viewState.setData(
                email,
                firstName,
                lastName,
                if (noMiddleNameChecked) null else middleName,
                noMiddleNameChecked,
                password,
                passwordConfirm,
                phone,
                phoneVerified,
                isAgree
        )
        if (!phoneVerified)
            viewState.phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
        performDataChange()
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickRegister(
            email: String?,
            firstName: String?,
            lastName: String?,
            password: String?,
            //passwordConfirm: String?,
            isAgree: Boolean
    ) {
        if (isDataValid(firstName, lastName, email, password, /*passwordConfirm,*/ isAgree)) {
            checkPhoneEmailIsUnique(
                    email!!,
                    firstName!!,
                    lastName!!,
                    password!!,
                    if (noMiddleNameChecked) USER_DATA_EMPTY else middleName,
                    phone
            )
            /*register(
                    email!!,
                    firstName!!,
                    lastName!!,
                    password!!,
                    if (noMiddleNameChecked) USER_DATA_EMPTY else middleName,
                    phone
            )*/
        } else {
            viewState.apply {
                showEmailError(email.isNullOrEmpty())
                showFirstNameError(firstName.isNullOrEmpty())
                showLastNameError(lastName.isNullOrEmpty())
                showPasswordError(password.isNullOrEmpty() || password.length < 6)
                showPasswordConfirmError(password != passwordConfirm)
                if (loginType == "email")
                    showEmailAgainError(email != emailAgain)
                showAgreementError(!isAgree)
            }
        }
    }

    override fun checkPhoneEmailIsUnique(email: String, firstName: String, lastName: String, password: String, middleName: String?, phone: String?) {
        when (loginType) {
            "email" -> {
                compositeDisposable += userRepository.checkEmailPhone(email, null)
                        .withCheckInternetConnectivity()
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribe({ register(email, firstName, lastName, password, middleName, phone) },
                                { viewState.showEmailNotUnique(email, firstName, lastName, password, middleName, phone) })
            }
            "phone" -> {
                compositeDisposable += userRepository.checkEmailPhone(null, validatePhoneBeforeSend(email))
                        .withCheckInternetConnectivity()
                        .performOnBackgroundOutOnMain()
                        .withLoadingDialog(viewState)
                        .subscribe({ register(email, firstName, lastName, password, middleName, phone) },
                                { viewState.showPhoneNotUnique(email, firstName, lastName, password, middleName, email) })
            }
        }
    }

    override fun onChangeEmailText(email: String, context: Context) {
        this.email = email
        viewState.showEmailError(false)
        if (Utils.isPhone(email) && !Utils.isContainLetters(email)) {
            loginType = "phone"
            Utils.newPhoneValidator(email)
            viewState.changeFieldType(loginType, false)
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(email)
            viewState.changeFieldType(loginType, AuthValidateUtil.isValidEmail(email))
        }
        //viewState.changeFieldType(loginType)
        performDataChange()
    }

    override fun onChangeEmailAgainText(email: String) {
        this.emailAgain = email
        viewState.showEmailAgainError(this.emailAgain != email)
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

    override fun onChangePasswordText(password: String) {
        this.password = password
        viewState.showPasswordError(password.isNotEmpty() && password.length < 6)
        if (!passwordConfirm.isNullOrBlank()) viewState.showPasswordConfirmError(password != passwordConfirm)
        performDataChange()
    }

    override fun onChangeNewPasswordText(password: String, isValid: Boolean) {
        this.password = password
        this.isPasswordValid = isValid
        performDataChange()
    }

    override fun onChangePasswordConfirmText(password: String) {
        this.passwordConfirm = password
        viewState.showPasswordConfirmError(this.password != password)
        performDataChange()
    }

    override fun onChangePhoneText(phone: String, context: Context) {
        viewState.apply {
            if (phoneVerified) {
                updatePhoneConfirmationStatus(this@RegisterEmailNewPresenter.phone == phone)
            }
            phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
            showWrongPhoneError(false)
        }
        this.phone = phone

        performDataChange()
    }

    override fun onPhoneConfirmClick() {
        val phone = this.phone
        val phoneValid = phone.isValidPhoneNumber(phoneNumberUtil)
        viewState.apply {
            showWrongPhoneError(!phoneValid)
            if (phone != null) showPhoneConfirm(phone)
        }
    }

    private fun performDataChange() {
        viewState.enableRegisterBtn(isDataValid(
                firstName,
                lastName,
                email,
                password,
                //passwordConfirm,
                isAgree
        ))
    }

    private fun isDataValid(
            firstName: String?,
            lastName: String?,
            email: String?,
            password: String?,
            //passwordConfirm: String?,
            isAgree: Boolean
    ): Boolean {
        val firstNameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val passwordValid = password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
        val emailValid = if (loginType == "email") (AuthValidateUtil.isValidEmail(email.toString()) && (email == emailAgain)) else Utils.newPhoneValidator(email?: "")
        val middleNameValid = if (noMiddleNameChecked) true else !middleName.isNullOrEmpty()
        return firstNameValid
                && lastNameValid
                //&& email?.let { AuthValidateUtil.isValidEmail(it) } ?: false
                && passwordValid
                && isPasswordValid//&& password == passwordConfirm
                && isAgree
                && emailValid
                && middleNameValid
    }

    override fun register(
            email: String,
            firstName: String,
            lastName: String,
            password: String,
            middleName: String?,
            phone: String?
    ) {
        val midName = if (middleName.isNullOrEmpty())
            null
        else
            FieldDetails(value = middleName)

        var phoneNumber: ArrayList<FieldDetails>? = null
        var em: FieldDetails? = null
        when (loginType) {
            "email" -> {
                em = FieldDetails(value = email, isVisible = true)
            }
            "phone" -> {
                phoneNumber = if (email.isNullOrEmpty())
                    null
                else
                    arrayListOf(FieldDetails(value = validatePhoneBeforeSend(email)/*email.replace(" ", "")*/, type = PHONE_PERSONAL, isVisible = true))
            }
        }

        compositeDisposable += authRepository.register(RegisterBody(password = password,
                name = firstName, lastName = lastName, middleName = midName,
                email = em, phone = phoneNumber))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    when (loginType) {
                        "email" -> {
                            authRepository.registerEmailResend(email)
                                    .performOnBackgroundOutOnMain()
                                    .withLoadingDialog(viewState)
                                    .subscribe({
                                        viewState.showEmailConfirmation(email, password)
                                    }, { it.printStackTrace() })
                        }
                        "phone" -> {
                            compositeDisposable += authRepository.registerPhoneResend("personal", validatePhoneBeforeSend(email))
                                    .performOnBackgroundOutOnMain()
                                    .withLoadingDialog(viewState)
                                    .subscribe({
                                        viewState.showFinishRegister(firstName,
                                                lastName, middleName,
                                                phoneNumber?.get(0)?.value, em?.value?: "", "code",
                                                false, middleName == USER_DATA_EMPTY,
                                                true)

                                    }, { it.printStackTrace() })
                        }
                    }
                }
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }
}
