package com.example.ui.userprofile.common.password

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.bodies.RecoverPasswordBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withDelay
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangePasswordPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
) : BasePresenter<ChangePasswordContract.View>(appData),
    ChangePasswordContract.Presenter {

    var isPasswordChange = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (isPasswordChange) viewState.showEnterNewPassword()
        else viewState.showEnterCurrentPassword()
    }

    override fun onCheckPasswordValid(password: String) {
        compositeDisposable += userRepository.checkPassword(password)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
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
                    if (appData.attemptsOfChangePassword != 3) appData.attemptsOfChangePassword = 3
                    viewState.showEnterNewPassword()
                }
            )
    }

    override fun onChangePasswordClick(newPassword: String) {
        compositeDisposable += userRepository.changePassword(appData.getId(), PasswordBody(newPassword))
            .doOnComplete { appData.getUser().state?.isEmptyPassword = false }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = { viewState.navigateUp() }
            )
    }


    override fun logoutFromAccount() {
        compositeDisposable += userRepository.logout(appData.getId())
            .withDelay(300)
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeBy(
                onError = { onReceiveError(it) },
                onComplete = {}
            )
    }
}