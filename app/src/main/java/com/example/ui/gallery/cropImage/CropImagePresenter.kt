package com.example.ui.gallery.cropImage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.util.ImageUtil
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class CropImagePresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val context: Context,
) : MvpPresenter<CropImageContract.View>(), CropImageContract.Presenter {

    var imageUrl: String? = null
    var customTransitionName: String? = null
    var isCropFinished = false

    private val compositeDisposable = CompositeDisposable()
    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += timerCompositeDisposable
        viewState.apply {
            val transitionName = customTransitionName
            if (transitionName.isNullOrEmpty()) setDefaultTransitionName()
            else setCustomTransitionName(transitionName)

            setImage(imageUrl?.toUri())
        }
    }

    override fun onShowImageCrop(uri: Uri?, width: Int, height: Int) {
        timerCompositeDisposable += Observable.timer(200, TimeUnit.MILLISECONDS)
            .flatMapSingle { Single.defer { Single.just(createBitmap(uri, width, height)) } }
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.showImageCrop(uri, it)
                timerCompositeDisposable.clear()
            }, {
                it.printStackTrace()
                timerCompositeDisposable.clear()
            })
    }

    override fun saveCroppedImage(request: Maybe<Bitmap>) {
        viewState.showProgressDialog()
        compositeDisposable += request
            .flatMapSingle { userRepository.changeUserImage(it) }
            .doOnSuccess { appData.updateUser { image = it } }
            .flatMap { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.hideProgressDialog()
                },
                onSuccess = {
                    isCropFinished = true
                    viewState.apply {
                        hideProgressDialog()
                        closeCropActivity()
                    }
                })
    }

    private fun createBitmap(uri: Uri?, width: Int, height: Int): Bitmap? {
        try {
            return Glide.with(context)
                .asBitmap()
                .load(uri)
                .signature(ObjectKey(System.currentTimeMillis()))
                .apply(RequestOptions().override(width, height))
                .submit().get()
        } catch (e: Exception) {
            return ImageUtil.getBitmapFromUri(context, uri, height, width)
        }
    }


    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
