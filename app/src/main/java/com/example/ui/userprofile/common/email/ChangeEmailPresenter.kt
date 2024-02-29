package com.example.ui.userprofile.common.email

import android.app.NotificationManager
import com.example.data.AppData
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.AuthValidateUtil
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomLoading
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class ChangeEmailPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BasePresenter<ChangeEmailContract.View>(appData), ChangeEmailContract.Presenter {

    var currentEmail : String? = ""
    private var newEmail = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentEmail(currentEmail)
        performDataChange()
    }

    override fun onCheckEmailIsUnique() {
        if (AuthValidateUtil.isValidEmail(newEmail)) {
            compositeDisposable += userRepository.checkEmailPhone(newEmail, null)
                .performOnBackgroundOutOnMain()
                .withCustomLoading(viewState)
                .subscribeSimple(
                    onError = { viewState.showEmailNotUnique(newEmail) },
                    onComplete = { onShowEmailConfirm() }
                )
        } else viewState.showEmailError(true)
    }

    override fun onShowEmailConfirm() {
        compositeDisposable += Completable.fromAction {
            appData.updateUser { this.email?.onConfirmation = newEmail }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.showEmailConfirm(newEmail)
            }
    }


    override fun onChangeEmailText(email: String) {
        newEmail = email
        viewState.showEmailError(false)
        performDataChange()
    }

    private fun performDataChange() = viewState.enableBtnSave(AuthValidateUtil.isValidEmail(newEmail))
}