package com.example.ui.profile

import android.app.NotificationManager
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Base64.decode
import androidx.core.graphics.drawable.toDrawable
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
import com.github.alexzhirkevich.customqrgenerator.QrCodeGenerator
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.createQrOptions
import com.github.alexzhirkevich.customqrgenerator.encoder.QrCodeMatrix
import com.github.alexzhirkevich.customqrgenerator.style.*
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.runBlocking
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withDelay
import withLoadingDialog
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt


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
    private var mDeviceId = appData.deviceId ?: ""
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
        viewState.setAppBarElevation(abs(mDy / 10f))
        viewState.setUserState(appData.hasBaseState, appData.hasMaxState)
        try {
            viewState.setUser(appData.getUserNew())
        } catch (e: Exception) {
            compositeDisposable += userRepository.getUserShortNew()
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.setUser(appData.getUserNew())
                }, { it.printStackTrace() })
        }
    }

    fun changeScrollingOffset(value: Int) {
        mDy += value
        viewState.setAppBarElevation(Math.abs(mDy / 10f))
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

    override fun sendEmail(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.hideDialogProgress()
                appData.updateUserNew {
                    this.email = FieldDetails(email, null, true, false, false, null)
                }
                viewState.emailSuccess()
            }, {
                viewState.hideDialogProgress()
                it.printStackTrace()
            })
    }

    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({ sendEmail(email) },
                { viewState.showEmailNotUnique(email) })
    }

    override fun checkPhoneIsUnique(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({ sendPhone(phone) },
                { viewState.showPhoneNotUnique(phone) })
    }

    override fun onQrScannerToAuthWebClick() {
        viewState.showQrScannerToAuthWebSite()
    }

    override fun sendPhone(phone: String) {
        compositeDisposable += authRepository.registerPhoneResend("personal", phone)
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.hideDialogProgress()
                viewState.phoneSuccess(phone)
            }, {
                viewState.hideDialogProgress()
                it.printStackTrace()
            })
    }

    override fun confirmCode(phone: String, code: String) {
        compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone, code))
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.hideDialogProgress()
                viewState.codeSuccess()
            }, {
                viewState.hideDialogProgress()
                it.printStackTrace()
            })
    }


    override fun onShowProfileDataBottomSheetDialog(user: UserDetail, context: Context) {
        compositeDisposable += Single.fromCallable {
            val link = BuildConfig.SHARE_URL + "portal/user/" + user.shortName
            val data = QrData.Url(link)
            val opt = createQrOptions(1054, 1054, .2f) {
                logo {
                    val drawableSource: DrawableSource
                    val drawableShape: QrLogoShape
                    if (!user.image.uri.isNullOrEmpty()) {
                        drawableSource = if (bmImage == null) {
                            val bm =
                                Glide.with(context).asBitmap().load(user.image.uri).submit().get()
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            CustomDrawableSource.DecodedBitmap(bm)
                        } else {
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            CustomDrawableSource.DecodedBitmap(bmImage!!)
                        }
                    } else {
                        drawableShape = QrLogoShape.Circle
                        drawableSource = DrawableSource.Resource(R.drawable.ic_about_app)
                    }
                    drawable = drawableSource
                    size = .25f
                    padding = QrLogoPadding.Accurate(.2f)
                    shape = drawableShape
                }
                colors { dark = QrColor.Solid(Color.BLACK) }
                shapes {
                    darkPixel = QrPixelShape.RoundCorners()
                    ball = QrBallShape.RoundCorners(.30f)
                    frame = QrFrameShape.RoundCorners(.30f)
                }
            }
            QrCodeGenerator(context).generateQrCode(data, opt)
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    compositeDisposable += Maybe.fromCallable {
                        val str = StringBuilder(user.qrCodeLink)
                        val l = str.delete(0, 22)
                        val imageByteArray = decode(l.toString(), Base64.DEFAULT)
                        Glide.with(context).asBitmap().load(imageByteArray).submit().get()
                    }.performOnBackgroundOutOnMain()
                        .subscribeSimple { bm ->
                            viewState.showProfileDataBottomSheetDialog(user, bm)
                        }
                },
                onSuccess = {
                    viewState.showProfileDataBottomSheetDialog(user, it)
                })

    }

    override fun onSettingsClick() {
        viewState.showSettings()
    }

    interface CustomDrawableSource {

        data class DecodedBitmap(val bitmap: Bitmap) : DrawableSource {
            override suspend fun get(context: Context): Drawable =
                bitmap.toDrawable(context.resources)
        }
    }

}
