package com.example.ui.auth.register.sn

import android.util.Log
import com.example.data.AppData
import com.example.data.bodies.MiddleNameBody
import com.example.data.bodies.RegisterBody
import com.example.data.bodies.SnRegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.ToggleStringModel
import com.example.exceptions.EmailNotUniqueException
import com.example.exceptions.PhoneNotUniqueException
import com.example.extensions.formatFromVkToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.extensions.removeAllDoubleSpaces
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class SnRegisterPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData
) : BasePresenter<SnRegisterContract.View>(appData), SnRegisterContract.Presenter {

    lateinit var snUser: SnUser

    private var lastName: String? = ""
    private var firstName: String? = ""
    private var middleName: String? = ""
    private var middleNameIsAbsent = false
    private var phone: String? = ""
    private var birthday: String? = ""
    private var email: String? = ""
    private var photo : String? = ""
    private var gender : String? = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        lastName = snUser.snUserData?.lastName
        firstName = snUser.snUserData?.name
        middleNameIsAbsent = true
        if (Utils.isPhoneNumberValid(snUser.snUserData?.phone)) phone = snUser.snUserData?.phone
        birthday = snUser.snUserData?.birthday?.formatFromVkToDefaultDate()
        email = snUser.snUserData?.email
        photo = snUser.snUserData?.photo
        gender = snUser.snUserData?.gender

        viewState.setUserData(
            lastName,
            firstName,
            middleName,
            middleNameIsAbsent,
            phone,
            email,
            birthday
        )
        performDataChange()
    }


    override fun onClickContinue(withCheck: Boolean) {
        if (!isDataValid()) showErrors()
        else compositeDisposable += checkEmailIsUnique(withCheck)
            .andThen(authRepository.registerSnUser(getRegisterBody()))
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = {
                    if (it is EmailNotUniqueException) viewState.showEmailIsNotUnique(email)
                    else onReceiveError(it)
                },
                onComplete = {
                    viewState.showEmailConfirmation(email!!)
                }
            )
    }

    override fun onChangeLastName(lastName: String) {
        this.lastName = lastName
        performDataChange()
        viewState.showLastNameError(false)
    }

    override fun onChangeFirstName(firstName: String) {
        this.firstName = firstName
        performDataChange()
        viewState.showFirstNameError(false)
    }

    override fun onChangeMiddleName(middleName: String) {
        this.middleName = middleName
        performDataChange()
        viewState.showMiddleNameError(false)
    }

    override fun onMiddleNameIsAbsent(isAbsent: Boolean) {
        this.middleNameIsAbsent = isAbsent
        viewState.setMiddleNameAbsent(isAbsent)
        viewState.showMiddleNameError(false)
        performDataChange()
    }

    override fun onChangeMobilePhone(phone: String) {
        this.phone = phone
        performDataChange()
        viewState.showPhoneError(false)
    }

    override fun onChangeBirthday(birthday: String) {
        this.birthday = birthday
        performDataChange()
        viewState.showBirthdayError(false)
    }

    override fun onChangeEmail(email: String) {
        this.email = email
        performDataChange()
        viewState.showEmailError(false)
    }

    private fun performDataChange() = viewState.enableContinueButton(isDataValid())

    private fun showErrors() {
        viewState.apply {
            showFirstNameError(firstName.isNullOrBlank())
            showLastNameError(lastName.isNullOrBlank())
            if (!middleNameIsAbsent) showMiddleNameError(middleName.isNullOrBlank())
            showPhoneError(!Utils.isPhoneNumberValid(phone))
            showBirthdayError(birthday.isNullOrBlank())
            showEmailError(!Utils.isEmailValid(email))
        }
    }

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank() && !Utils.isContainsNumbers(firstName)
        val lastNameValid = !lastName.isNullOrBlank() && !Utils.isContainsNumbers(lastName)

        val middleNameValid = if (middleNameIsAbsent) true
        else !middleName.isNullOrBlank() && !Utils.isContainsNumbers(middleName)

        val phoneValid = Utils.isPhoneNumberValid(phone)
        val emailValid = Utils.isEmailValid(email)
        val birthdayValid = !birthday.isNullOrBlank()
        return firstNameValid && lastNameValid && middleNameValid && phoneValid
                && birthdayValid && emailValid
    }

    private fun checkEmailIsUnique(withCheck: Boolean): Completable {
        return if (withCheck) userRepository.checkEmailPhone(email, null)
            .onErrorResumeNext { Completable.error(EmailNotUniqueException()) }
        else Completable.complete()
    }

    private fun getRegisterBody(): SnRegisterBody {
        val midName = if (middleName.isNullOrEmpty()) MiddleNameBody(value = null, absent = true)
        else MiddleNameBody(value = middleName?.removeAllDoubleSpaces(), absent = false)

        return SnRegisterBody(
            uuid = snUser.snAuth.uuid,
            socialNetwork = snUser.snAuth.snType.code,
            name = firstName?.removeAllDoubleSpaces(),
            lastName = lastName?.removeAllDoubleSpaces(),
            middleName = midName,
            email = email,
            phone = phone,
            birthday = birthday?.formatToDefaultServerDate(),
            photo = photo,
            gender = ToggleStringModel(gender, true),
            deviceId = appData.deviceId ?: "",
            deviceModel = getDeviceName(),
            build = getAppVersionCode(),
            version = getAppVersion()
        )
    }

    companion object {
        private const val STEP_EMAIL = 0
        private const val STEP_PASSWORD = 1

        private const val ERROR_SENT_CONFIRM_EMAIL = "SENT_CONFIRM_EMAIL"
    }
}
