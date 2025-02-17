package com.example.ui.auth.register

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.exceptions.PhoneNotUniqueException
import com.example.extensions.*
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
import io.reactivex.disposables.CompositeDisposable
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

    var firstName: String? = ""
    var lastName: String? = ""
    var middleName: String? = ""
    private var isMiddleNameAbsent = middleName == USER_DATA_EMPTY
    private var login: String? = ""
    private var birthday: String? = ""
    private var password: String? = ""
    private var isPasswordValid: Boolean = false
    private var isAgree: Boolean = false


    override fun attachView(view: UserRegistrationContract.View?) {
        super.attachView(view)
        viewState.setData(lastName, firstName, middleName, isMiddleNameAbsent, login, birthday, password, isAgree)
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
                    onSuccess = { viewState.showPhoneConfirmation(login!!, it) }
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
        val phoneValid = isPhoneNumberValid(login)
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
            showLoginError(!isPhoneNumberValid(login))
            showBirthdayError(birthday.isNullOrBlank())
            showUserAgreementError(!isAgree)
        }
    }

    private fun checkPhoneIsUnique(withCheck: Boolean): Completable {
        return if (withCheck) Completable.defer {
            userRepository.checkEmailPhone(null, validatePhoneBeforeSend(login!!))
        }.onErrorResumeNext { Completable.error(PhoneNotUniqueException()) }
        else Completable.complete()
    }

    private fun getRegisterBody(): RegisterBody {
        val midName = if (middleName.isNullOrEmpty()) FieldDetails(value = null, absent = true)
        else FieldDetails(value = middleName?.removeAllDoubleSpaces(), absent = false)

        return RegisterBody(
            password = password,
            name = firstName?.removeAllDoubleSpaces(),
            lastName = lastName?.removeAllDoubleSpaces(),
            middleName = midName,
            phone = FieldDetails(validatePhoneBeforeSend(login!!), PHONE_PERSONAL).toList(),
            birthday = FieldDetails(value = birthday?.formatToDefaultServerDate()),
            deviceId = appData.deviceId ?: "",
            deviceModel = getDeviceName(),
            build = getAppVersionCode(),
            version = getAppVersion(),
            tempToken = appData.tempToken ?: ""
        )
    }
}
