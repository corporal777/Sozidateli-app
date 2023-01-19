package com.example.ui.userprofile.read.settings

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.UserShortNameBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_PHONE
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.models.UserState
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.AuthValidateUtil
import com.example.util.PHONE_PERSONAL
import com.example.util.phoneToServer
import com.google.gson.Gson
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import javax.inject.Inject
import kotlin.math.abs

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

    fun changeScrollingOffset(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun onChangePhoneClick() {
        viewState.showPhoneEdit(appData.getUserNew().phone?.firstOrNull { it.type == PHONE_PERSONAL })
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }


    override fun onChangeEmailClick() {
        val email = appData.getUserNew().email
        if (email?.value.isNullOrEmpty() && email?.onConfirmation.isNullOrEmpty()) {
            viewState.showChangeEmail()
        } else {
            if (!email?.value.isNullOrEmpty()) {
                viewState.showNewChangeEmail(email?.value ?: "")
            } else {
                viewState.showNewChangeEmail(email?.onConfirmation ?: "")
            }
        }

    }


    override fun onDeleteConfirmEmail(email: String) {
        compositeDisposable += authRepository.deleteConfirmEmail(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
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
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.value = null
                    this.email?.isConfirmed = null
                    this.email?.onConfirmation = null
                }
                viewState.showChangeEmail()
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
            .withCustomProgressBarLoadingDialog(viewState)
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
            .withCustomProgressBarLoadingDialog(viewState)
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

    override fun showChangeShortNameClick() {
        viewState.showChangeShortName(user)
    }


    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.showEmailConfirmation(email)
            }
    }

}
