package com.example.ui.auth.register

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface RegisterContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun passwordCheckColored(isHasSix: Boolean, isOneCap: Boolean, isHasSymbol: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun goToLoginWithEmailConfirmation(email: String, password: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onClickBack()
        fun onChangeEmailText(email: String)
        fun onChangePasswordText(password: String)
        fun onChangeNameText(name: String)
        fun onChangeLastNameText(lastName: String)
        fun onClickRegister(email: String, password: String, name: String, lastName: String)
    }
}
