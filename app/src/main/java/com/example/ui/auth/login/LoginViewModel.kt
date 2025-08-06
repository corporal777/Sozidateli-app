package com.example.ui.auth.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.models.AuthResponse
import com.example.data.models.SnAuth
import com.example.extensions.getAppVersion
import com.example.extensions.getAppVersionCode
import com.example.extensions.getDeviceName
import com.example.repository.AuthRepository
import com.example.ui.base.BaseViewModel
import com.example.util.AuthValidateUtil
import com.example.util.Utils.isContainLetters
import com.example.util.Utils.isPhone
import com.example.util.Utils.isPhoneNumberValid
import com.example.util.Utils.validatePhoneBeforeSend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.delayFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val repository: AuthRepository,
    private val appData: AppData
) : BaseViewModel() {

    private val _isEnabled = MutableStateFlow<Boolean>(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled

    private val _isLoginSuccess = MutableStateFlow<Boolean>(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess

    private var login = ""
    private var password = ""
    private var loginType = "email"
    var snAuth : SnAuth? = null

    fun onLoginClick(invite: Int) {
        viewModelScope.launch {
            if (invite != -1) repository.authEmailOrPhoneWithInvite(invite, getLoginBody())
            else if (snAuth != null) repository.authEmailOrPhoneWithSn(getLoginBody(), snAuth!!)
            else repository.authEmailOrPhoneWithResult(getLoginBody())
                //flowOf(AuthResponse(id = 22197, token = "ca5b1d3386ec1e7f3fc4195e4653bcd7"))
                .onEach {
                    if (it.token != null) appData.login(it.token)
                    if (it.id != null) appData.saveId(it.id)
                }
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

    private fun getLoginBody(): AuthBody {
        val validatedLogin = if (loginType == "phone") validatePhoneBeforeSend(login) else login
        return AuthBody(
            LoginModel(loginType, validatedLogin),
            LoginModel("common", password),
            "",
            getDeviceName(),
            getAppVersionCode(),
            getAppVersion(),
            ""
        )
    }
}