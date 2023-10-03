package com.example.ui.gallery

import android.Manifest
import android.content.Context
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ImageModel
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.ImageUtil
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.example.util.rxtakephoto.RxTakePhoto
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class GalleryBottomPresenter
@Inject constructor(
    private val appData: AppData,
    private val context: Context,
    private val rxPermissions: RxPermissions,
    private val userRepository: UserRepository,
    private val rxTakePhoto: RxTakePhoto
) : BaseBottomSheetPresenter<GalleryBottomContract.View>(appData), GalleryBottomContract.Presenter {

    private var imagesList = listOf<Uri>()
    private var isFirstLaunch = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .flatMapMaybe {
                if (it) Maybe.defer { ImageUtil.getGalleryImages(context) }
                else Maybe.error(PermissionNotGrantedException())
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.hideGalleryFragment() },
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
            .doOnComplete {
                appData.updateUserNew { image = ImageModel(null, null, null, null, null) }
            }
            .andThen(userRepository.checkUserProfileSingle())
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = {
                    viewState.apply {
                        setPhotoUpdated(appData.getUserNew().image)
                        hideGalleryFragment()
                    }
                }
            )
    }

    override fun observeCropFinished(request: Single<Boolean>) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.apply {
                    if (it) {
                        setPhotoUpdated(appData.getUserNew().image)
                        hideGalleryFragment()
                    }
                }
            }
    }


    fun isHasAnyState() = appData.hasBaseState || appData.hasMaxState
}