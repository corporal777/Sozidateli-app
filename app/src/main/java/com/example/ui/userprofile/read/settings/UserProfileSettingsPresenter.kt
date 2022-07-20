package com.example.ui.userprofile.read.settings

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.PasswordBody
import com.example.data.models.*
import com.example.data.models.UserDetail.Companion.USER_EMAIL
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmPresenter
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.phoneToServer
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.lang.Math.abs
import javax.inject.Inject

@InjectViewState
class UserProfileSettingsPresenter @Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository
) : BaseUserProfilePresenter<UserProfileSettingsContract.View>(appData),
    UserProfileSettingsContract.Presenter {

    private var mDy = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
    }

    override fun attachView(view: UserProfileSettingsContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    fun changeScrollingOffset(value : Int){
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun onChangePhoneClick() {
        viewState.showPhoneEdit()
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }

    override fun onChangePasswordClickConfirm(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String
    ) {
        compositeDisposable += userRepository.changePassword(
            appData.getId(),
            PasswordBody(password = newPassword)
        )
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                },
                onComplete = {
                    viewState.showPasswordChangeComplete()
                })
    }

    override fun checkPasswordValid(password: String, newPassword: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .performOnBackgroundOutOnMain()
            //.withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showOldPasswordError()
                },
                onComplete = {
                    viewState.hideNewPasswordDialog()
                    onChangePasswordClickConfirm(password, newPassword, newPassword)
                }
            )
        /*.subscribe({
            viewState.hideNewPasswordDialog()
            onChangePasswordClickConfirm(password, newPassword, newPassword)
        }, {
            viewState.showOldPasswordError()
        })*/
    }

    override fun onChangeEmailClick() {
        val email = appData.getUserNew().email
        if (email?.value == null) {
            viewState.showChangeEmail()
        } else {
            viewState.showNewChangeEmail(email.value ?: "")
        }
    }

    override fun registerEmailResend(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.onConfirmation = email
                }
                viewState.showChangeEmailComplete(email)
            }
    }

    override fun onDeleteConfirmEmail(email: String) {
        compositeDisposable += authRepository.deleteConfirmEmail(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.onConfirmation = null
                }
            }
    }

    override fun onDeleteEmail() {
        compositeDisposable += authRepository.registerEmailResend("")
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.value = null
                    this.email?.isConfirmed = null
                    this.email?.onConfirmation = null
                }
                viewState.showChangeEmail()
            }
        /*updateUser(mapOf(USER_EMAIL to FieldDetails(value = null, isConfirmed = null))) {
            it.email?.value = null
            it.email?.isConfirmed = null
            viewState.showChangeEmail()
        }*/
    }


    override fun checkEmailIsUnique(email: String, isFirst: Boolean) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({ onChangeEmailConfirm(email, isFirst) },
                { viewState.showEmailNotUnique(email) })
    }

    override fun onChangeEmailConfirm(email: String, isFirst: Boolean) {
        if (AuthValidateUtil.isValidEmail(email)) {
            compositeDisposable += authRepository.registerEmailResend(email)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple {
                    appData.updateUserNew {
                        /*if (isFirst) {
                            this.email?.value = email
                        }*/
                        this.email?.onConfirmation = email
                    }
                    viewState.showChangeEmailComplete(email)
                }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({

                sendPhone(phone)
            },
                { viewState.showPhoneNotUnique(phone) })
    }

    override fun sendPhone(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend(
            "personal", phone.phoneToServer()
                ?: ""
        )
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.hideDialogProgress()
                viewState.phoneSuccess(phone)

            }, {
                viewState.hideDialogProgress()
                it.printStackTrace()
            })
    }


    override fun onPasswordInputComplete(password: String, phone: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                viewState.hideDialogProgress2()
                viewState.passwordSuccess(phone)

            }, {
                viewState.hideDialogProgress2()
                viewState.showRequestErrorMessage()
                //viewState.showUpdateError("Неправильный пароль!")
            })
    }

    override fun confirmCode(phone: String, code: String) {
        compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone, code))
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.hideDialogProgress()
                appData.updatePhone(phone)
                viewState.codeSuccess()
            }, {
                viewState.hideDialogProgress()
                it.printStackTrace()
            })
    }

    override fun onChangePrivacyClick() {
        viewState.showChangePrivacy()
    }

    override fun onChangePrivacyConfirm(hidden: Boolean) {
        updateUser(mapOf(USER_STATE to UserState(isHidden = hidden))) {
            it.state?.isHidden = hidden
        }
    }

    override fun onChangeNotConfirmedPhone(phone: String) {
//        appData.updateUserNew {
//            if (this.phone?.firstOrNull()?.type == PHONE_PERSONAL) {
//                this.phone?.firstOrNull()?.value = phone
//            }
//
//        }
        updateUser(
            mapOf(
                USER_PHONE to arrayListOf(
                    FieldDetails(
                        value = phone.phoneToServer(),
                        type = PHONE_PERSONAL,
                        isVisible = true,
                        isConfirmed = false
                    )
                )
            )
        ) {
            it.phone?.firstOrNull()?.value = phone
            viewState.codeSuccess()
        }
    }

    override fun onDeleteProfileClick() {
        viewState.showDeleteProfile()
    }

    override fun onDeleteProfileConfirm() {
        compositeDisposable += userRepository.deleteProfile(appData.getId())
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onComplete = {
                    appData.isSubscribedToPush = false
                    socket.disconnectFromSocket()
                    appData.logout()
                    notificationManager.cancelAll()
                }
            )
    }

    private fun updateUser(data: Map<String, Any?>, onComplete: (UserDetail) -> Unit) {
        compositeDisposable += userRepository.updateProfile(appData.getId(), data)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                },
                onSuccess = {
                    user.apply {
                        phone = it.phone
                        /*it.user_status?.let { status -> user_status = status }
                        it.user_status_detail?.let { details -> user_status_detail = details }*/
                    }
                    appData.updateUserNew(onComplete)
                })
    }
}
