package com.example.ui.auth.register.email

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RegisterEmailPresenter
@Inject constructor(
        private val authRepository: AuthRepository,
        snAuthManager: SnAuthManager,
        appData: AppData
) : BaseAuthPresenter<RegisterEmailContract.View>(authRepository, snAuthManager, appData), RegisterEmailContract.Presenter {

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var passwordConfirm: String? = null
    private var isAgree: Boolean = false

    override fun attachView(view: RegisterEmailContract.View?) {
        super.attachView(view)
        initData()
    }

    private fun initData() {
        viewState.setData(email, firstName, lastName, password, passwordConfirm, isAgree)
        performDataChange()
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickUserAgreement() {
        viewState.showUserAgreement()
    }

    override fun onClickRegister(email: String?, firstName: String?, lastName: String?, password: String?, passwordConfirm: String?, isAgree: Boolean) {
        if (isDataValid(firstName, lastName, email, password, passwordConfirm, isAgree)) {
            register(email!!, firstName!!, lastName!!, password!!)
        } else {
            viewState.apply {
                showEmailError(email.isNullOrEmpty())
                showFirstNameError(firstName.isNullOrEmpty())
                showLastNameError(lastName.isNullOrEmpty())
                showPasswordError(password.isNullOrEmpty() || password.length < 6)
                showPasswordConfirmError(password != passwordConfirm)
                showAgreementError(!isAgree)
            }
        }
    }

    override fun onClickAgree(isAgree: Boolean) {
        this.isAgree = isAgree
        viewState.showAgreementError(false)
        performDataChange()
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
        viewState.enableRegisterBtn(isDataValid(firstName, lastName, email, password, passwordConfirm, isAgree))
    }

    private fun isDataValid(firstName: String?, lastName: String?, email: String?, password: String?, passwordConfirm: String?, isAgree: Boolean): Boolean {
        return !firstName.isNullOrBlank()
                && !lastName.isNullOrBlank()
                && email?.let { AuthValidateUtil.isValidEmail(it) } ?: false
                && password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
                && password == passwordConfirm
                && isAgree
    }

    private fun register(email: String, firstName: String, lastName: String, password: String) {
        compositeDisposable += authRepository.register(RegisterBody(password = password,
                name = firstName, lastName = lastName,
                email = FieldDetails(value = email, isVisible = true)))
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
