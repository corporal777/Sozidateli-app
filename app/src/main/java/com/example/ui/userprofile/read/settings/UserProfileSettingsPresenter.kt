package com.example.ui.userprofile.read.settings

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.RegisterBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_EMAIL
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.models.UserState
import com.example.data.models.asOptional
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import ru.houseofapps.chat.HAChat
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfileSettingsPresenter @Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val haChat: HAChat,
        private val notificationManager: NotificationManager
) : BaseUserProfilePresenter<UserProfileSettingsContract.View>(appData), UserProfileSettingsContract.Presenter {

    override fun onChangePhoneClick() {
        viewState.showPhoneEdit()
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }

    override fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String) {
        compositeDisposable += userRepository.changePassword(appData.getId(), PasswordBody(password = newPassword))
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

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(mapOf(USER_EMAIL to FieldDetails(value = email))) {
                it.email?.value = email
                viewState.showChangeEmailComplete(email)
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun onChangePrivacyClick() {
        viewState.showChangePrivacy()
    }

    override fun onChangePrivacyConfirm(hidden: Boolean) {
        updateUser(mapOf(USER_STATE to UserState(isHidden = hidden))) {
            it.state?.isHidden = hidden
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
                            haChat.disconnect()
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
