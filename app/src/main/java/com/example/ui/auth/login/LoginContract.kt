package com.example.ui.auth.login

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface LoginContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWelcome()
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLogin()
    }

    interface Presenter : BaseContract.Presenter{
        fun clickOnVkAuth()
        fun clickOnFbAuth()
        fun clickOnLoginEmail()
    }
}
