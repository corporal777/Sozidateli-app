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
                    if (user != null){
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
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                },
                onSuccess = {
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
                            .subscribeSimple(
                                onError = {
                                    viewState.goToNext()
                                }, onSuccess = {
                                    viewState.goToNext()
                                })
                    //viewState.goToNext()
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

    override fun onConfirmPhoneClick(phone: String) {
        compositeDisposable += userRepository.checkEmailPhone(
            null,
            Utils.validatePhoneBeforeSend(phone)
        )
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
            .andThen(Completable.defer { sendRequestCheckPhone(phone) })
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    viewState.showPhoneNotUnique(phone)
                }, onComplete = {
                    viewState.showPhoneConfirm(phone)
                })
    }

    override fun confirmCode(phone: String, code: String) {
        val data =  mapOf(
            UserDetail.USER_PHONE to arrayListOf(
                FieldDetails(
                    value = phone,
                    type = PHONE_PERSONAL,
                    isConfirmed = true,
                )
            )
        )
        compositeDisposable += authRepository.confirmPhone(ConfirmCodeBody("personal", phone, code))
            .andThen(userRepository.updateProfile(appData.getId(), data))
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = {
                    onReceiveError(it)
                },
                onSuccess = {
                    appData.updatePhoneNew(it.phone?.firstOrNull { it.type == PHONE_PERSONAL }?.value?:"")
                    viewState.codeSuccess(it.phone, canGoNext)
                })
    }

    private fun sendRequestCheckPhone(phone: String): Completable =
        userRepository.checkEmailPhone(
            null,
            Utils.validatePhoneBeforeSend(phone)
        )

    fun setCanGoNext(can : Boolean){
        this.canGoNext = can
    }

    private fun updateUserInternal(update: UserDetail.() -> Unit) = appData.updateUserNew(update)
}