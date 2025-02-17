package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.example.app.BuildConfig
import com.example.app.R
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.models.ImageModel
import com.example.ui.base.bottomSheet.BaseBSPresenter
import com.example.util.ImageUtil.Companion.getBitmapFromUri
import com.example.util.ImageUtil.Companion.getBitmapFromUrlAsync
import com.example.util.ImageUtil.Companion.getDrawableFromUrl
import com.example.util.rxtakephoto.RxTakePhoto
import com.example.util.saveImageToCache
import com.generator.qrcodegenerator.QrData
import com.generator.qrcodegenerator.createQrVectorOptions
import com.generator.qrcodegenerator.loadQrCodeDrawable
import com.generator.qrcodegenerator.style.BitmapScale
import com.generator.qrcodegenerator.style.BitmapScale.FitXY.toBitmapScale
import com.generator.qrcodegenerator.style.QrVectorBallShape
import com.generator.qrcodegenerator.style.QrVectorColor
import com.generator.qrcodegenerator.style.QrVectorFrameShape
import com.generator.qrcodegenerator.style.QrVectorImage
import com.generator.qrcodegenerator.style.QrVectorLogo
import com.generator.qrcodegenerator.style.QrVectorLogoPadding
import com.generator.qrcodegenerator.style.QrVectorLogoShape
import com.generator.qrcodegenerator.style.QrVectorPixelShape
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject


@InjectViewState
class ProfileDataPresenter
@Inject constructor(
    private val takePhoto: RxTakePhoto,
    private val appData: AppData,
    private val context: Context
) : BaseBSPresenter<ProfileDataContract.View>(appData), ProfileDataContract.Presenter {

    private var userImageUrl: String? = appData.getUser().loadUserNotDefaultImage()
    private var userName: String = appData.getUser().nameLastName
    private var userLink: String =
        if (appData.getUser().shortName.isNullOrEmpty()) getUrl() + appData.getUser().id
        else getUrl() + "@" + appData.getUser().shortName


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setName(userName, userLink)
        compositeDisposable += getQrCodeImageFromDrawable(userLink, userImageUrl)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.setImage(it) }
            )
    }

    override fun onShareImageClick(context: Context, image: Bitmap) {
        compositeDisposable += Single.defer {
            val uri = saveImageToCache(context, image)
            if (uri != null) Single.just(uri)
            else Single.error(NullPointerException("Uri for share is null"))
        }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.showShareImage(it) }
            )
    }

    override fun onShareLinkClick(text: String) = viewState.showShareLink(text)

    override fun onSaveImageClick(image: Bitmap) {
        compositeDisposable += takePhoto.saveImage(image)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onComplete = {
                    viewState.showSnackBarMessage(
                        R.string.qr_code_is_saved,
                        R.drawable.ic_profile_code_save_filled
                    )
                }
            )
    }


    private fun getQrCodeImageFromDrawable(link: String, uri: String?): Maybe<Bitmap> {
        return Maybe.defer {
            val options = createQrVectorOptions {
                background { color = QrVectorColor.Solid(context.getColor(R.color.white)) }
                padding = .1f
                logo {
                    bitmap = getBitmapFromUrlAsync(context, uri)
                    drawable = getDrawable(context, R.drawable.ic_about_app)
                    size = .23f
                    padding = QrVectorLogoPadding.Natural(.1f)
                    shape = QrVectorLogoShape.Internal.getLogoShape(0.30f, this)
                }
                colors { dark = QrVectorColor.Solid(context.getColor(R.color.black)) }
                shapes {
                    darkPixel = QrVectorPixelShape.Default
                    ball = QrVectorBallShape.Default
                    frame = QrVectorFrameShape.Default
                }
            }
            Maybe.just(loadQrCodeDrawable(QrData.Url(link), options))
        }.map { drawable ->
            val bitmap = Bitmap.createBitmap(1054, 1054, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun getUrl() = BuildConfig.SHARE_URL + "portal/user/"
}