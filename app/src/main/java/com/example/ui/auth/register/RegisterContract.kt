package com.example.ui.auth.register

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface RegisterContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable:Boolean)
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun passwordCheckColored(isHasSix:Boolean,isOneCap:Boolean,isHasSymbol:Boolean)
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWelcome()
    }

    interface Presenter : BaseContract.Presenter{
        fun clickOnBack()
        fun changeEmailText(email:String)
        fun chagnePasswordText(password:String)
        fun changeNameText(name:String)
        fun clickRegister()
    }
}
