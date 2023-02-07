package com.example.ui.auth.recoveryPassword

import android.content.Context
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface RecoveryPasswordContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRecoveryBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmail(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryNotification(email: String, userId : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongEmailError()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTimeLeft(seconds: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordSuccessUpdated()
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecoveryClick()
        fun onChangeEmailText(email: String)
        fun onUserUnderstand()
        fun onCloseClick()
        fun onSetPassword(code: String, password: String, userId: String)
        fun sendCodeAgain()
    }
}
