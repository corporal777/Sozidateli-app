package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.core.content.ContextCompat
import com.example.app.BuildConfig
import com.example.app.R
import com.example.data.AppData
import com.example.ui.base.bottomSheet.BaseBSPresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.ImageUtil
import com.example.util.ImageUtil.Companion.getBitmapFromUri
import com.example.util.ImageUtil.Companion.getBitmapFromUrlAsync
import com.example.util.rxtakephoto.RxTakePhoto
import com.example.util.saveImageToCache
import com.generator.qrcodegenerator.QrCodeGenerator
import com.generator.qrcodegenerator.QrData
import com.generator.qrcodegenerator.createReadyBitmapQrOptions
import com.generator.qrcodegenerator.vector.QrCodeDrawable
import com.generator.qrcodegenerator.vector.createReadyVectorQrOptions
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

    private var userImageUrl: String = appData.getUser().loadUserImage() ?: ""
    private var userName: String = appData.getUser().nameLastName
    private var userLink: String =
        if (appData.getUser().shortName.isNullOrEmpty()) getUrl() + appData.getUser().id
        else getUrl() + "@" + appData.getUser().shortName


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setName(userName, userLink)
        compositeDisposable += getQrCodeImageFromDrawable(userLink, userImageUrl)
            .onErrorResumeNext(getQrCodeImageFromBitmap(userLink, userImageUrl))
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.setImage(it) }
            )
    }

    override fun shareImageClick(context: Context, image: Bitmap) {
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

    override fun shareLinkClick(text: String) = viewState.showShareLink(text)

    override fun saveImageToGalleryClick(context: Context, image: Bitmap) {
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

    private fun getQrCodeImageFromBitmap(link: String, uri: String): Maybe<Bitmap> {
        return Maybe.defer {
            val data = QrData.Url(link)
            val opt = createReadyBitmapQrOptions(
                getBitmapFromUrlAsync(context, uri),
                R.drawable.ic_about_app,
                ContextCompat.getColor(context, R.color.qr_code_pixels_color)
            )
            Maybe.just(QrCodeGenerator(context).generateQrCode(data, opt))
        }
    }

    private fun getQrCodeImageFromDrawable(link: String, uri: String): Maybe<Bitmap> {
        return Maybe.defer {
            val data = QrData.Url(link)
            val avatar = getBitmapFromUrlAsync(context, uri)
            val options = createReadyVectorQrOptions(
                avatar,
                R.drawable.ic_about_app,
                ContextCompat.getColor(context, R.color.qr_code_pixels_color)
            )
            val bitmap = Bitmap.createBitmap(1054, 1054, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val drawable = QrCodeDrawable(context, data, options)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            Maybe.just(bitmap)
        }
    }

    private fun getUrl() = BuildConfig.SHARE_URL + "portal/user/"
}