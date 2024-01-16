package com.example.ui.auth.base

import com.example.data.AppData
import com.example.repository.AuthRepository
import com.example.ui.base.BasePresenter
import com.example.data.models.SnAuth

abstract class BaseAuthPresenter<V : BaseAuthContract.View>
constructor(
    private val authRepository: AuthRepository,
    appData: AppData
) : BasePresenter<V>(appData), BaseAuthContract.Presenter {


    companion object {
        private const val ERROR_NEED_REGISTRATION = "NEED_REGISTRATION"
    }
}
