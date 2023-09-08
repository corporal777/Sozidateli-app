package com.example.ui.views.galleryView.cropImage

import android.net.Uri
import androidx.core.net.toUri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.repository.UserRepository
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
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
) : MvpPresenter<CropImageContract.View>(), CropImageContract.Presenter {

    var imageUrl: String? = null
    var customTransitionName: String? = null
    private val compositeDisposable = CompositeDisposable()

    private val timerCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        timerCompositeDisposable += compositeDisposable
        viewState.apply {
            val transitionName = customTransitionName
            if (transitionName == null) setDefaultTransitionName()
            else setCustomTransitionName(transitionName)

            setImage(imageUrl?.toUri())
        }
    }

    override fun onShowImageCrop(uri: Uri?) {
        timerCompositeDisposable += Observable.timer(1000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = {
                    timerCompositeDisposable.clear()
                },
                onNext = {
                    viewState.showImageCrop(uri)
                    timerCompositeDisposable.clear()
                })
    }

    override fun saveCroppedImage(cropView: CropImageView) {
        compositeDisposable += cropView.cropAsSingle()
            .flatMap { userRepository.changeUserImage(it) }
            .performOnBackgroundOutOnMain()
            .subscribe({
                appData.getUserNew().image = it
            }, {

            })

    }


    override fun onDestroy() {
        timerCompositeDisposable.clear()
        compositeDisposable.clear()
        super.onDestroy()
    }
}
