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
        viewState?.apply {
            enableRegisterBtn(false)
        }
    }


    override fun clickOnBack() = viewState.navigateUp()

    override fun changeEmailText(email: String) {
        isEmailValid = AuthUtil.isValidEmail(email)
        setValidRegister()
    }

    override fun chagnePasswordText(password: String) {
        isPasswordValid = AuthUtil.isValidPassword(password)
        viewState.passwordCheckColored(AuthUtil.isPasswordHasSix(password), AuthUtil.isPasswordHasOneCap(password), AuthUtil.isPasswordHasSymbol(password))
        setValidRegister()
    }


    override fun changeNameText(name: String) {
        isNameValid = name.trim().isNotEmpty()
        setValidRegister()
    }

    private fun setValidRegister() {
        viewState.enableRegisterBtn(isEmailValid && isNameValid && isPasswordValid)
    }

    override fun clickRegister() {
        authRepository.register()
                .performOnBackgroundOutOnMain()
                .subscribe {
                    viewState.showWelcome()
                }.call(compositeDisposable)
    }
}
