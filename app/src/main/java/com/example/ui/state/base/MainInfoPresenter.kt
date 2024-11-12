package com.example.ui.state.base

import com.example.data.AppData
import com.example.data.models.ImageModel
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withProgressBarDialogLoading
import javax.inject.Inject


@InjectViewState
class MainInfoPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : BasePresenter<MainInfoContract.View>(appData), MainInfoContract.Presenter {

    lateinit var type: UserState
    var screen: Int = 1
    private var isImageUpdating = false
    private var isPhoneUpdating = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                val user = it.value
                if (user == null) viewState.navigateUp()
                else if (isPhoneUpdating) {
                    isPhoneUpdating = false
                    viewState.updatePhone(user.personalPhone)
                } else if (isImageUpdating) isImageUpdating = false
                else viewState.setPersonalData(user)
            }
    }


    override fun onSaveData(data: MutableMap<String, Any?>) {
        if (data.isNullOrEmpty()) viewState.navigateUp()
        else compositeDisposable += userRepository.updateUserProfile(appData.getId(), data)
            .flatMap { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.goToNext() }
            )

    }


    override fun checkEmailIsUnique(withCheck: Boolean, email: String) {
        compositeDisposable += Completable.defer {
            if (withCheck) userRepository.checkEmailPhone(email, null)
            else Completable.complete()
        }
            .doOnComplete {
                if (getEmail()?.value.isNullOrEmpty())
                    getUserData().email?.value = email
                else getUserData().email?.onConfirmation = email
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { viewState.showEmailNotUnique(email) },
                onComplete = { viewState.showEmailConfirm(email) }
            )
    }


    override fun onUpdateImage(photo: ImageModel?) {
        if (photo != null) viewState.updateImage(photo, getUserData().avatarIsDefault)
    }

    override fun onShowPhoneEdit() {
        isPhoneUpdating = true
        viewState.showPhoneEdit()
    }

    override fun onShowImageEdit() {
        isImageUpdating = true
        viewState.showChangeImage()
    }

    fun getEmail() = appData.getUser().email
}