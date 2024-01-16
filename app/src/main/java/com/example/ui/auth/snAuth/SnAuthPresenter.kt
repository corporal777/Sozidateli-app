package com.example.ui.auth.snAuth

import com.example.data.AppData
import com.example.data.models.SnUser
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class SnAuthPresenter
@Inject constructor(
    private val authRepository: AuthRepository,
    private val appData: AppData,
) : BasePresenter<SnAuthContract.View>(appData), SnAuthContract.Presenter {

    lateinit var snUser: SnUser


    override fun onClickLogin() {
        viewState.showLogin(snUser.snAuth)
    }

    override fun onClickRegister() {
        viewState.showSnRegistration(snUser)
    }
}
