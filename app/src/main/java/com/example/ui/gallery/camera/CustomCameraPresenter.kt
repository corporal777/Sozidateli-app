package com.example.ui.gallery.camera

import android.net.Uri
import androidx.core.net.toUri
import com.example.data.AppData
import com.example.repository.UserRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import moxy.MvpPresenter
import performOnBackgroundOutOnMain
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class CustomCameraPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
) : MvpPresenter<CustomCameraContract.View>(), CustomCameraContract.Presenter {

    var imageUrl: String? = null
    var customTransitionName: String? = null
    var capturedImageUri: Uri? = null
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

    override fun onStartPreview() {
        timerCompositeDisposable += Observable.timer(500, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = {
                    viewState.startCameraPreview()
                    timerCompositeDisposable.clear()
                },
                onNext = {
                    viewState.startCameraPreview()
                    timerCompositeDisposable.clear()
                })
    }


    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
