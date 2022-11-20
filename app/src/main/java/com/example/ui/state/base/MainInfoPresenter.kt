package com.example.ui.state.base

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.ConfirmCodeBody
import com.example.data.models.*
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmPresenter
import com.example.util.*
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import javax.inject.Inject
import kotlin.math.abs


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
    var isUpdatePhoto = false
    var isImageUpdating = false
    private var mDy = 0f
    private var canGoNext = false

    override fun attachView(view: MainInfoContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(mDy)
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
                        compositeDisposable += userRepository.searchAddress(
                            user.address?.getShortAddress() ?: ""
                        )
                            .performOnBackgroundOutOnMain()
                            .subscribeSimple(
                                onError = {
                                    if (!isImageUpdating) viewState.setPersonalData(user)
                                    isImageUpdating = false
                                }, onSuccess = { add ->
                                    if (add.data?.isNotEmpty() == true)
                                        user.address?.shortAddres = add.data[0].region
                                    if (!isImageUpdating) viewState.setPersonalData(user)
                                    isImageUpdating = false

                                })
                    }
                })
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun updateFiles(data: MutableMap<String, Any?>) {
        if (data.isNullOrEmpty()) {
            viewState.navigateUp()
            return
        } else {
            compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        onReceiveError(it)
                    },
                    onSuccess = {
                        compositeDisposable += userRepository.checkUserProfileSingle()
                            .performOnBackgroundOutOnMain()
                            .subscribeSimple(
                                onError = {
                                    viewState.goToNext()
                                }, onSuccess = {
                                    viewState.goToNext()
                                })

                    })
        }
    }


    override fun updateEmail(email: String) {
        appData.updateUserNew {
            this.email = FieldDetails(email, null, true, true, false, null)
        }
        viewState.showChangeEmailComplete(email)
    }

    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.showEmailNotUnique(email)
                },
                onComplete = {
                    viewState.showEmailConfirm(email)
                })
    }

    fun getEmail() = appData.getUserNew().email

    override fun onConfirmPhoneClick(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(null, phone)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                }, onComplete = {
                    viewState.showPhoneConfirm(phone)
                })
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
            .withCustomProgressBarLoadingDialog(viewState)
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

    override fun checkPassword(password: String, phone: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .andThen(Completable.defer { userRepository.checkEmailPhone(null, phone) })
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.apply {
                        hideCheckPassword()
                        showPhoneNotUnique(phone)
                    }
                }, onComplete = {
                    viewState.apply {
                        hideCheckPassword()
                        showPhoneConfirm(phone)
                    }
                })
    }

    fun setCanGoNext(can: Boolean) {
        this.canGoNext = can
    }

    fun isCanGoNext() = canGoNext

    private fun updateUserInternal(update: UserDetail.() -> Unit) = appData.updateUserNew(update)
}