package com.example.ui.user.edit

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Interest
import com.example.data.models.UserEditDataType
import com.example.data.models.UserInterest
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.loadBitmap
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Maybe
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

    private var isFileEdit = false
    private var isInterestsLoaded = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    when (editType) {
                        UserEditDataType.MAIN -> setMainData(user)
                        UserEditDataType.PERSONAL -> viewState.setPersonalData(user)
                        UserEditDataType.EDUCATION -> viewState.setEducationData(user)
                        UserEditDataType.WORK -> viewState.setWorkData(user)
                        UserEditDataType.INTERESTS -> setInterestsData(user)
                        UserEditDataType.ADDITIONAL -> viewState.setAdditionalData(user)
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    private fun setMainData(user: User) {
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

    override fun onSaveMainClick(data: Map<String, Any?>) {
        onEditSave(data, true) { true }
    }

    override fun onSavePersonalClick(data: Map<String, Any?>) {
        onEditSave(data, true) { true }
    }

    override fun onSaveEducationClick(data: Map<String, Any?>) {
        onEditSave(data, true) { true }
    }

    override fun onSaveWorkClick(data: Map<String, Any?>) {
        onEditSave(data, true) { true }
    }

    override fun onSaveInterestsClick(data: List<Interest>) {
        onEditSave(mapOf(User.FIELD_INTERESTS to data), true) { false }
    }

    override fun onSaveAdditionalClick(data: Map<String, Any?>) {
        onEditSave(data, true) { false }
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

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(userRepository.updateUser(mapOf(User.FIELD_USER_EMAIL to email)), false) {
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
        onEditSave(mapOf(User.FIELD_USER_OLD_PASSWORD to oldPassword, User.FIELD_USER_NEW_PASSWORD to newPassword), false) {
            viewState.showPasswordChangeComplete()
            false
        }
    }

    override fun onAddFileClick() {
        viewState.showFileSelector()
    }

    override fun onEditFileClick(file: RecommendationFile) {
        isFileEdit = true
        viewState.setFileEditData(file)
    }

    override fun onFilePicked(path: String) {
        compositeDisposable += userRepository.uploadRecommendationFile(path)
                .flatMapMaybe { userRepository.getUserFull() }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({

                }, {
                    it.printStackTrace()
                    viewState.showUpdateError()
                })
    }

    override fun onFileEditSaveClick() {
        onEditSave(mapOf(
                User.FIELD_ATTACHED_FILES to (appData.getUser().attached_recomendation_files
                        ?: emptyList())
        ), true) {
            isFileEdit = false
            true
        }
    }

    override fun onFileEditCancelClick() {
        isFileEdit = false
        viewState.setAdditionalData(appData.getUser())
    }

    override fun onNavigateUpRequest() {
        when {
            isFileEdit -> onFileEditCancelClick()
            else -> viewState.navigateUpChecked()
        }
    }

    override fun onFileClick(file: RecommendationFile) {
        file.url?.let { viewState.downloadFile(it) }
    }

    private fun setInterestsData(user: User) {
        if (isInterestsLoaded) return
        compositeDisposable += userRepository.getInterests()
                .map { groupUserInterests(user, it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setInterestsData(it)
                    isInterestsLoaded = true
                }, {
                    it.printStackTrace()
                })
    }

    private fun groupUserInterests(user: User, interests: List<Interest>): Map<Interest, List<UserInterest>> {
        val userInterests = user.interests ?: emptyList()
        val groups = mutableMapOf<Interest, MutableList<UserInterest>>()
        interests.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest = userInterests.find { userInterest -> userInterest.id == interest.id } != null
                groups.getOrPut(parent) { mutableListOf() }.add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }

    private fun onEditSave(data: Map<String, Any?>, reloadUser: Boolean, onComplete: () -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        val avatar = data[User.FIELD_USER_AVATAR] as? Bitmap
        if (avatar != null) {
            if (data.size == 1) {
                updateUser(userRepository.uploadAvatar(avatar), reloadUser, onComplete)
            } else {
                updateUser(userRepository.uploadAvatar(avatar)
                        .flatMap { userRepository.updateUser(data.minus(User.FIELD_USER_AVATAR)) }, reloadUser, onComplete)
            }
        } else {
            updateUser(userRepository.updateUser(data), reloadUser, onComplete)
        }
    }

    private fun updateUser(request: Single<User>, reloadUser: Boolean, onComplete: () -> Boolean) {
        compositeDisposable += request
                .flatMapMaybe { if (reloadUser) userRepository.getUserFull() else Maybe.just(it) }
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
