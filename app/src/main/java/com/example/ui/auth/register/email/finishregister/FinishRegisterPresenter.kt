package com.example.ui.auth.register.email.finishregister

import android.content.Context
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.EmailCodeBody
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.SnUser
import com.example.data.models.UserDetail.Companion.USER_EMAIL
import com.example.data.models.UserDetail.Companion.USER_LAST_NAME
import com.example.data.models.UserDetail.Companion.USER_MIDDLE_NAME
import com.example.data.models.UserDetail.Companion.USER_NAME
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.base.BaseAuthPresenter
import com.example.ui.auth.register.email.RegisterEmailContract
import com.example.ui.auth.register.email.newbuild.RegisterEmailNewContract
import com.example.ui.snAuth.SnAuthManager
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.USER_DATA_EMPTY
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import isValidPhoneNumber
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import io.reactivex.functions.Predicate

@InjectViewState
class FinishRegisterPresenter
@Inject constructor(
        private val appData: AppData,
        private val authRepository: AuthRepository,
        private val phoneNumberUtil: PhoneNumberUtil,
        private val userRepository: UserRepository,
        snAuthManager: SnAuthManager
) : BaseAuthPresenter<FinishRegisterContract.View>(authRepository, snAuthManager), FinishRegisterContract.Presenter {

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var middleName: String? = null
    private var phone: String? = null
    private var isAgree: Boolean = false
    private var code: String = ""
    private var noAgreeChecked = false
    private var noMiddleNameChecked = false

    var snUser: SnUser? = null
    private var phoneVerified: Boolean = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userPhoneConfirmedSubject
                .performOnBackgroundOutOnMain()
                .subscribeBy {
                    phoneVerified = it
                    viewState.updatePhoneConfirmationStatus(it)
                }
    }

    override fun attachView(view: FinishRegisterContract.View?) {
        super.attachView(view)
        initData()
    }

    private fun initData() {
        viewState.setData(email, firstName, middleName, lastName, phone, isAgree, phoneVerified)
        if (!phoneVerified)
            viewState.phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onClickUserAgreement() {
        viewState.showUserAgreement()
    }

    override fun onClickAgree(isAgree: Boolean) {
        this.isAgree = isAgree
        viewState.showAgreementError(false)
        viewState.enableRegisterBtn(isAgree)
    }

    override fun onChangePhoneText(phone: String) {
        viewState.apply {
            if (phoneVerified) {
                updatePhoneConfirmationStatus(this@FinishRegisterPresenter.phone == phone)
            }
            phoneConfirmEnabled(phone.isValidPhoneNumber(phoneNumberUtil))
        }
        this.phone = phone
    }

    override fun onChangeMiddleNameText(middleName: String) {
        this.middleName = middleName
    }

    override fun authVk() {
        super.authVk()
    }

    override fun onSaveCode(code: String) {
        this.code = code
        viewState.setData(email, firstName, middleName, lastName, phone, isAgree, phoneVerified)
    }

    override fun onNoMiddleNameChecked(checked: Boolean) {
        noMiddleNameChecked = checked
        viewState.enableMiddleNameInput(!checked)
    }

    override fun phoneConfirmed(isConfirmed: Boolean) {
        //this.phoneVerified = isConfirmed
        //viewState.phoneConfirmEnabled(phoneVerified)
    }

    override fun onChangeNameText(name: String) {
        this.firstName = name
    }

    override fun onChangeLastNameText(lastName: String) {
        this.lastName = lastName
    }

    override fun onChangeEmailText(email: String) {
        this.email = email
    }

    override fun onHandleAuthLink() {
        userRepository.updateProfile(appData.getId(), mapOf(USER_EMAIL to FieldDetails(value = email, isVisible = true), USER_NAME to firstName,
                USER_LAST_NAME to lastName, USER_MIDDLE_NAME to FieldDetails(value = middleName, absent = noMiddleNameChecked),
                USER_PHONE to arrayListOf(FieldDetails(value = phone?.replace(" ", ""), type = PHONE_PERSONAL, isVisible = true, isConfirmed = phoneVerified))))
                .performOnBackgroundOutOnMain()
                .subscribe({ viewState.openHome() }, { })
                .call(compositeDisposable)


        /*authRepository.registerConfirm(email?: "", code, firstName?: "",
                lastName?: "", middleName, phone, email?: "")
                .performOnBackgroundOutOnMain()
                .subscribe({ }, { })
                .call(compositeDisposable)*/
    }

    override fun onContinueWithSnRegistration(snUser: SnUser) {
        viewState.showSnRegistration(snUser)
    }

    override fun onPhoneConfirmClick() {
        val phone = this.phone
        val phoneValid = phone.isValidPhoneNumber(phoneNumberUtil)
        viewState.apply {
            showWrongPhoneError(!phoneValid)
            if (phone != null) showPhoneConfirm(phone)
        }
    }
}
