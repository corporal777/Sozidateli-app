package com.example.ui.status

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.data.models.user.User
import com.example.data.models.user.User.Companion.FIELD_USER_PHONE_MOBILE
import com.example.data.models.user.User.Companion.FIELD_USER_PHONE_WORK
import com.example.data.models.user.User.Companion.FIELD_USER_STATUS_PHONE
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackground
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class StatusPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<StatusContract.View>(), StatusContract.Presenter {

    lateinit var status: User.Status

    private var phone: String? = null
    private var password: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.cancelVerification()
                    val user = it.value
                    phone = if (user?.user_status_phone_confirmed == true) user.user_status_phone else null
                    viewState.setStatus(status, status == user?.user_status, phone, user?.user_status_detail)
                }, {
                    it.printStackTrace()
                })
    }

    override fun onAddPhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_ADD)
    }

    override fun onChangePhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_CHANGE)
    }

    override fun onRemovePhoneClick() {
        viewState.checkPassword(VERIFICATION_ACTION_REMOVE)
    }

    override fun onPasswordInputComplete(password: String, action: Int) {
        compositeDisposable += userRepository.checkPasswordNew(password)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    this.password = password
                    when (action) {
                        VERIFICATION_ACTION_REMOVE -> viewState.showRemovePhone(phone ?: "")
                        else -> viewState.showChangePhone(action)
                    }
                }, {
                    if (it is ApiError && it.errors.contains(WRONG_PASSWORD_MESSAGE)) {
                        viewState.showVerificationError(VERIFICATION_ERROR_WRONG_PASSWORD)
                    } else {
                        it.printStackTrace()
                        viewState.showRequestErrorMessage()
                    }
                })
    }

    override fun onPhoneInputComplete(phone: String, action: Int, saveFlag: Int) {
        val password = this.password ?: return
        val updateMap = mutableMapOf(FIELD_USER_STATUS_PHONE to phone)
        when (saveFlag) {
            VERIFICATION_ADD_PHONE_MOBILE -> updateMap[FIELD_USER_PHONE_MOBILE] = phone
            VERIFICATION_ADD_PHONE_WORK -> updateMap[FIELD_USER_PHONE_WORK] = phone
        }
        compositeDisposable += userRepository.updateUser(updateMap)
                .flatMapCompletable { userRepository.sendStatusPhoneConfirmSms(password) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showCode(phone, action, saveFlag)
                }, {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                })
    }

    override fun onCodeInputComplete(phone: String, code: String, saveFlag: Int) {
        compositeDisposable += userRepository.sendStatusPhoneConfirmCode(code)
                .andThen(userRepository.getUserShortData())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({

                }, {
                    if (it is ApiError && it.errors.contains(WRONG_CODE_MESSAGE)) {
                        viewState.showVerificationError(VERIFICATION_ERROR_WRONG_CODE)
                    } else {
                        it.printStackTrace()
                        viewState.showRequestErrorMessage()
                    }
                })
    }

    override fun onDoNotReceiveCodeClick(phone: String) {
        val password = this.password ?: return
        compositeDisposable += userRepository.sendStatusPhoneConfirmSms(password)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showSentNewCodeMessage(phone)
                }, {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                })
    }

    override fun onPhoneRemoveAccept() {
        compositeDisposable += userRepository.updateUser(mapOf(FIELD_USER_STATUS_PHONE to ""))
                .flatMapMaybe { userRepository.getUserShortData() }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({

                }, {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                })
    }

    companion object {
        const val VERIFICATION_ACTION_ADD = 0
        const val VERIFICATION_ACTION_CHANGE = 1
        const val VERIFICATION_ACTION_REMOVE = 2

        const val VERIFICATION_ADD_PHONE_NONE = 0
        const val VERIFICATION_ADD_PHONE_MOBILE = 1
        const val VERIFICATION_ADD_PHONE_WORK = 2

        const val VERIFICATION_ERROR_WRONG_PASSWORD = 1
        const val VERIFICATION_ERROR_WRONG_CODE = 2

        private const val WRONG_PASSWORD_MESSAGE = "user_password is not match with stored"
        private const val WRONG_CODE_MESSAGE = "User has already confirmed phone or code mismatch"
    }
}
