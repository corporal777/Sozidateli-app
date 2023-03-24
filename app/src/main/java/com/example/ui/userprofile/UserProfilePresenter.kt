package com.example.ui.userprofile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ImageModel
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfilePresenter @Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val takePhoto: RxTakePhoto,
) : BaseUserProfilePresenter<UserProfileContract.View>(appData),
    UserProfileContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun attachView(view: UserProfileContract.View?) {
        super.attachView(view)
    }

    override fun onEditAvatarClick() {
        val avatar = user.image?.uri?.takeIf { it.isNotBlank() }
        viewState.showTakePictureChooser(avatar != null, appData.hasBaseState, appData.hasMaxState)
    }

    override fun onTakePhotoFromGalleryClick() = takePhoto(takePhoto.takeGalleryImage())
    override fun onTakePhotoFromCameraClick() = takePhoto(takePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
            .firstOrError()
            .flatMap {
                takePhoto.crop(
                    resultRotation = it,
                    outputMaxWidth = IMAGE_MAX_SIZE_AVATAR,
                    outputMaxHeight = IMAGE_MAX_SIZE_AVATAR,
                    cropMode = CropImageView.CropMode.SQUARE
                )
            }
            .flatMap { userRepository.changeUserImage(it) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = {
                    compositeDisposable += userRepository.checkUserProfileSingle()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(onSuccess = {})
                    updateUserInternal {
                        image = it
                    }
                })
    }


    override fun onRemovePhotoClick() {
        compositeDisposable += userRepository.deleteImage()
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribeSimple(
                onComplete = {
                    compositeDisposable += userRepository.checkUserProfileSingle()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(onSuccess = {})
                    updateUserInternal {
                        image = ImageModel(null, null, null, null, null, null)
                    }
                }
            )
    }

    override fun onMainDataClick() = viewState.showMainData()

    override fun onContactsClick() = viewState.showContacts()

    override fun onInterestsClick() {
        if (!user.isHasInterests()) {
            viewState.showEdit()
        } else {
            viewState.showInterests()
            //viewState.showNextScreen()
        }

    }

    override fun onEducationClick() = viewState.showEducation()

    override fun onExperienceClick() = viewState.showExperience()
}
