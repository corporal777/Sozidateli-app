package com.example.ui.auth.register

import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.extensions.removeAllDoubleSpaces
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.exceptions.PhoneNotUniqueException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
import com.example.util.Utils.isContainLetters
import com.example.util.Utils.isContainsNumbers
import com.example.util.Utils.isEmailValid
import com.example.util.Utils.isPhone
import com.example.util.Utils.isPhoneNumberValid
import com.example.util.Utils.validatePhoneBeforeSend
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class UserRegistrationPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : BasePresenter<UserRegistrationContract.View>(appData), UserRegistrationContract.Presenter {

    private var firstName: String? = ""
    private var lastName: String? = ""
    private var middleName: String? = ""
    private var isMiddleNameAbsent = middleName == USER_DATA_EMPTY
    private var login: String? = ""
    private var birthday: String? = ""
    private var password: String? = ""
    private var isPasswordValid: Boolean = false
    private var isAgree: Boolean = false
    private var loginType: String? = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        performDataChange()
    }

    override fun registerUser(withCheck: Boolean) {
        if (isDataValid()) {
            compositeDisposable += checkPhoneIsUnique(withCheck)
                .andThen(authRepository.registerUser(getRegisterBody()))
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = {
                        if (it is PhoneNotUniqueException) viewState.showPhoneIsNotUnique(login!!)
                        else onReceiveError(it)
                    },
                    onComplete = { viewState.showCodeConfirmation(login!!) }
                )
        } else showErrors()
    }


    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
        performDataChange()
        viewState.showLastNameError(false, null)
    }

    override fun onChangeFirstNameText(firstName: String) {
        this.firstName = firstName
        performDataChange()
        viewState.showFirstNameError(false, null)
    }

    override fun onChangeMiddleNameText(middleName: String) {
        this.middleName = middleName
        viewState.showMiddleNameError(false, null)
        performDataChange()
    }

    override fun onMiddleNameIsAbsent(checked: Boolean) {
        isMiddleNameAbsent = checked
        viewState.enableMiddleNameInput(!checked)
        performDataChange()
    }

    override fun onChangeLoginText(login: String) {
        this.login = login
        loginType = if (isPhone(login) && !isContainLetters(login)) "phone" else "email"
        viewState.showLoginError(false)
        performDataChange()
    }

    override fun onChangeBirthdayText(birthday: String) {
        this.birthday = birthday
        viewState.showBirthdayError(false)
        performDataChange()
    }

    override fun onChangePasswordText(password: String?, isValid: Boolean) {
        this.password = password
        this.isPasswordValid = isValid
        performDataChange()
    }

    override fun onChangeUserAgreement(isAgree: Boolean) {
        this.isAgree = isAgree
        viewState.showUserAgreementError(false)
        performDataChange()
    }

    fun onCheckLastNameValid() {
        if (isContainsNumbers(lastName))
            viewState.showLastNameError(true, "Фамилия может содержать только буквы")
    }

    fun onCheckFirstNameValid() {
        if (isContainsNumbers(firstName))
            viewState.showFirstNameError(true, "Имя может содержать только буквы")
    }

    fun onCheckMiddleNameValid() {
        if (!isMiddleNameAbsent) {
            if (isContainsNumbers(middleName))
                viewState.showMiddleNameError(true, "Отчество может содержать только буквы")
        }
    }

    private fun performDataChange() = viewState.enableRegisterBtn(isDataValid())

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank() && !isContainsNumbers(firstName)
        val lastNameValid = !lastName.isNullOrBlank() && !isContainsNumbers(lastName)

        val middleNameValid = if (isMiddleNameAbsent) true
        else !middleName.isNullOrBlank() && !isContainsNumbers(middleName)

        val passwordValid = !password.isNullOrBlank() && isPasswordValid
        val phoneValid =
            if (loginType == "phone") isPhoneNumberValid(login) else isEmailValid(login)
        val birthdayValid = !birthday.isNullOrBlank()
        return firstNameValid && lastNameValid && middleNameValid && phoneValid
                && birthdayValid && passwordValid && isAgree
    }

    private fun showErrors() {
        viewState.apply {
            if (isContainsNumbers(firstName)) {
                viewState.showFirstNameError(true, "Имя может содержать только буквы")
            } else showFirstNameError(firstName.isNullOrBlank(), null)

            if (isContainsNumbers(lastName))
                viewState.showLastNameError(true, "Фамилия может содержать только буквы")
            else showLastNameError(lastName.isNullOrBlank(), null)

            if (!isMiddleNameAbsent) {
                if (isContainsNumbers(middleName))
                    viewState.showMiddleNameError(true, "Отчество может содержать только буквы")
                else showMiddleNameError(middleName.isNullOrBlank(), null)
            }
            showPasswordError(!isPasswordValid)
            showLoginError(
                if (loginType == "phone") !isPhoneNumberValid(login) else !isEmailValid(
                    login
                )
            )
            showBirthdayError(birthday.isNullOrBlank())
            showUserAgreementError(!isAgree)
        }
    }

    private fun checkPhoneIsUnique(withCheck: Boolean): Completable {
        return if (withCheck) Completable.defer {
            if (loginType == "phone")
                userRepository.checkEmailPhone(null, validatePhoneBeforeSend(login!!))
            else userRepository.checkEmailPhone(login, null)
        }.onErrorResumeNext { Completable.error(PhoneNotUniqueException()) }
        else Completable.complete()
    }

    private fun getRegisterBody(): RegisterBody {
        val midName = if (middleName.isNullOrEmpty()) FieldDetails(value = null, absent = true)
        else FieldDetails(value = middleName?.removeAllDoubleSpaces(), absent = false)

        var phoneNumber: ArrayList<FieldDetails>? = null
        var email: FieldDetails? = null
        if (loginType == "phone")
            phoneNumber = FieldDetails(value = validatePhoneBeforeSend(login!!), type = PHONE_PERSONAL).toList()
        else email = FieldDetails(value = login)

        return RegisterBody(
            password = password,
            name = firstName?.removeAllDoubleSpaces(),
            lastName = lastName?.removeAllDoubleSpaces(),
            middleName = midName,
            email = email,
            phone = phoneNumber,
            birthday = FieldDetails(value = birthday?.formatToDefaultServerDate()),
            deviceId = appData.deviceId ?: "",
            deviceModel = getDeviceName(),
            build = getAppVersionCode(),
            version = getAppVersion(),
            tempToken = appData.tempToken ?: ""
        )
    }

    fun getLoginType() = loginType
}
