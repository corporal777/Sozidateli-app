package com.example.ui.auth.authorization

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import moxy.viewstate.strategy.alias.OneExecution

interface AuthorizationContract {
    interface View : BaseAuthContract.View {
        @OneExecution
        fun showLogin()

        @OneExecution
        fun showRegistration()

        @OneExecution
        fun showSnRegistration(snUser: SnUser)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onRegisterClick()
        fun onLoginClick()
    }
}
