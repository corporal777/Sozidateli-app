package com.example.ui.userprofile.read.settings

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.models.UserState
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.PHONE_PERSONAL
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
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


    override fun onChangePhoneClick() {
        viewState.showPhoneEdit(appData.getUserNew().phone?.firstOrNull { it.type == PHONE_PERSONAL })
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }


    override fun onChangeEmailClick() {
        compositeDisposable += Maybe.defer { Maybe.just(appData.getUserNew()) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val email = it.email?.value ?: it.email?.onConfirmation
                viewState.showChangeEmail(email)
            }
    }


    override fun onDeleteConfirmEmail(email: String) {
        compositeDisposable += authRepository.deleteConfirmEmail(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
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
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.value = null
                    this.email?.isConfirmed = null
                    this.email?.onConfirmation = null
                }
                viewState.showChangeEmail(null)
            }
    }


    override fun onChangePrivacyConfirm(hidden: Boolean) {
        updateUser(mapOf(USER_STATE to UserState(isHidden = hidden.toString()))) {
            it.state?.isHidden = hidden.toString()
        }
    }


    override fun onBlockEventNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_EVENT to hidden)) {
            it.blockedNotifications?.event = hidden
        }
    }

    override fun onBlockOrganizationNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_ORG to hidden)) {
            it.blockedNotifications?.organizations = hidden
        }
    }

    override fun onBlockProjectNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_PROJECT to hidden)) {
            it.blockedNotifications?.projects = hidden
        }
    }

    override fun onDeleteProfileClick() {
        viewState.showDeleteProfile()
    }

    override fun onDeleteProfileConfirm() {
        compositeDisposable += userRepository.deleteProfile(appData.getId())
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                //Shake.unregisterUser()
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
    }

    private fun updateUser(data: Map<String, Any?>, onComplete: (UserDetail) -> Unit) {
        compositeDisposable += userRepository.updateProfile(appData.getId(), data)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                },
                onSuccess = {
                    user.apply {
                        phone = it.phone
                    }
                    appData.updateUserNew(onComplete)
                })
    }

    override fun showChangeNameClick() {
        viewState.showChangeName(user)
    }

    override fun showChangeShortNameClick() {
        viewState.showChangeShortName(user)
    }


    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.showEmailConfirmation(email)
            }
    }

}
