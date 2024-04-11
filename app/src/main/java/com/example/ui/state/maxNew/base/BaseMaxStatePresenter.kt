package com.example.ui.state.maxNew.base

import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.Utils
import io.reactivex.Completable
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
        val email = appData.getUser().email
        if (email?.value != null && email.isConfirmed != null) onShowMaxStateDone()
        else viewState.showAddEmailDialog()
    }

    override fun checkEmailIsUnique(withCheck: Boolean, email: String) {
        compositeDisposable += Completable.defer {
            if (withCheck) userRepository.checkEmailPhone(email, null)
            else Completable.complete()
        }
            .doOnComplete {
                if (getUserData().email?.value.isNullOrEmpty())
                    getUserData().email?.value = email
                else getUserData().email?.onConfirmation = email
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { viewState.showEmailIsNotUnique(email) },
                onComplete = { viewState.showEmailConfirmation(email) }
            )
    }



    override fun onClickClose() = viewState.setClickClose(screen)
    override fun onShowMaxStateDone() = viewState.showMaxStateDone(screen)

}