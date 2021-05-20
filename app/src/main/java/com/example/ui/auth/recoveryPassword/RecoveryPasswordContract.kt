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
        fun showRecoveryNotification(email: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongEmailError()
    }

    interface Presenter : BaseContract.Presenter {
        fun onRecoveryClick(context: Context)
        fun onChangeEmailText(email: String, context: Context)
        fun onUserUnderstand()
        fun onCloseClick()
        fun onSetPassword(code: String, password: String)
    }
}
