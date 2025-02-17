package com.example.ui.gallery

import android.net.Uri
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBSPresenter
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class GalleryBottomPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val rxTakePhoto: RxTakePhoto
) : BaseBSPresenter<GalleryBottomContract.View>(appData), GalleryBottomContract.Presenter {

    private var imagesList = listOf<Uri>()
    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += rxTakePhoto.takeAllGalleryImages()
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { viewState.hideBottomSheetFragment() },
                onNext = { viewState.setGalleryImages(it) }
            )
    }

    override fun attachView(view: GalleryBottomContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.updateCameraPreviewItem()
    }

    override fun onGalleryClick() {
        compositeDisposable += rxTakePhoto.takeGalleryImage()
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext = { viewState.showCropActivity(it.uri, null) })
    }

    override fun onRemovePhotoClick() {
        compositeDisposable += userRepository.deleteImage()
            .andThen(userRepository.getUserInternal())
            .doOnSuccess {
                appData.updateUser {
                    image = it.image
                    avatarIsDefault = it.avatarIsDefault
                }
            }
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.apply {
                        setPhotoUpdated(appData.getUser().image)
                        hideBottomSheetFragment()
                    }
                }
            )
    }

    override fun observeCropFinished(request: Single<Boolean>) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.apply {
                    if (it) {
                        setPhotoUpdated(appData.getUser().image)
                        hideBottomSheetFragment()
                    }
                }
            }
    }


    fun isHasAnyState() = appData.hasBaseState || appData.hasMaxState
}