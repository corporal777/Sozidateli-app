package com.example.ui.auth.register

import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.exceptions.EmailNotUniqueException
import com.example.exceptions.PhoneNotUniqueException
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.extensions.removeAllDoubleSpaces
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.auth.register.email.newbuild.RegisterEmailContract
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
import com.example.util.Utils
import com.example.util.Utils.isContainsNumbers
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class UserRegistrationPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<UserRegistrationContract.View>(authRepository, snAuthManager, appData),
    UserRegistrationContract.Presenter {

    private var firstName: String? = ""
    private var lastName: String? = ""
    private var middleName: String? = ""
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY
    private var mobilePhone: String? = ""
    private var password: String? = ""
    private var isPasswordValid: Boolean = false
    private var isAgree: Boolean = false


    private val deviceId = appData.deviceId
    private val deviceModel = getDeviceName()
    private val appVersion = getAppVersion()
    private val appCode = getAppVersionCode()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        performDataChange()
    }

    override fun registerUser() {
        if (isDataValid()){

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

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
        performDataChange()
    }

    override fun onChangeMobilePhoneText(phone: String) {
        this.mobilePhone = phone
        viewState.showMobilePhoneError(false)
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
        if (!noMiddleNameChecked) {
            if (isContainsNumbers(middleName))
                viewState.showMiddleNameError(true, "Отчество может содержать только буквы")
        }
    }

    private fun performDataChange() = viewState.enableRegisterBtn(isDataValid())

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank() && !isContainsNumbers(firstName)
        val lastNameValid = !lastName.isNullOrBlank() && !isContainsNumbers(lastName)

        val middleNameValid = if (noMiddleNameChecked) true
        else !middleName.isNullOrEmpty() && !isContainsNumbers(middleName)

        val passwordValid = !password.isNullOrBlank() && isPasswordValid
        val phoneValid = Utils.newPhoneValidator(mobilePhone)
        return firstNameValid && lastNameValid && middleNameValid && phoneValid && passwordValid && isAgree
    }

    private fun showErrors() {
        viewState.apply {
            if (isContainsNumbers(firstName)) {
                viewState.showFirstNameError(true, "Имя может содержать только буквы")
            } else showFirstNameError(firstName.isNullOrEmpty(), null)

            if (isContainsNumbers(lastName))
                viewState.showLastNameError(true, "Фамилия может содержать только буквы")
            else showLastNameError(lastName.isNullOrEmpty(), null)

            if (!noMiddleNameChecked) {
                if (isContainsNumbers(middleName))
                    viewState.showMiddleNameError(true, "Отчество может содержать только буквы")
                else showMiddleNameError(middleName.isNullOrEmpty(), null)
            }
            showMobilePhoneError(!Utils.newPhoneValidator(mobilePhone))
            showUserAgreementError(!isAgree)
        }
    }

    override fun onContinueWithSnRegistration(SnAuth: SnAuth) {
    }


}
