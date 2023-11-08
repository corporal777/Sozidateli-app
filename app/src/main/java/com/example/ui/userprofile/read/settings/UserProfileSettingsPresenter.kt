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
import com.example.ui.views.CustomCheckView
import com.example.util.PHONE_PERSONAL
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
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

    override fun onChangeEmailClick() {
        compositeDisposable += Maybe.defer { Maybe.just(appData.getUser()) }
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
                appData.updateUser {
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
                appData.updateUser {
                    this.email?.value = null
                    this.email?.isConfirmed = null
                    this.email?.onConfirmation = null
                }
                viewState.showChangeEmail(null)
            }
    }


    override fun onChangePrivacyConfirm(hidden: Boolean, view: CustomCheckView) {
        val map = mapOf(USER_STATE to UserState(isHidden = hidden.toString()))
        updateUser(map, view) { it.state?.isHidden = hidden.toString() }
    }


    override fun onBlockEventNotificationsClick(hidden: Boolean, view: CustomCheckView) {
        val map = mapOf(UserDetail.BLOCK_EVENT to hidden)
        updateUser(map, view) { it.blockedNotifications?.event = hidden }
    }

    override fun onBlockOrganizationNotificationsClick(hidden: Boolean, view: CustomCheckView) {
        val map = mapOf(UserDetail.BLOCK_ORG to hidden)
        updateUser(map, view) { it.blockedNotifications?.organizations = hidden }
    }

    override fun onBlockProjectNotificationsClick(hidden: Boolean, view: CustomCheckView) {
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
                appData.logout()
                notificationManager.cancelAll()
            }
    }

    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.showEmailConfirmation(email)
            }
    }

    private fun updateUser(data: Map<String, Any?>, view: CustomCheckView, onComplete: (UserDetail) -> Unit) {
        compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
            .performOnBackgroundOutOnMain()
            .withProgressLoading(view)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { appData.updateUser(onComplete) }
            )
    }

    override fun onDeleteProfileClick() = viewState.showDeleteProfile()
    override fun onChangePhoneClick() = viewState.showPhoneEdit(appData.getUser().phone?.firstOrNull { it.type == PHONE_PERSONAL })
    override fun onChangePasswordClick() = viewState.showChangePassword()
    override fun showChangeNameClick() = viewState.showChangeName(user)
    override fun showChangeShortNameClick() = viewState.showChangeShortName(user)

    private fun <T> Single<T>.withProgressLoading(view: CustomCheckView): Single<T> {
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
            .doOnSuccess(actionConsumer())
            .doOnError(actionConsumer())
    }
}
