package com.example.ui.auth.loginEmail

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface LoginEmailContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWelcome()
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRegister()
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableLoginBtn(isEnable:Boolean)
    }

    interface Presenter : BaseContract.Presenter{
        fun clickOnBack()
        fun changeEmailText(email:String)
        fun chagnePasswordText(password:String)
        fun clickLogin()
        fun clickRegister()
    }
}
