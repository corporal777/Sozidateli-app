package com.example.ui.support.sendFile

import android.Manifest
import android.content.Context
import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.SupportFile
import com.example.data.models.SupportFileType
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.ImageUtil
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.example.util.rxtakephoto.RxTakePhoto
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class SupportFilesPresenter @Inject constructor(
    private val appData: AppData,
    private val context: Context,
    private val rxPermissions: RxPermissions,
    private val userRepository: UserRepository,
    private val rxTakePhoto: RxTakePhoto
) : BaseBottomSheetPresenter<SupportFilesContract.View>(appData), SupportFilesContract.Presenter {

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

    override fun attachView(view: SupportFilesContract.View?) {
        super.attachView(view)
        if (isFirstLaunch) isFirstLaunch = false
        else viewState.updateCameraPreviewItem()
    }

    override fun onOpenGalleryClick() {
        compositeDisposable += rxTakePhoto.takeGalleryImage()
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.setFileUriReady(SupportFile(SupportFileType.IMAGE, it.uri))
            }
    }

    override fun onOpenCameraClick() {
       compositeDisposable += rxTakePhoto.takeCameraImage()
           .performOnBackgroundOutOnMain()
           .subscribeBy {
               viewState.setFileUriReady(SupportFile(SupportFileType.IMAGE, it.uri))
           }
    }

    override fun onOpenImageClick(uri: Uri?) {
        viewState.setFileUriReady(SupportFile(SupportFileType.IMAGE, uri))
    }

    override fun onOpenFileClick() {
        compositeDisposable += rxTakePhoto.takeFile()
            .performOnBackgroundOutOnMain()
            .subscribeBy {
                viewState.setFileUriReady(SupportFile(SupportFileType.FILE, it.uri))
            }
    }


}