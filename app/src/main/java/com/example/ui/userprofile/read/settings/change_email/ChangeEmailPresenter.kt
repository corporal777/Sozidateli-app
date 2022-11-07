package com.example.ui.userprofile.read.settings.change_email

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.AuthValidateUtil
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomProgressBarLoadingDialog
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
        if (AuthValidateUtil.isValidEmail(email)){
            compositeDisposable += userRepository.checkEmailPhone(email, null)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        viewState.showEmailNotUnique(email)
                    },
                    onComplete = {
                        onChangeEmailConfirm(email)
                    })
        }else {
            viewState.showEmailNotValid(email)
        }

    }

    override fun onChangeEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .withCheckInternetConnectivity()
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                appData.updateUserNew {
                    this.email?.onConfirmation = email
                }
                viewState.showChangeEmailComplete(email)
            }
    }


}