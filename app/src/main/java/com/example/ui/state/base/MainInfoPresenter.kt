package com.example.ui.state.base

import android.util.Log
import com.example.data.AppData
import com.example.extensions.formatToDefaultDate
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.example.util.PHONE_PERSONAL
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
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
                }
                else if (isImageUpdating) isImageUpdating = false
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
                onError = {
                    onReceiveError(it)
                    viewState.goToNext()
                },
                onSuccess = { viewState.goToNext() }
            )

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
            .doOnSuccess { new -> appData.updateUser { this.email = new.email } }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.showEmailConfirm(email)
            }
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