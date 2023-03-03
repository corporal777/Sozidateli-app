package com.example.ui.profile

import android.app.NotificationManager
import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BaseContract
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.phoneToServer
import com.shakebugs.shake.Shake
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import javax.inject.Inject
import kotlin.math.abs


@InjectViewState
class ProfilePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
) : BasePresenter<ProfileContract.View>(appData), ProfileContract.Presenter {

    private var mDy = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        compositeDisposable += userRepository.getUserShortNew()
            .doOnSuccess { getAdditionalData() }
            .performOnBackgroundOutOnMain()
            .withShimmerLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = {
                    viewState.apply {
                        setUser(it)
                        setUserLink(it)
                        if (it.getSessionsCount() <= 1) {
                            setChangeOrAddNewAccount(R.string.add_account_label, R.drawable.ic_profile_add_account_edit)
                        } else {
                            setChangeOrAddNewAccount(R.string.change_account_label, R.drawable.ic_profile_change_account_edit)
                        }
                    }
                })
    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState.apply {
            setAppBarElevation(abs(mDy / 10f))
            setUserState(appData.hasBaseState, appData.hasMaxState)
        }
        compositeDisposable += getUserRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = {
                    viewState.apply {
                        setUser(it)
                        setUserLink(it)
                    }
                })
    }

    fun changeScrollingOffset(value: Int) {
        mDy += value
        viewState.setAppBarElevation(abs(mDy / 10f))
    }

    override fun onProfileClick() = viewState.showProfile(appData.getId().toString())

    override fun onFavoritesClick() = viewState.showFavorites()

    override fun onEventsClick() = viewState.showEvents()

    override fun onAboutApplicationClick() = viewState.showAboutApp()

    override fun onBannedClick() = viewState.showBanned()

    override fun onSupportClick() = viewState.openSupportEmail(appData.getId().toString())

    override fun onRateClick() = viewState.openPlayMarket()

    override fun onSessionsClick() = viewState.showSessions()
    override fun onChangeAccountClick() = viewState.showChangeAccount()


    override fun onLogoutClick() {
        viewState.setIgnoreTokenListener(false)
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(500)
            .doOnComplete {
                Shake.unregisterUser()
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                },
                onComplete = {}
            )
        /*compositeDisposable += userRepository.getFcmToken()
                .flatMapCompletable { userRepository.notificationsUnregister(it.token) }
                .doOnComplete {
                    appData.isSubscribedToPush = false
                    haChat.disconnect()
                    appData.logout()
                    notificationManager.cancelAll()
                }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                            viewState.showRequestErrorMessage()
                        }
                )*/
    }

    private fun getAdditionalData() {
        compositeDisposable += userRepository.getEducationLevel()
            .subscribe({}, { it.printStackTrace() })
        compositeDisposable += userRepository.getSpeciality()
            .subscribe({}, { it.printStackTrace() })
        compositeDisposable += userRepository.getAcademicDegrees()
            .subscribe({}, { it.printStackTrace() })
    }


    override fun checkEmailIsUnique(email: String) {
        viewState.hideAddPhoneEmailDialog()
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showEmailNotUnique(email)
                },
                onComplete = {
                    onShowEmailConfirm(email)
                })
    }

    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += userRepository.updateUserProfile(
            appData.getId(),
            mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))
        ).ignoreElement()
            .andThen(authRepository.registerEmailResend(email))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email = FieldDetails(value = email)
                }
                viewState.showEmailConfirmation(email)
            }
    }

    override fun checkPhoneIsUnique(phone: String) {
        viewState.hideAddPhoneEmailDialog()
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                },
                onComplete = {
                    onShowPhoneConfirm(phone)
                })
    }

    override fun onShowPhoneConfirm(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone)
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.showPhoneConfirmation(phone)
            }
    }

    override fun onConfirmPhoneSuccess(phone: String) {
        compositeDisposable += userRepository.updateUserProfile(
            appData.getId(),
            mapOf(
                UserDetail.USER_PHONE to arrayListOf(
                    FieldDetails(
                        value = Utils.validatePhoneBeforeSend(phone.phoneToServer() ?: ""),
                        type = PHONE_PERSONAL,
                        isConfirmed = true
                    )
                )
            )
        )
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.codeSuccess()
            }
    }


    override fun onQrScannerToAuthWebClick() {
        viewState.showQrScannerToAuthWebSite()
    }

    override fun onShowUserProfileLink() {
        compositeDisposable += getUserRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.showUserProfileLinkDialog(it)
                })
    }

    override fun onShowChangeUserShortName() {
        compositeDisposable += getUserRequest()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    viewState.showChangeUserShortNameDialog(it)
                })
    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }

    private fun getUserRequest(): Maybe<UserDetail> {
        return Maybe.defer { Maybe.just(appData.getUserNew()) }
            .onErrorResumeNext(userRepository.getUserShortNew())
    }

    private fun <T> Maybe<T>.withShimmerLoading(baseView: ProfileContract.View): Maybe<T> {
        val loadingDisposable = Completable.complete()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                baseView.showShimmerView()
            }
            .doOnDispose {
                baseView.hideShimmerView()
            }
            .subscribe()
        val actionHide = Action {
            if (loadingDisposable.isDisposed) baseView.hideShimmerView()
            else loadingDisposable.dispose()
        }
        fun <T> actionConsumer() = Consumer<T> {
            if (loadingDisposable.isDisposed) baseView.hideShimmerView()
            else loadingDisposable.dispose()
        }
        return this.doFinally(actionHide)
            .doOnDispose(actionHide)
            .doOnSuccess(actionConsumer())
            .doOnError(actionConsumer())
    }
}
