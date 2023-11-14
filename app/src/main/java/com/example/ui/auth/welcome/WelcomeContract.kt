package com.example.ui.auth.welcome

import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution

interface WelcomeContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setUserName(name: String)
    }

    interface Presenter : BaseContract.Presenter
}
