package com.example.ui.auth.register.invite

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.*
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.UserDetail
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.snAuth.SnAuthManager
import com.example.util.*
import com.shakebugs.shake.Shake
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class InviteRegisterPresenter
@Inject constructor(
    private val appData: AppData,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    snAuthManager: SnAuthManager
) : BaseAuthPresenter<InviteRegisterContract.View>(authRepository, snAuthManager, appData),
    InviteRegisterContract.Presenter {

    var firstName: String? = null
    var lastName: String? = null
    var middleName: String? = null
    var oldEmail: String? = null
    var newEmail: String? = null
    var invite: Int? = null
    var code: String = ""
    private var password: String? = null
    private var noMiddleNameChecked = middleName == USER_DATA_EMPTY || middleName.isNullOrEmpty()
    private var agreeWithPolicyChecked = false
    private var isPasswordValid: Boolean = false


    private var deviceId = appData.deviceId
    private var deviceModel = getDeviceName()
    private var appVersion = getAppVersion()
    private var appCode = getAppVersionCode()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setData(firstName, lastName, middleName, newEmail)
            setIgnoreTokenListener(true)
            performDataChange()
        }

        compositeDisposable += authRepository.authEmailOrPhone(getLoginBody())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.setIgnoreTokenListener(false) },
                onComplete = { viewState.setIgnoreTokenListener(false) }
            )
    }

    override fun onChangeFirstNameText(value: String) {
        firstName = value
        viewState.showFirstNameError(false)
        performDataChange()
    }

    override fun onChangeLastNameText(value: String) {
        lastName = value
        viewState.showLastNameError(false)
        performDataChange()
    }

    override fun onChangeMiddleNameText(value: String) {
        middleName = value
        performDataChange()
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
        performDataChange()
    }

    override fun onChangePasswordText(value: String?, isValid: Boolean) {
        password = value
        isPasswordValid = isValid
        performDataChange()
    }

    override fun onAgreeChecked(checked: Boolean) {
        agreeWithPolicyChecked = checked
        performDataChange()
    }

    override fun onClickRegister() {
        if (isDataValid()) register()
        else {
            viewState.apply {
                showEmailError(newEmail.isNullOrEmpty())
                showFirstNameError(firstName.isNullOrEmpty())
                showLastNameError(lastName.isNullOrEmpty())
            }
        }
    }

    private fun register() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += userRepository.changePassword(appData.getId(), getPasswordBody())
            .andThen(userRepository.updateProfile(appData.getId(), getUpdateBody()))
            .flatMapCompletable { authRepository.confirmEmailCode(getConfirmBody()) }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        if (newEmail != oldEmail) showEmailDialog(newEmail ?: "")
                        else openHome()
                    }
                })
    }


    private fun isDataValid(): Boolean {
        return !firstName.isNullOrBlank()
                && !lastName.isNullOrBlank()
                && if (noMiddleNameChecked) true else !middleName.isNullOrBlank()
                && newEmail?.let { AuthValidateUtil.isValidEmail(it) } ?: false
                && password?.let { AuthValidateUtil.isValidPassword(it) } ?: false
                && isPasswordValid
                && agreeWithPolicyChecked
    }


    override fun onClickClose() {
        viewState.setIgnoreTokenListener(true)
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(500)
            .doOnComplete {
                appData.isSubscribedToPush = false
                appData.logout()
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        loggedOut()
                    }
                },
                onComplete = {
                    viewState.apply {
                        setIgnoreTokenListener(false)
                        loggedOut()
                    }
                }
            )
    }

    private fun getUpdateBody(): Map<String, Any?> {
        val midName = if (middleName.isNullOrEmpty()) null
        else FieldDetails(value = middleName, absent = noMiddleNameChecked)

        return mapOf(
            UserDetail.USER_NAME to firstName,
            UserDetail.USER_LAST_NAME to lastName,
            UserDetail.USER_MIDDLE_NAME to midName,
            UserDetail.USER_EMAIL to FieldDetails(value = newEmail),
            UserDetail.USER_REGISTRATION_FINISH to true
        )
    }

    private fun getLoginBody(): AuthBody {
        return AuthBody(
            LoginModel("email", newEmail ?: ""),
            LoginModel("temporary", code),
            deviceId ?: "",
            deviceModel,
            appCode,
            appVersion
        )
    }

    private fun getPasswordBody(): PasswordBody = PasswordBody(password ?: "")
    private fun getConfirmBody(): EmailCodeBody = EmailCodeBody(code = code, email = newEmail ?: "")

    private fun confirmEmailRequest(): Completable {
        return if (newEmail == oldEmail) Completable.complete()
        else authRepository.confirmEmailCode(EmailCodeBody(code = code, email = newEmail ?: ""))
    }

    private fun performDataChange() = viewState.enableRegisterBtn(isDataValid())
    override fun onClickUserAgreement() = viewState.showUserAgreement()
    override fun onContinueWithSnRegistration(snUser: SnUser) {}
}