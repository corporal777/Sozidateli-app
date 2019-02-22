package com.example.ui.auth.register

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthUtil
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class RegisterPresenter
@Inject constructor(private val authRepository: AuthRepository
) : BasePresenter<RegisterContract.View>(), RegisterContract.Presenter {

    private var isEmailValid = false
    private var isPasswordValid = false
    private var isNameValid = false
    private var isLastNameValid = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        setValidRegister()
    }

    override fun onClickBack() = viewState.navigateUp()

    override fun onChangeEmailText(email: String) {
        isEmailValid = AuthUtil.isValidEmail(email)
        setValidRegister()
    }

    override fun onChangePasswordText(password: String) {
        isPasswordValid = AuthUtil.isValidPassword(password)
        viewState.passwordCheckColored(isPasswordValid, true, isHasSymbol = true)
        setValidRegister()
    }

    override fun onChangeNameText(name: String) {
        isNameValid = name.isNotBlank()
        setValidRegister()
    }

    override fun onChangeLastNameText(lastName: String) {
        isLastNameValid = lastName.isNotBlank()
        setValidRegister()
    }

    override fun onClickRegister(email: String, password: String, name: String, lastName: String) {
        authRepository.register(email, password, name, lastName)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.goToLoginWithEmailConfirmation(email, password)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    private fun setValidRegister() {
        viewState.enableRegisterBtn(isEmailValid && isNameValid && isPasswordValid && isLastNameValid)
    }
}
