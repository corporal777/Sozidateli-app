package com.example.ui.userprofile.read.settings.change_password

import android.app.NotificationManager
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withDelay
import javax.inject.Inject

@InjectViewState
class ChangePasswordPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
) : BaseBottomSheetPresenter<ChangePasswordContract.View>(appData),
    ChangePasswordContract.Presenter {

    var recoverCode = ""
    var fromRecover = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (fromRecover) viewState.showEnterNewPassword()
    }


    override fun checkPasswordValid(password: String) {
        compositeDisposable += userRepository.checkPasswordNew(password)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    appData.attemptsOfChangePassword = appData.attemptsOfChangePassword - 1
                    if (appData.attemptsOfChangePassword <= 0) {
                        viewState.apply {
                            setPasswordIsNotCorrect(0)
                            showLoginAgainDialog()
                        }
                    } else {
                        val newAttempts = appData.attemptsOfChangePassword
                        viewState.setPasswordIsNotCorrect(newAttempts)
                    }
                },
                onComplete = {
                    if (appData.attemptsOfChangePassword != 3) {
                        appData.attemptsOfChangePassword = 3
                    }
                    viewState.showEnterNewPassword()
                }
            )
    }

    override fun onChangePasswordClickConfirm(newPassword: String) {
        compositeDisposable += if (fromRecover){
            authRepository.recoverPasswordNew(RecoverPasswordBody("email", recoverCode, newPassword))
        }else {
            userRepository.changePassword(appData.getId(), PasswordBody(newPassword))
        }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.showPasswordSuccessUpdated() }
            )
    }


    override fun logoutFromAccount() {
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(500)
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeBy(
                onError = {
                    it.printStackTrace()
                    viewState.showRequestErrorMessage()
                },
                onComplete = {

                }
            )
    }

    override fun onRecoveryPasswordClick() = viewState.showRecoveryPassword()

}