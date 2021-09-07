package com.example.ui.state.base

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.UserDetail
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class MainInfoPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository,
        private val takePhoto: RxTakePhoto
): BasePresenter<MainInfoContract.View>(), MainInfoContract.Presenter {

    lateinit var type: UserState
    var screen: Int = 1
    var isUpdatePhoto = false
    var isImageUpdating = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.apply {
                        compositeDisposable += userRepository.searchAddress(user.address?.getShortAddress()?: "")
                                .performOnBackgroundOutOnMain()
                                .subscribe({ add ->
                                    if (add.data?.isNotEmpty() == true)
                                        user.address?.shortAddres = add.data[0].region
                                    if (!isImageUpdating) setPersonalData(user)
                                    isImageUpdating = false
                                }, {
                                    if (!isImageUpdating) setPersonalData(user)
                                    isImageUpdating = false
                                })
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    fun getUserData() = appData.getUserNew()

    override fun updateFiles(data: MutableMap<String, Any?>) {
        onEditSave(data) {
            appData.updateUserNew {
                name = it.name
                middleName = it.middleName
                lastName = it.lastName
                birthday = it.birthday
                gender = it.gender
                notes = it.notes
                address = it.address
                phone = it.phone
            }
            true
        }
    }

    private fun onEditSave(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }

        updateUser(userRepository.updateUserProfile(appData.getId(), data), onComplete)
    }

    private fun updateUser(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUserNew().apply {
                        phone = it.phone
                        name = it.name
                        lastName = it.lastName
                        middleName = it.middleName
                        birthday = it.birthday
                        gender = it.gender
                        address = it.address
                    }
                    if (onComplete(it))
                        compositeDisposable += userRepository.checkUserProfileSingle()
                                .performOnBackgroundOutOnMain()
                                .subscribe({
                                    viewState.goToNext()
                                },{
                                    viewState.goToNext()
                                })
                        //viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }

    override fun sendEmail(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    appData.updateUserNew {
                        this.email = FieldDetails(email, null, true, false, false, null)
                    }
                    viewState.showChangeEmailComplete(email)
                }, {
                    it.printStackTrace()
                })
    }

    fun getEmail() = appData.getUserNew().email

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String, isFirst: Boolean) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUser(userRepository.updateProfile(appData.getId(), mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email)))) {
                it.email?.value = email
                viewState.showChangeEmailComplete(email)
                false
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun onConfirmPhoneClick(phone: String) {
        viewState.showPhoneConfirm(phone)
    }

    override fun onTakePhotoFromGalleryClick() = takePhoto(takePhoto.takeGalleryImage())
    override fun onTakePhotoFromCameraClick() = takePhoto(takePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        isUpdatePhoto = true
        isImageUpdating = true
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
                        onSuccess = {
                            compositeDisposable += userRepository.checkUserProfileSingle()
                                    .performOnBackgroundOutOnMain()
                                    .subscribeSimple(onSuccess = {})
                            updateUserInternal {
                                image = it
                            }
                            viewState.photoUpdated(it)
                        }
                )
    }

    override fun onRemovePhotoClick() {
        isUpdatePhoto = true
        isImageUpdating = true
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
                            viewState.photoUpdated(ImageModel(null, null, null, null, null, null))
                        }
                )
    }

    private fun updateUserInternal(update: UserDetail.() -> Unit) = appData.updateUserNew(update)
}