package com.example.ui.user.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserEditDataType
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.loadBitmap
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserEditPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val takePhoto: RxTakePhoto
) : BasePresenter<UserEditContract.View>(), UserEditContract.Presenter {

    lateinit var editType: UserEditDataType

    private val user = appData.getUser()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        when (editType) {
            UserEditDataType.MAIN -> setMainData()
            UserEditDataType.PERSONAL -> setPersonalData()
        }
    }

    private fun setMainData() {
        compositeDisposable += user.user_avatar.loadBitmap()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setMainData(user, it.value)
                }, {
                    it.printStackTrace()
                    viewState.setMainData(user, null)
                })
    }

    override fun onCancelClick() {
        viewState.navigateUp()
    }

    override fun onSaveClick(data: Map<String, Any?>) {
        onEditSave(data)
    }

    override fun onDisabledMainInputInfoClick() {
        viewState.showDisabledMainInputInfo()
    }

    override fun onEditAvatarClick() {
        viewState.showTakePictureChooser()
    }

    override fun onRemoveAvatarClick() {
        viewState.changeUserAvatar(null)
    }

    override fun onTakePhotoFromCameraRequest() = takePhoto(takePhoto.takeCameraImage())
    override fun onTakePhotoFromGalleryRequest() = takePhoto(takePhoto.takeGalleryImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
                .flatMapSingle { takePhoto.crop(resultRotation = it, outputMaxWidth = IMAGE_MAX_SIZE_AVATAR, outputMaxHeight = IMAGE_MAX_SIZE_AVATAR) }
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.changeUserAvatar(it)
                }, {
                    it.printStackTrace()
                })
    }

    private fun setPersonalData() {
        viewState.setPersonalData(user)
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(userRepository.updateUser(mapOf(User.FIELD_USER_EMAIL to email))) {
                viewState.showChangeEmailComplete(email)
                false
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun onChangePasswordClick() {
        viewState.showChangePassword()
    }

    override fun onChangePasswordClickConfirm(oldPassword: String, newPassword: String, newPasswordConfirm: String) {
        onEditSave(mapOf(User.FIELD_USER_OLD_PASSWORD to oldPassword, User.FIELD_USER_NEW_PASSWORD to newPassword)) {
            viewState.showPasswordChangeComplete()
            false
        }
    }

    private fun onEditSave(data: Map<String, Any?>, onComplete: () -> Boolean = { true }) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        val avatar = data[User.FIELD_USER_AVATAR] as? Bitmap
        if (avatar != null) {
            if (data.size == 1) {
                updateUser(userRepository.uploadAvatar(avatar), onComplete)
            } else {
                updateUser(userRepository.uploadAvatar(avatar)
                        .flatMap { userRepository.updateUser(data.minus(User.FIELD_USER_AVATAR)) }, onComplete)
            }
        } else {
            updateUser(userRepository.updateUser(data), onComplete)
        }
    }

    private fun updateUser(request: Single<User>, onComplete: () -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    if (onComplete()) viewState.navigateUp()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }
}
