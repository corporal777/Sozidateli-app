package com.example.ui.userprofile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
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
        appData: AppData,
        private val userRepository: UserRepository,
        private val takePhoto: RxTakePhoto
) : BaseUserProfilePresenter<UserProfileContract.View>(appData), UserProfileContract.Presenter {

    override fun onEditAvatarClick() {
        val avatar = user.user_avatar?.takeIf { it.isNotBlank() }
        viewState.showTakePictureChooser(avatar != null)
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
                .flatMap { userRepository.uploadAvatar(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onSuccess = {
                            updateUserInternal {
                                user_avatar = it.user_avatar
                            }
                        }
                )
    }

    override fun onRemovePhotoClick() {
        compositeDisposable += userRepository.updateUser(mapOf(User.FIELD_USER_AVATAR to null))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onSuccess = {
                            updateUserInternal {
                                user_avatar = it.user_avatar
                            }
                        }
                )
    }

    override fun onMainDataClick() = viewState.showMainData()

    override fun onContactsClick() = viewState.showContacts()

    override fun onInterestsClick() = viewState.showInterests()

    override fun onEducationClick() = viewState.showEducation()

    override fun onExperienceClick() = viewState.showExperience()
}
