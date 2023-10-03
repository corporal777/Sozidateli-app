package com.example.ui.state.maxNew.base

import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.Utils
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading

abstract class BaseMaxStatePresenter<V : BaseMaxStateContract.View>(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BasePresenter<V>(appData), BaseMaxStateContract.Presenter {

    var screen = -1
    var isUpdating = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.buttonNextEnabled(false)
    }

    fun checkNextScreen() {
        compositeDisposable += userRepository.checkUserProfileSingle()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    viewState.apply {
                        hideCustomLoading()
                        goToNextScreen(Utils.maxStateScreen(getUserData()))
                    }
                },
                onSuccess = {
                    viewState.apply {
                        hideCustomLoading()
                        goToNextScreen(Utils.maxStateScreenNew(it))
                    }
                }
            )
    }

    override fun checkUserEmail() {
        if (appData.getUserNew().email?.value != null && appData.getUserNew().email?.isConfirmed != null) {
            onShowMaxStateDone()
        } else viewState.showAddEmailDialog()
    }

    override fun checkEmailIsUnique(email: String) {
        compositeDisposable += userRepository.checkEmailPhone(email, null)
            .withProgressBarDialogLoading(viewState)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = { viewState.showEmailIsNotUnique(email) },
                onComplete = { onShowEmailConfirm(email) }
            )
    }

    override fun onShowEmailConfirm(email: String) {
        viewState.hideAddEmailDialog()
        compositeDisposable += userRepository.updateUserProfile(
            appData.getId(),
            mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))
        ).ignoreElement()
            .andThen(authRepository.registerEmailResend(email))
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email = FieldDetails(value = email)
                }
                viewState.showEmailConfirmation(email)
            }
    }


    override fun onClickClose() = viewState.setClickClose(screen)
    override fun onShowMaxStateDone() = viewState.showMaxStateDone(screen)

}