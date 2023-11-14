package com.example.ui.profile.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.BuildConfig
import com.example.R
import com.example.data.AppData
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
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
) : BaseBottomSheetPresenter<ProfileDataContract.View>(appData), ProfileDataContract.Presenter {

    var userImageUrl: String = ""
    var userCodeUrl: String = ""
    var userName: String = ""
    var userLink: String = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setName(userName, userLink)
        compositeDisposable += getQrCodeImageFromDrawable(userLink, userImageUrl)
            .onErrorResumeNext(getQrCodeImageFromBitmap(userLink, userImageUrl))
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.setImage(it) }
            )
    }

    override fun shareImageClick(context: Context, image: Bitmap) {
        compositeDisposable += Single.create<Uri> {
            val uri = saveImageToCache(context, image)
            if (uri != null) it.onSuccess(uri)
            else it.onError(Exception("Uri is null"))
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.showShareImage(it)
            }
    }

    override fun shareLinkClick(text: String) = viewState.showShareLink(text)

    override fun saveImageToGalleryClick(context: Context, image: Bitmap) {
        compositeDisposable += takePhoto.saveImage(image)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onComplete = {
                    viewState.showSnackBarMessage(
                        context.getString(R.string.qr_code_is_saved),
                        R.drawable.ic_profile_code_save_filled
                    )
                }
            )
    }

    private fun getQrCodeImageFromBitmap(link: String, uri: String): Maybe<Bitmap> {
        return Maybe.fromCallable {
            val data = QrData.Url(link)
            val opt = createReadyBitmapQrOptions(
                uri,
                Glide.with(context).asBitmap().load(uri).submit().get(),
                R.drawable.ic_about_app,
                ContextCompat.getColor(context, R.color.qr_code_pixels_color)
            )
            QrCodeGenerator(context).generateQrCode(data, opt)
        }
    }

    private fun getQrCodeImageFromDrawable(link: String, uri: String): Maybe<Bitmap> {
        return Maybe.fromCallable {
            val data = QrData.Url(link)
            val options = createReadyVectorQrOptions(
                uri,
                Glide.with(context).asBitmap().load(uri).submit().get(),
                R.drawable.ic_about_app,
                ContextCompat.getColor(context, R.color.qr_code_pixels_color)
            )
            val bitmap = Bitmap.createBitmap(1054, 1054, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val drawable = QrCodeDrawable(context, data, options)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun getUrl() = BuildConfig.SHARE_URL + "portal/user/"
}