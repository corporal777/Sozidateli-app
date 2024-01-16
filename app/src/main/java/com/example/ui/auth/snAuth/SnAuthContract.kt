package com.example.ui.auth.snAuth

import com.example.data.models.SnAuth
import com.example.data.models.SnUser
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface SnAuthContract {
    interface View : BaseContract.View {
        @OneExecution
        fun showLogin(snAuth : SnAuth)

        @OneExecution
        fun showSnRegistration(snUser: SnUser)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickLogin()
        fun onClickRegister()
    }
}