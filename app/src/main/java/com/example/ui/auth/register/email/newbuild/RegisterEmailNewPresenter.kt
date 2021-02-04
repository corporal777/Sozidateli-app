package com.example.ui.auth.register.email.newbuild

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
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
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<RegisterEmailNewContract.View>(authRepository, snAuthManager), RegisterEmailNewContract.Presenter {

    private var firstName: String? = null
    private var lastName: String? = null
    private var middleName: String? = null
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY
    private var email: String? = null
    private var password: String? = null
    private var passwordConfirm: String? = null
    private var phone: String? = null
    private var phoneVerified: Boolean = false

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
                phoneVerified
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
            passwordConfirm: String?
    ) {
        if (isDataValid(firstName, lastName, email, password, passwordConfirm)) {
            register(
                    email!!,
                    firstName!!,
                    lastName!!,
                    password!!,
                    if (noMiddleNameChecked) USER_DATA_EMPTY else middleName,
                    phone
            )
        } else {
            viewState.apply {
                showEmailError(email.isNullOrEmpty())
                showFirstNameError(firstName.isNullOrEmpty())
                showLastNameError(lastName.isNullOrEmpty())
                showPasswordError(password.isNullOrEmpty() || password.length < 6)
                showPasswordConfirmError(password != passwordConfirm)
            }
        }
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
        viewState.showEmailError(false)
        performDataChange()
    }

    override fun onChangeFirstNameText(firstName: String) {
        this.firstName = firstName
        viewState.showFirstNameError(false)
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

    override fun onChangePasswordConfirmText(password: String) {
        this.passwordConfirm = password
        viewState.showPasswordConfirmError(this.password != password)
        performDataChange()
    }

    override fun onChangePhoneText(phone: String) {
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
                passwordConfirm
        ))
    }

    private fun isDataValid(
            firstName: String?,
            lastName: String?,
            email: String?,
            password: String?,
            passwordConfirm: String?
    ): Boolean {
        return !firstName.isNullOrBlank()
                && !lastName.isNullOrBlank()
                && email?.let { AuthValidateUtil.isValidEmail(it) } ?: false
                && password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
                && password == passwordConfirm
    }

    private fun register(
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

        val phoneNumber = if (phone.isNullOrEmpty())
            null
        else
            arrayListOf(FieldDetails(value = phone.replace(" ", ""), type = PHONE_PERSONAL, isVisible = true))

        compositeDisposable += authRepository.register(RegisterBody(password = password,
                name = firstName, lastName = lastName, middleName = midName,
                email = FieldDetails(value = email, isVisible = true), phone = phoneNumber))
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    viewState.showEmailConfirmation(email, password)
                }
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }
}
