package com.example.ui.userprofile.settings

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.data.models.SnAuth
import com.example.data.models.SnType
import com.example.data.models.UserDetail
import com.example.data.models.UserDetail.Companion.USER_STATE
import com.example.data.models.UserState
import com.example.data.socket.SocketIOManager
import com.example.exceptions.VkAccountAlreadyBoundException
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.login.LoginPresenter
import com.example.ui.auth.snAuth.SnAuthCallbackHelper
import com.example.ui.base.BasePresenter
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.ui.views.CustomCheckView
import com.example.util.PHONE_PERSONAL
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import retrofit2.HttpException
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
) : BasePresenter<UserProfileSettingsContract.View>(appData), UserProfileSettingsContract.Presenter {

    val user get() = appData.getUser()

    override fun attachView(view: UserProfileSettingsContract.View?) {
        super.attachView(view)
        compositeDisposable += Maybe.just(user)
            .performOnBackgroundOutOnMain()
            .subscribeSimple { user ->
                viewState.setUserData(user)
            }
    }

    override fun onDeleteProfileConfirm() {
        compositeDisposable += userRepository.deleteProfile(appData.getId())
            .andThen(authRepository.getTemporaryToken())
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                notificationManager.cancelAll()
                appData.logout()
            }
    }

    override fun onChangePrivacyConfirm(hidden: Boolean) {
        updateUser(mapOf(USER_STATE to UserState(isHidden = hidden.toString())))
    }

    override fun onBlockEventNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_EVENT to hidden))
    }

    override fun onBlockOrganizationNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_ORG to hidden))
    }

    override fun onBlockProjectNotificationsClick(hidden: Boolean) {
        updateUser(mapOf(UserDetail.BLOCK_PROJECT to hidden))
    }

    private fun updateUser(data: Map<String, Any?>) {
        compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.setUserData(user) }
            )
    }


    override fun onBindVkAccount(context: Context, snAuth: SnAuth?) {
        compositeDisposable += Single.defer {
            if (snAuth == null) SnAuthCallbackHelper.start(context, SnType.VK)
            else Single.just(snAuth)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple { sn ->
                userRepository.bindSocialAccount(sn.uuid, sn.snType.code, snAuth != null)
                    .doOnSuccess { appData.getUser().socialBinds?.vkontakte = it.data }
                    .performOnBackgroundOutOnMain()
                    .withProgressLoading()
                    .subscribeSimple(
                        onError = { onReceiveSnAuthError(it, sn) },
                        onSuccess = { viewState.setUserData(user) }
                    )
            }
    }

    override fun onUnbindVkAccount() {
        compositeDisposable += userRepository.unbindSocialAccount(user.getVkUUID(), SnType.VK.code)
            .doOnComplete { appData.getUser().socialBinds?.vkontakte = null }
            .andThen(Maybe.just(true))
            .performOnBackgroundOutOnMain()
            .withProgressLoading()
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.setUserData(user) }
            )
    }

    override fun onDeleteProfileClick() = viewState.showDeleteProfile()

    override fun onChangePhoneClick() = viewState.showChangePhone()

    override fun onChangeEmailClick() = viewState.showChangeEmail(user.email?.value)

    override fun onChangePasswordClick() = viewState.showChangePassword()

    override fun showChangeNameClick() = viewState.showChangeName(user)

    override fun showChangeShortNameClick() = viewState.showChangeShortName(user)

    private fun <T> Maybe<T>.withProgressLoading(): Maybe<T> {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { viewState.showBlockingLoading(true) }
            .doOnDispose { viewState.showBlockingLoading(false) }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) viewState.showBlockingLoading(false)
            else loadingDisposable.dispose()
        }

        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) viewState.showBlockingLoading(false)
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnSuccess(actionConsumer())
            .doOnError(actionConsumer())
    }


    private fun onReceiveSnAuthError(it: Throwable, sn : SnAuth) {
        if (it !is HttpException) onReceiveError(it)
        else {
            try {
                val error = Gson().fromJson(it.response()?.errorBody()?.string(), ApiError::class.java)
                if (error == null) onReceiveError(it)
                else if (error.hasError("Vk profile already connected."))
                    viewState.showAccountAlreadyBoundDialog(sn)
                else onReceiveError(it)
            } catch (e: Exception) { onReceiveError(e) }
        }
    }
}
