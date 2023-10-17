package com.example.ui.profile

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.phoneToServer
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withDelay
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject


@InjectViewState
class ProfilePresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
) : BasePresenter<ProfileContract.View>(appData), ProfileContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += userRepository.getUserShortData()
            .doOnSuccess { getAdditionalData() }
            .performOnBackgroundOutOnMain()
            .withProgressBarLoading(viewState)
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
        viewState.setUserState(appData.hasBaseState, appData.hasMaxState)
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

    override fun onLogoutClick() {
        viewState.setIgnoreTokenListener(false)
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(300)
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeBy(
                onError = { onReceiveError(it) },
                onComplete = {}
            )
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
            .withProgressBarDialogLoading(viewState)
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
            .withProgressBarDialogLoading(viewState)
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
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.codeSuccess()
            }
    }


    override fun onQrScannerToAuthWebClick() = viewState.showQrScannerToAuthWebSite()
    override fun onSettingsClick() = viewState.showSettings()
    override fun onProfileClick() = viewState.showProfile(appData.getId().toString())

    override fun onFavoritesClick() = viewState.showFavorites()


    override fun onAboutApplicationClick() = viewState.showAboutApp()


    override fun onSupportClick() = viewState.openSupportEmail(appData.getId().toString())

    override fun onRateClick() = viewState.openPlayMarket()

    override fun onSessionsClick() = viewState.showSessions()
    override fun onChangeAccountClick() = viewState.showChangeAccount()

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


    private fun getUserRequest(): Maybe<UserDetail> {
        return Maybe.defer { Maybe.just(appData.getUserNew()) }
            .onErrorResumeNext(userRepository.getUserShortData())
    }
}
