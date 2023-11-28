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
import com.example.ui.snAuth.SnAuth
import com.example.ui.snAuth.SnAuthManager
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
import com.example.util.Utils
import com.example.util.Utils.isContainsNumbers
import com.example.util.Utils.isPhoneNumberValid
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject
import kotlin.math.abs

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
    private var isMiddleNameAbsent = middleName == USER_DATA_EMPTY
    private var mobilePhone: String? = ""
    private var password: String? = ""
    private var isPasswordValid: Boolean = false
    private var isAgree: Boolean = false

    private var scrollValue = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        performDataChange()
        viewState.changeAppBarHeader(abs(scrollValue / 10f))
    }

    override fun registerUser(withCheck: Boolean) {
        if (isDataValid()){
            compositeDisposable += checkPhoneIsUnique(withCheck)
                .andThen(authRepository.registerUser(getRegisterBody()))
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = {
                        if (it is PhoneNotUniqueException) viewState.showPhoneIsNotUnique(mobilePhone!!)
                        else onReceiveError(it)
                    },
                    onComplete = { viewState.showPhoneCodeConfirmation(mobilePhone!!) }
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
        val phoneValid = isPhoneNumberValid(mobilePhone)
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

            if (!isMiddleNameAbsent) {
                if (isContainsNumbers(middleName))
                    viewState.showMiddleNameError(true, "Отчество может содержать только буквы")
                else showMiddleNameError(middleName.isNullOrEmpty(), null)
            }
            showPasswordError(!isPasswordValid)
            showMobilePhoneError(!isPhoneNumberValid(mobilePhone))
            showUserAgreementError(!isAgree)
        }
    }

    private fun checkPhoneIsUnique(withCheck: Boolean): Completable {
        return if (withCheck) Completable.create { emitter ->
            val disposable = CompositeDisposable()
            disposable += userRepository.checkEmailPhone(null, mobilePhone)
                .subscribeSimple(
                    onError = { emitter.onError(PhoneNotUniqueException()) },
                    onComplete = { emitter.onComplete() })
            emitter.setDisposable(disposable)
        } else Completable.complete()
    }

    private fun getRegisterBody(): RegisterBody {
        val midName = if (middleName.isNullOrEmpty()) null
        else FieldDetails(value = middleName?.removeAllDoubleSpaces())

        return RegisterBody(
            password = password,
            name = firstName?.removeAllDoubleSpaces(),
            lastName = lastName?.removeAllDoubleSpaces(),
            middleName = midName,
            phone = FieldDetails(value = mobilePhone, type = PHONE_PERSONAL, isVisible = true).toList(),
            deviceId = appData.deviceId ?: "",
            deviceModel = getDeviceName(),
            build = getAppVersionCode(),
            version = getAppVersion()
        )
    }

    override fun onScrollChange(value: Int) {
        scrollValue = value
        viewState.changeAppBarHeader(abs(scrollValue / 10f))
    }


    override fun onContinueWithSnRegistration(SnAuth: SnAuth) {
    }


}
