package com.example.ui.userprofile.read.settings

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
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
        val data = mapOf(User.FIELD_USER_OLD_PASSWORD to oldPassword, User.FIELD_USER_NEW_PASSWORD to newPassword)
        updateUser(data) {
            viewState.showPasswordChangeComplete()
        }
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(mapOf(User.FIELD_USER_EMAIL to email)) {
                it.user_email = email
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
        updateUser(mapOf(User.FIELD_USER_HIDDEN to hidden)) {
            it.isHidden = hidden
        }
    }

    override fun onDeleteProfileClick() {
        viewState.showDeleteProfile()
    }

    override fun onDeleteProfileConfirm() {
        compositeDisposable += userRepository.deleteProfile()
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

    private fun updateUser(data: Map<String, Any?>, onComplete: (User) -> Unit) {
        compositeDisposable += userRepository.updateUser(data)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            it.printStackTrace()
                            viewState.showUpdateError(it.message)
                        },
                        onSuccess = {
                            user.apply {
                                user_phone_confirmed = it.user_phone_confirmed
                                it.user_status?.let { status -> user_status = status }
                                it.user_status_detail?.let { details -> user_status_detail = details }
                            }
                            appData.updateUser(onComplete)
                        })
    }
}
