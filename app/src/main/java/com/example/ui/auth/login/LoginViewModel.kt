package com.example.ui.auth.login

import androidx.lifecycle.viewModelScope
import com.examle.domain.model.auth.SnAuthModel
import com.examle.domain.interactor.AuthInteractor
import com.example.ui.base.BaseViewModel
import com.example.common.util.AuthValidateUtil
import com.example.common.util.Utils.isContainLetters
import com.example.common.util.Utils.isPhone
import com.example.common.util.Utils.isPhoneNumberValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val interactor: AuthInteractor
) : BaseViewModel() {

    private val _isEnabled = MutableStateFlow<Boolean>(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled

    private val _isLoginSuccess = MutableStateFlow<Boolean>(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess

    private var login = ""
    private var password = ""
    private var loginType = "email"
    var snAuth: SnAuthModel? = null

    fun onLoginClick(invite: Int) {
        viewModelScope.launch {
            if (invite != -1) interactor.authWithInvite(invite, loginType, login, password)
            else if (snAuth != null) interactor.authWithSn(loginType, login, password, snAuth!!)
            else interactor.authWithResult(loginType, login, password)
                .withLoading()
                .catch { e -> e.printStackTrace() }
                .collect { _isLoginSuccess.update { true } }
        }
    }

    fun onChangeLogin(value: String) {
        this.login = value
        performDataChange()
    }

    fun onChangePassword(value: String) {
        this.password = value
        performDataChange()
    }

    private fun performDataChange() {
        _isEnabled.value = if (isPhone(login) && !isContainLetters(login)) {
            loginType = "phone"
            isPhoneNumberValid(login) && password.isNotEmpty()
        } else {
            loginType = "email"
            AuthValidateUtil.isValidEmail(login) && password.isNotEmpty()
        }
    }
}