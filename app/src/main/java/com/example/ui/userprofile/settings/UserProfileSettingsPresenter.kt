package com.example.ui.userprofile.settings

import android.app.NotificationManager
import android.content.Context
import android.view.ViewGroup
import com.example.data.AppData
import com.example.data.models.SnType
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.models.UserState
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.snAuth.SnAuthCallbackHelper
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.ui.views.CustomCheckView
import com.example.util.PHONE_PERSONAL
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withLoadingDialog
import withProgressBarDialogLoading
import withProgressBarLoading
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


    override fun attachView(view: UserProfileSettingsContract.View?) {
        super.attachView(view)
        viewState.setUserPassword(user.state?.isEmptyPassword ?: false)
        viewState.setUserSocialBinds(user)
    }

    override fun onDeleteConfirmEmail(email: String) {
        compositeDisposable += authRepository.deleteConfirmEmail(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.updateUser { this.email?.onConfirmation = null }
            }
    }

    override fun onDeleteEmail() {
        compositeDisposable += authRepository.registerEmailResend("")
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.updateUser {
                    this.email?.value = null
                    this.email?.isConfirmed = null
                    this.email?.onConfirmation = null
                }
                viewState.showChangeEmail(null)
            }
    }


    override fun onChangePrivacyConfirm(hidden: Boolean, view: ViewGroup) {
        val map = mapOf(USER_STATE to UserState(isHidden = hidden.toString()))
        updateUser(map, view) { it.state?.isHidden = hidden.toString() }
    }

    override fun onBlockEventNotificationsClick(hidden: Boolean, view: ViewGroup) {
        val map = mapOf(UserDetail.BLOCK_EVENT to hidden)
        updateUser(map, view) { it.blockedNotifications?.event = hidden }
    }

    override fun onBlockOrganizationNotificationsClick(hidden: Boolean, view: ViewGroup) {
        val map = mapOf(UserDetail.BLOCK_ORG to hidden)
        updateUser(map, view) { it.blockedNotifications?.organizations = hidden }
    }

    override fun onBlockProjectNotificationsClick(hidden: Boolean, view: ViewGroup) {
        val map = mapOf(UserDetail.BLOCK_PROJECT to hidden)
        updateUser(map, view) { it.blockedNotifications?.projects = hidden }
    }


    override fun onDeleteProfileConfirm() {
        compositeDisposable += userRepository.deleteProfile(appData.getId())
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                notificationManager.cancelAll()
                appData.logout()
            }
    }

    private fun updateUser(
        data: Map<String, Any?>,
        view: ViewGroup,
        onComplete: (UserDetail) -> Unit
    ) {
        compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
            .ignoreElement()
            .performOnBackgroundOutOnMain()
            .withProgressLoading(view)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { appData.updateUser(onComplete) }
            )
    }


    override fun onBindVkAccount(context: Context, view: ViewGroup) {
        if (user.getVkontakteBinds() == null) {
            compositeDisposable += SnAuthCallbackHelper.start(context, SnType.VK)
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    userRepository.bindSocialAccount(it.uuid, it.snType.code)
                        .doOnSuccess { user.socialBinds?.vkontakte = it.data }.ignoreElement()
                        .performOnBackgroundOutOnMain()
                        .withProgressLoading(view)
                        .subscribeSimple {
                            viewState.setUserSocialBinds(user)
                        }
                }
        } else {
            compositeDisposable += userRepository.unbindSocialAccount(user.getVkUUID(), SnType.VK.code)
                .doOnComplete { user.socialBinds?.vkontakte = null }
                .performOnBackgroundOutOnMain()
                .withProgressLoading(view)
                .subscribeSimple {
                    viewState.setUserSocialBinds(user)
                }
        }
    }

    override fun onDeleteProfileClick() = viewState.showDeleteProfile()
    override fun onChangePhoneClick() = viewState.showChangePhone()

    override fun onChangeEmailClick() {
        val email = user.email?.value ?: user.email?.onConfirmation
        viewState.showChangeEmail(email)
    }

    override fun onChangePasswordClick() = viewState.showChangePassword(false)
    override fun showChangeNameClick() = viewState.showChangeName(user)
    override fun showChangeShortNameClick() = viewState.showChangeShortName(user)

    private fun Completable.withProgressLoading(view: ViewGroup): Completable {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showBlockingLoading(true, view) }
            .doOnDispose { viewState.showBlockingLoading(false, view) }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.showBlockingLoading(false, view)
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.showBlockingLoading(false, view)
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnError(actionConsumer())
    }
}
