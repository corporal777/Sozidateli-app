package com.example.ui.profile

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import android.util.Base64.decode
import android.util.Log
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.InjectViewState
import com.bumptech.glide.Glide
import com.example.BuildConfig
import com.example.R
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.*
import com.example.util.qr_generator.QrCodeGenerator
import com.example.util.qr_generator.QrData
import com.example.util.qr_generator.QrErrorCorrectionLevel
import com.example.util.qr_generator.createQrOptions
import com.example.util.qr_generator.style.*
import com.example.util.qr_generator.vector.QrCodeDrawable
import com.example.util.qr_generator.vector.createQrVectorOptions
import com.example.util.qr_generator.vector.style.QrVectorBallShape
import com.example.util.qr_generator.vector.style.QrVectorColor
import com.example.util.qr_generator.vector.style.QrVectorFrameShape
import com.example.util.qr_generator.vector.style.QrVectorPixelShape
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import java.util.concurrent.TimeUnit
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
    private var userId = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            setAppBarElevation(0f)
            showShimmerView()
        }
        compositeDisposable += userRepository.getUserShortNew()
            .doOnSuccess {
                getAdditionalData()
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    userId = it.id
                    viewState.apply {
                        hideShimmerView()
                        setUser(it)
                        setUserLink(it)
                        if (it.binds?.deviceSessionsCount ?: 0 <= 1) {
                            setChangeOrAddNewAccount(
                                R.string.add_account_label,
                                R.drawable.ic_profile_add_account_edit
                            )
                        } else {
                            setChangeOrAddNewAccount(
                                R.string.change_account_label,
                                R.drawable.ic_profile_change_account_edit
                            )
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
        compositeDisposable += Maybe.just(appData.getUserNew())
            .onErrorResumeNext(userRepository.getUserShortNew())
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
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(500)
            .doOnComplete {
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
                onComplete = {

                }
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
            .performOnBackgroundOutOnMain()
            .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getSpeciality()
            .performOnBackgroundOutOnMain()
            .subscribe({}, { it.printStackTrace() })

        compositeDisposable += userRepository.getAcademicDegrees()
            .performOnBackgroundOutOnMain()
            .subscribe({}, { it.printStackTrace() })
    }


    override fun onEmailConfirmed(email: String) {
        appData.updateUserNew {
            this.email = FieldDetails(email, null, true, true, false, null)
        }
        viewState.codeSuccess()
    }

    override fun onPhoneConfirmed(phone: String) {
        appData.updateUserNew {
            if (this.phone?.filter { x -> x.type == PHONE_PERSONAL }.isNullOrEmpty()) {
                this.phone = listOf(FieldDetails(phone, type = PHONE_PERSONAL, isConfirmed = true))
            } else {
                appData.updatePhone(phone)
            }
        }
        viewState.codeSuccess()
    }

    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showEmailNotUnique(email)
                },
                onComplete = {
                    viewState.showEmailConfirmation(email)
                })
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                },
                onComplete = {
                    viewState.showPhoneConfirmation(phone)
                })
    }


    override fun onQrScannerToAuthWebClick() {
        viewState.showQrScannerToAuthWebSite()
    }

    override fun onShowProfileDataBottomSheetDialog(user: UserDetail, context: Context) {
        viewState.showProfileDataBottomSheetDialog(user)
    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }
}
