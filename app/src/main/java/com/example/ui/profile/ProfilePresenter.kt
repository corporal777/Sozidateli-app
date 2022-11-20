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
import com.example.util.PHONE_PERSONAL
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
import io.reactivex.Maybe
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
    var bmImage: Bitmap? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(0f)
        compositeDisposable += userRepository.getUserShortNew()
            .performOnBackgroundOutOnMain()
            .subscribe({
                userId = it.id
                viewState.setUser(it)
                getAdditionalData()
            }, { it.printStackTrace() })

    }

    override fun attachView(view: ProfileContract.View?) {
        super.attachView(view)
        viewState.apply {
            setAppBarElevation(abs(mDy / 10f))
            setUserState(appData.hasBaseState, appData.hasMaxState)
        }
        compositeDisposable += Maybe.defer { Maybe.just(appData.getUserNew()) }
            .onErrorResumeNext(userRepository.getUserShortNew())
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = {
                    viewState.setUser(appData.getUserNew())
                })

        /*try {
            viewState.setUser(appData.getUserNew())
        } catch (e: Exception) {
            compositeDisposable += userRepository.getUserShortNew()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setUser(appData.getUserNew())
                }, { it.printStackTrace() })
        }
         */
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


    override fun onSessionsClick() = viewState.showSessions()
    override fun onChangeAccountClick() = viewState.showChangeAccount()

    override fun onRateClick() {
        viewState.openPlayMarket()
    }

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
            if (this.phone?.filter { x -> x.type == PHONE_PERSONAL }.isNullOrEmpty()){
                this.phone = listOf(FieldDetails(phone, type = PHONE_PERSONAL, isConfirmed = true))
            }else {
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
        viewState.showProfileDataBottomSheetDialog(user, bmImage)
    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }
}
