package com.example.ui.userprofile.common.password

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.bodies.PasswordBody
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.views.passwordView.PasswordModel
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import withDelay
import javax.inject.Inject

@InjectViewState
class CheckPasswordPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
) : BasePresenter<CheckPasswordContract.View>(appData),
    CheckPasswordContract.Presenter {

    private var oldPassword = ""
    var isWarningVisible = false


    override fun attachView(view: CheckPasswordContract.View?) {
        super.attachView(view)
        viewState.enableBtnChange(!oldPassword.isNullOrEmpty())
    }

    override fun onCheckPasswordValid() {
        compositeDisposable += userRepository.checkPassword(oldPassword)
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = {
                    appData.attemptsOfChangePassword -= 1
                    if (appData.attemptsOfChangePassword <= 0){
                        viewState.setPasswordIsNotCorrect(0)
                        viewState.showLoginAgainDialog()
                    }
                    else viewState.setPasswordIsNotCorrect(appData.attemptsOfChangePassword)
                },
                onComplete = {
                    appData.attemptsOfChangePassword = 3
                    viewState.showChangePassword()
                }
            )
    }


    override fun logoutFromAccount() {
        compositeDisposable += userRepository.logout(appData.getId())
            .andThen(authRepository.getTemporaryToken())
            .withDelay(200)
            .doOnComplete {
                appData.isSubscribedToPush = false
                socket.disconnectFromSocket()
                appData.logout()
                notificationManager.cancelAll()
            }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onComplete = {}
            )
    }

    fun onChangeOldPassword(password : String?){
        oldPassword = password ?: ""
        viewState.enableBtnChange(!oldPassword.isNullOrEmpty())
    }
}