package com.example.ui.userprofile.passwordconfirm

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ApiError
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class PasswordConfirmPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        appData: AppData
) : BasePresenter<PasswordConfirmContract.View>(appData), PasswordConfirmContract.Presenter {

    companion object {
        private const val WRONG_PASSWORD_MESSAGE = "user_password is not match with stored"
    }

    lateinit var phone: String

    override fun onClickConfirmPassword(password: String) {
        compositeDisposable += userRepository.checkIfPasswordValid(password)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        {
                            if (it is ApiError && it.errors.contains(WRONG_PASSWORD_MESSAGE)) {
                                viewState.showConfirmPasswordError()
                            } else {
                                it.printStackTrace()
                                viewState.showRequestErrorMessage()
                            }
                        },
                        {
                            viewState.showPhoneConfirm(phone, password)
                        }
                )
    }
}
