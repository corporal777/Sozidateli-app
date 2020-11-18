package com.example.ui.auth.register.invite

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.USER_DATA_EMPTY
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.rxkotlin.plusAssign
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class InviteRegisterPresenter
@Inject constructor(
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<InviteRegisterContract.View>(authRepository, snAuthManager), InviteRegisterContract.Presenter {

    private var firstName: String? = null
    private var lastName: String? = null
    private var middleName: String? = null
    private var email: String? = null
    private var newEmail: String? = null
    private var code: String = ""
    private var password: String? = null
    private var passwordConfirm: String? = null
    private var phone: String? = null
    private var phoneVerified: Boolean = false
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY

    override fun attachView(view: InviteRegisterContract.View?) {
        super.attachView(view)
    }

    override fun onSaveEmailText(email: String) {
        this.email = email
        this.newEmail = email
        viewState.showEmailError(false)
        performDataChange()
    }

    override fun onChangeEmailText(email: String) {
        this.newEmail = email
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

    override fun onChangePhoneText(phone: String) {
        viewState.apply {
            if (phoneVerified) {
                updatePhoneConfirmationStatus(this@InviteRegisterPresenter.phone == phone)
            }
            phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
            showWrongPhoneError(false)
        }
        this.phone = phone

        performDataChange()
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

    private fun register(
            email: String,
            firstName: String,
            lastName: String,
            password: String,
            middleName: String?,
            phone: String?
    ) {
        val newEm = if (this.email == email) null else email
        authRepository.registerConfirm(this.email?: "", code, firstName,
                lastName, middleName, phone, newEm, password)
                .performOnBackgroundOutOnMain()
                .subscribe({ if (newEm != null) viewState.showEmailDialog(newEm) }, { })
                .call(compositeDisposable)
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
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

    override fun onChangeMiddleNameText(middleName: String) {
        this.middleName = middleName
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

    private fun performDataChange() {
        viewState.enableRegisterBtn(isDataValid(
                firstName,
                lastName,
                newEmail,
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

    override fun onSaveCode(code: String) {
        this.code = code
        authRepository.registerData(email?: "", code)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    this.firstName = it.user?.user_name
                    this.lastName = it.user?.user_last_name
                    this.middleName = it.user?.user_middle_name
                    viewState.updateFieldsInUI(firstName?: "",
                            lastName?: "",
                            middleName?: "",
                            email?: "")
                }, {  })
                .call(compositeDisposable)
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickUserAgreement() {
        viewState.showUserAgreement()
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {

    }
}