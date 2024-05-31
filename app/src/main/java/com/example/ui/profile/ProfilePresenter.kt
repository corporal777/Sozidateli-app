package com.example.ui.profile

import android.app.NotificationManager
import com.example.R
import com.example.data.AppData
import com.example.data.bodies.FieldPhoneBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.exceptions.EmailNotUniqueException
import com.example.exceptions.PhoneNotUniqueException
import com.example.extensions.phoneToServer
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomLoading
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

    private var firstLaunch = true

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        compositeDisposable += getUserRequest()
            .performOnBackgroundOutOnMain()
            .let {
                if (firstLaunch) {
                    firstLaunch = false
                    it.withCustomLoading(viewState)
                } else it
            }
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.apply {
                        setUser(it)
                        setUserLink(it)
                        setUserState(appData.hasBaseState, appData.hasMaxState)
                    }
                })
    }

    override fun onLogoutClick() {
        compositeDisposable += userRepository.logout(appData.getId())
            .andThen(authRepository.getTemporaryToken())
            .withDelay(300)
            .doOnComplete {
                viewState.setIgnoreTokenListener(false)
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


    override fun checkEmailIsUnique(withCheck: Boolean, email: String) {
        viewState.hideAddPhoneEmailDialog()
        compositeDisposable += Completable.defer {
            if (withCheck) userRepository.checkEmailPhone(email, null)
                .onErrorResumeNext { Completable.error(EmailNotUniqueException()) }
            else Completable.complete()
        }
            .andThen(updateEmailPhoneRequest(email, null))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is EmailNotUniqueException) viewState.showEmailNotUnique(email)
                    else onReceiveError(it)
                },
                onComplete = { viewState.showEmailConfirmation(email) }
            )
    }


    override fun checkPhoneIsUnique(withCheck: Boolean, phone: String) {
        viewState.hideAddPhoneEmailDialog()
        compositeDisposable += Completable.defer {
            if (withCheck) userRepository.checkEmailPhone(null, phone)
                .onErrorResumeNext { Completable.error(PhoneNotUniqueException()) }
            else Completable.complete()
        }
            .andThen(updateEmailPhoneRequest(null, phone))
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    if (it is PhoneNotUniqueException) viewState.showPhoneNotUnique(phone)
                    else onReceiveError(it)
                },
                onComplete = { viewState.showPhoneConfirmation(phone) }
            )
    }


    override fun onQrScannerToAuthWebClick() = viewState.showQrScannerToAuthWebSite()
    override fun onSettingsClick() = viewState.showSettings()
    override fun onProfileClick() = viewState.showProfile(appData.getId().toString())
    override fun onFavoritesClick() = viewState.showFavorites()
    override fun onAboutApplicationClick() = viewState.showAboutApp()
    override fun onSupportClick() = viewState.showSupport()
    override fun onWriteEmailClick() = viewState.openSupportEmail(appData.getId().toString())
    override fun onRateClick() = viewState.openPlayMarket()
    override fun onSessionsClick() = viewState.showSessions()
    override fun onChangeAccountClick() = viewState.showChangeAccount()

    override fun onShowUserProfileLink() = viewState.showUserProfileLinkDialog()

    override fun onShowChangeUserShortName() = viewState.showChangeUserShortName()


    private fun getUserRequest(): Maybe<UserDetail> {
        return if (firstLaunch) userRepository.getUserFullData()
            .doOnSuccess { getAdditionalData() }
        else Maybe.defer { Maybe.just(appData.getUser()) }
            .onErrorResumeNext(userRepository.getUserFullData())
    }

    private fun getAdditionalData() {
        compositeDisposable += userRepository.getEducationLevel()
            .subscribe({}, { it.printStackTrace() })
        compositeDisposable += userRepository.getSpeciality()
            .subscribe({}, { it.printStackTrace() })
        compositeDisposable += userRepository.getAcademicDegrees()
            .subscribe({}, { it.printStackTrace() })
    }

    private fun updateEmailPhoneRequest(email: String?, phone: String?): Completable {
        return if (email.isNullOrEmpty()) userRepository.updateUserProfileField(
            mapOf(
                UserDetail.USER_PHONE to FieldPhoneBody(
                    value = phone,
                    type = PHONE_PERSONAL
                ).toList()
            )
        ).doOnSuccess { new -> appData.updateUser { this.phone = new.phone } }.ignoreElement()
        else userRepository.updateUserProfileField(
            mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))
        ).doOnSuccess { new -> appData.updateUser { this.email = new.email } }.ignoreElement()
    }
}
