package com.example.ui.userprofile.read.settings.change_email

import android.app.NotificationManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.FieldDetails
import com.example.data.models.UserDetail
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
        if (AuthValidateUtil.isValidEmail(email)) {
            compositeDisposable += userRepository.checkEmailPhone(email, null)
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                //.withCustomProgressBarLoadingDialog(viewState)
                .subscribeSimple(
                    onError = {
                        viewState.showEmailNotUnique(email)
                    },
                    onComplete = {
                        onShowEmailConfirm(email)
                    })
        } else {
            viewState.showEmailNotValid(email)
        }

    }

    override fun onShowEmailConfirm(email: String) {
        compositeDisposable += authRepository.registerEmailResend(email)
            .andThen(userRepository.updateUserProfileField(mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))))
            .doOnSuccess { new -> appData.updateUserNew { this.email = new.email } }
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribeSimple {
                viewState.showEmailConfirm(email)
            }
    }


    override fun updateEmail(email: String) {
        viewState.showChangeEmailComplete()
//        compositeDisposable += userRepository.updateUserProfile(
//            appData.getId(),
//            mutableMapOf<String, Any>().apply {
//                put(
//                    UserDetail.USER_EMAIL,
//                    FieldDetails(value = email, isVisible = true, isConfirmed = true)
//                )
//            }
//        )
//            .performOnBackgroundOutOnMain()
//            .withCustomProgressBarLoadingDialog(viewState)
//            .subscribeSimple(
//                onError = {
//                    onReceiveError(it)
//                },
//                onSuccess = {
//
//                })
    }


}