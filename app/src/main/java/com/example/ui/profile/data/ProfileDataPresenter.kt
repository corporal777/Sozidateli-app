package com.example.ui.profile.data

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Base64
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.InjectViewState
import com.bumptech.glide.Glide
import com.example.BuildConfig
import com.example.R
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
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
import com.example.util.saveImageToCache
import com.example.util.saveImageToGallery
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ProfileDataPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val rxPermissions: RxPermissions,
    private val appData: AppData,
) : BaseBottomSheetPresenter<ProfileDataContract.View>(appData), ProfileDataContract.Presenter {

    var userAvatar: Bitmap? = null
    var userId = 0
    var userImageUrl = ""
    var userCodeUrl = ""
    var userName = ""
    var context: Context? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (context != null) {
            val userLink = BuildConfig.SHARE_URL + "portal/user/" + userId
            compositeDisposable += getQrCodeBitmapFromDrawable(context, userLink, userImageUrl)
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                        viewState.apply {
                            setName(userName, userLink)
                            showQrCodeLoadingProgress()
                        }
                        compositeDisposable += getQrCodeBitmap(context!!, userLink, userImageUrl)
                            .onErrorResumeNext(getQrCodeBitmapFromUrl(context!!, userCodeUrl))
                            .performOnBackgroundOutOnMain()
                            .subscribeSimple(
                                onError = {
                                    it.printStackTrace()
                                    viewState.hideQrCodeLoadingProgress()
                                },
                                onSuccess = { bm ->
                                    viewState.apply {
                                        hideQrCodeLoadingProgress()
                                        setImage(bm)
                                    }
                                })

                    },
                    onSuccess = {
                        viewState.apply {
                            setImage(it)
                            setName(userName, userLink)
                        }
                    })

        }


    }

    override fun shareImageClick(context: Context, image: Bitmap) {
        compositeDisposable += Single.create<Uri> {
            val uri = saveImageToCache(context, image)
            if (uri != null) {
                it.onSuccess(uri)
            } else {
                it.onError(Exception("Uri is null"))
            }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.showShareImage(it)
            }
    }

    override fun shareLinkClick(text: String) {
        viewState.showShareLink(text)
    }

    override fun saveImageToGalleryClick(context: Context, image: Bitmap) {
        compositeDisposable += rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribeSimple {
                if (it) {
                    compositeDisposable += Completable.fromAction {
                        saveImageToGallery(context, image, "sozidateli_images")
                    }
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple {
                            viewState.showSnackBarMessage(
                                context.getString(R.string.qr_code_is_saved),
                                R.drawable.ic_profile_code_save_filled
                            )
                        }
                }
            }

    }

    private fun getQrCodeBitmap(context: Context, link: String, uri: String): Maybe<Bitmap> {
        return Maybe.fromCallable {
            val data = QrData.Url(link)
            val opt = createQrOptions(1400, 1400, .1f) {
                logo {
                    val drawableSource: DrawableSource
                    val drawableShape: QrLogoShape
                    if (!uri.isNullOrEmpty()) {
                        drawableSource = if (userAvatar == null) {
                            val bm =
                                Glide.with(context).asBitmap().load(uri).submit().get()
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            DrawableSource.DecodedBitmap(bm)
                        } else {
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            DrawableSource.DecodedBitmap(userAvatar!!)
                        }
                    } else {
                        drawableShape = QrLogoShape.Circle
                        drawableSource = DrawableSource.Resource(R.drawable.ic_about_app)
                    }
                    drawable = drawableSource
                    size = .25f
                    padding = QrLogoPadding.Accurate(.1f)
                    shape = drawableShape
                }
                colors {
                    dark =
                        QrColor.Solid(ContextCompat.getColor(context, R.color.qr_code_pixels_color))
                }
                shapes {
                    darkPixel = QrPixelShape.RoundCorners()
                    ball = QrBallShape.RoundCorners(.30f)
                    frame = QrFrameShape.RoundCorners(.30f)
                }
            }
            QrCodeGenerator(context).generateQrCode(data, opt)
        }
    }

    private fun getQrCodeBitmapFromDrawable(
        ct: Context?,
        link: String,
        uri: String
    ): Maybe<Bitmap> {
        return Maybe.fromCallable {
            val data = QrData.Url(link)
            val options = createQrVectorOptions {
                padding = .1f
                logo {
                    val drawableSource: DrawableSource
                    val drawableShape: QrLogoShape
                    if (!uri.isNullOrEmpty()) {
                        drawableSource = if (userAvatar == null) {
                            val bm =
                                Glide.with(ct!!).asBitmap().load(uri).submit().get()
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            DrawableSource.DecodedBitmap(bm)
                        } else {
                            drawableShape = QrLogoShape.RoundCorners(.30f)
                            DrawableSource.DecodedBitmap(userAvatar!!)
                        }
                    } else {
                        drawableShape = QrLogoShape.Circle
                        drawableSource = DrawableSource.Resource(R.drawable.ic_about_app)
                    }
                    drawable = drawableSource
                    size = .25f
                    shape = drawableShape
                }
                colors {
                    dark = QrVectorColor
                        .Solid(ContextCompat.getColor(ct!!, R.color.qr_code_pixels_color))
                }
                shapes {
                    darkPixel = QrVectorPixelShape.RoundCorners(.5f)
                    ball = QrVectorBallShape.RoundCorners(.30f)
                    frame = QrVectorFrameShape.RoundCorners(.30f)
                }
                errorCorrectionLevel = QrErrorCorrectionLevel.Medium
            }
            val drawable = QrCodeDrawable(ct!!, data, options)
            val bitmap = Bitmap.createBitmap(1054, 1054, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun getQrCodeBitmapFromUrl(context: Context, uri: String): Maybe<Bitmap> {
        return Maybe.fromCallable {
            val str = StringBuilder(uri)
            val l = str.delete(0, 22)
            val imageByteArray = Base64.decode(l.toString(), Base64.DEFAULT)
            Glide.with(context).asBitmap().load(imageByteArray)
                .submit().get()
        }
    }
}