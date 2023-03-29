package com.example.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.annotation.DrawableRes
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bumptech.glide.Glide
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ImageViewPresenter
@Inject constructor(
    private val context : Context
) : MvpPresenter<ImageViewContract.View>(), ImageViewContract.Presenter {

    var url: String? = null
    @DrawableRes
    var resource: Int? = null
    var bitmap: Bitmap? = null

    var customTransitionName: String? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val url = this.url
        val resource = this.resource
        val bitmap = this.bitmap
        viewState.apply {
            val transitionName = customTransitionName
            if (transitionName == null) setDefaultTransitionName()
            else setCustomTransitionName(transitionName)

            when {
                url != null -> {
                    findImageBitmap(url)
                }
                resource != null -> findImageBitmap(resource)
                bitmap != null -> onBitmapFound(bitmap)
                else -> throw NullPointerException("All supported image types are null")
            }
        }
    }

    private fun decodeByteArray(url : String){
        compositeDisposable += Maybe.fromCallable {
            val bytes = Base64.decode(url, Base64.DEFAULT)
            Glide.with(context).asBitmap().load(bytes)
                .submit().get()
        }
            .performOnBackgroundOutOnMain()
            .subscribe {
                onBitmapFound(it)
            }
    }

    override fun onBitmapFound(bitmap: Bitmap) {
        this.bitmap = bitmap
        viewState.setImage(bitmap)
    }

    override fun onBitmapFoundFailed(t: Exception) {
        viewState.apply { showError() }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
