package com.example.ui.auth.register

import call
import com.arellomobile.mvp.InjectViewState
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.util.AuthUtil
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class RegisterPresenter
@Inject constructor(private val authRepository: AuthRepository
) : BasePresenter<RegisterContract.View>(), RegisterContract.Presenter {

    private var isEmailValid = false
    private var isPasswordValid = false
    private var isNameValid = false

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
        viewState.passwordCheckColored(AuthUtil.isPasswordHasSix(password), AuthUtil.isPasswordHasOneCap(password), AuthUtil.isPasswordHasSymbol(password))
        setValidRegister()
    }

    override fun onChangeNameText(name: String) {
        isNameValid = name.trim().isNotEmpty()
        setValidRegister()
    }

    private fun setValidRegister() {
        viewState.enableRegisterBtn(isEmailValid && isNameValid && isPasswordValid)
    }

    override fun onClickRegister(email: String, password: String, name: String) {
        authRepository.register(email, password, name)
                .performOnBackgroundOutOnMain()
                .subscribe({
                    viewState.showConfirmEmailDialog(email)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }
}
