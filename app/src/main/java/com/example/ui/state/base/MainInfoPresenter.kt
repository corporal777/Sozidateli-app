package com.example.ui.state.base

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.PHONE_PERSONAL
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import javax.inject.Inject


@InjectViewState
class MainInfoPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val takePhoto: RxTakePhoto
) : BasePresenter<MainInfoContract.View>(appData), MainInfoContract.Presenter {

    lateinit var type: UserState
    var screen: Int = 1
    private var isImageUpdating = false
    private var isPhoneUpdating = false
    private var isFirstLaunch = true


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder()
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    val user = it.value
                    if (user != null) {
                        if (isPhoneUpdating) isPhoneUpdating = false
                        else if (isImageUpdating) isImageUpdating = false
                        else viewState.setPersonalData(user)
                    }
                })
    }


    override fun onSaveData(data: MutableMap<String, Any?>) {
        if (data.isNullOrEmpty()) {
            viewState.navigateUp()
            return
        } else {
            compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
                .flatMap { userRepository.checkUserProfileSingle() }
                .performOnBackgroundOutOnMain()
                .withProgressBarDialogLoading(viewState)
                .subscribeSimple(
                    onError = {
                        onReceiveError(it)
                        viewState.goToNext()
                    },
                    onSuccess = { viewState.goToNext() }
                )
        }
    }


    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { viewState.showEmailNotUnique(email) },
                onComplete = { onShowEmailConfirm(email) }
            )
    }

    override fun onShowEmailConfirm(email: String) {
        isPhoneUpdating = true
        compositeDisposable += authRepository.registerEmailResend(email)
            .andThen(userRepository.getUserInternal())
            .doOnSuccess { new -> appData.updateUserNew { this.email = new.email } }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.showEmailConfirm(email)
            }
    }


    override fun onShowPhoneEdit(phone: String?) {
        isPhoneUpdating = true
        viewState.showPhoneEdit(phone)
    }

    override fun onTakePhotoFromGalleryClick() = takePhoto(takePhoto.takeGalleryImage())
    override fun onTakePhotoFromCameraClick() = takePhoto(takePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
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
            .doOnSuccess { appData.getUserNew().image = it }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onSuccess = {
                    compositeDisposable += userRepository.checkUserProfileSingle()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(onSuccess = {})

                    viewState.photoUpdated(it)
                }
            )
    }

    override fun onRemovePhotoClick() {
        isImageUpdating = true
        compositeDisposable += userRepository.deleteImage()
            .doOnComplete { appData.getUserNew().image = null }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onComplete = {
                    compositeDisposable += userRepository.checkUserProfileSingle()
                        .performOnBackgroundOutOnMain()
                        .subscribeSimple(onSuccess = {})

                    viewState.photoUpdated(null)
                }
            )
    }

    fun getEmail() = appData.getUserNew().email
    fun getPhone() = appData.getUserNew().phone?.firstOrNull { it.type == PHONE_PERSONAL }
    override fun onClickClose() = viewState.navigateUp()
}