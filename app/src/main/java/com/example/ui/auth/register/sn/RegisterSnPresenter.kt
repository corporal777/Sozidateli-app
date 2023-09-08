package com.example.ui.auth.register.sn

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class RegisterSnPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    snAuthManager: SnAuthManager,
    appData: AppData
) : BaseAuthPresenter<RegisterSnContract.View>(authRepository, snAuthManager, appData),
    RegisterSnContract.Presenter {

    lateinit var snUser: SnUser

    private var email: String? = null
    private var password: String? = null
    private var passwordConfirm: String? = null
    private var isAgree: Boolean = false

    private var step = STEP_EMAIL

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        email = snUser.snUserData.email ?: snUser.snAuth.email
        viewState.apply {
            setUserData(
                snUser.snAuth.snType,
                "${snUser.snUserData.firstName} ${snUser.snUserData.lastName}",
                snUser.snUserData.avatar
            )
            setEmail(email)
            performDataChange()
        }
    }

    override fun attachView(view: RegisterSnContract.View?) {
        super.attachView(view)
        return when (step) {
            STEP_EMAIL -> viewState.setEmail(email)
            STEP_PASSWORD -> viewState.setPassword(password, passwordConfirm)
            else -> Unit
        }
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickUserAgreement() {
        viewState.showUserAgreement()
    }

    override fun onClickContinue() {
        if (!isDataValid()) return
        when (step) {
            STEP_EMAIL -> checkEmailRegistered()
            STEP_PASSWORD -> register()
        }
    }

    override fun onClickAgree(isAgree: Boolean) {
        this.isAgree = isAgree
        viewState.showAgreementError(false)
        performDataChange()
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
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
        viewState.enableContinueButton(isDataValid())
    }

    private fun isDataValid(): Boolean {
        return when (step) {
            STEP_EMAIL -> email?.let { AuthValidateUtil.isValidEmail(it) } ?: false
            STEP_PASSWORD ->
                password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
                        && password == passwordConfirm
                        && isAgree
            else -> false
        }
    }

    private fun checkEmailRegistered() {
        compositeDisposable += authRepository.checkRegisterStatus(null, null, email)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                if (it.user_by_email_found) {
                    register()
                } else {
                    step = STEP_PASSWORD
                    viewState.setPassword(password, passwordConfirm)
                    performDataChange()
                }
            }
    }

    private fun register() {
        val snUser = this.snUser
        val email = this.email ?: return
        val password = this.password
        val firstName = snUser.snUserData.firstName
        val lastName = snUser.snUserData.lastName
        val snType = snUser.snAuth.snType.code
        val snToken = snUser.snAuth.token
        compositeDisposable += authRepository.authSocialNetwork(
            snType,
            snToken,
            email,
            firstName,
            lastName,
            password
        )
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    if (it is ApiError && it.hasError(ERROR_SENT_CONFIRM_EMAIL)) {
                        viewState.showEmailConfirmation(email, snUser)
                    } else {
                        onReceiveError(it)
                    }
                },
                onComplete = {}
            )
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {

    }

    companion object {
        private const val STEP_EMAIL = 0
        private const val STEP_PASSWORD = 1

        private const val ERROR_SENT_CONFIRM_EMAIL = "SENT_CONFIRM_EMAIL"
    }
}
