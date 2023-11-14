package com.example.ui.userprofile.edit.email

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangeEmailPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
) : BaseBottomSheetPresenter<ChangeEmailContract.View>(appData),
    ChangeEmailContract.Presenter {

    var currentEmail = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentEmail(currentEmail)
    }

    override fun checkEmailIsUnique(email: String) {
        if (AuthValidateUtil.isValidEmail(email)) {
            compositeDisposable += userRepository.checkEmailPhone(email, null)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = { viewState.showEmailNotUnique(email) },
                    onComplete = { onShowEmailConfirm(email) }
                )
        } else viewState.showEmailNotValid(email)
    }

    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .andThen(userRepository.getUserInternal())
            .doOnSuccess { new -> appData.updateUser { this.email = new.email } }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple {
                viewState.showEmailConfirm(email)
            }
    }


    override fun updateEmail(email: String) {
        viewState.showChangeEmailComplete()
    }


}