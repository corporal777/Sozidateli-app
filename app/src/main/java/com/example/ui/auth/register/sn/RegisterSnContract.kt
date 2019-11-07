package com.example.ui.auth.register.sn

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.snAuth.SnType

interface RegisterSnContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserData(snType: SnType, name: String?, avatar: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmail(email: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPassword(password: String?, passwordConfirm: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableContinueButton(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailConfirmation(email: String, snUser: SnUser?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordConfirmError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickClose()
        fun onClickUserAgreement()
        fun onClickContinue()
        fun onClickAgree(isAgree: Boolean)

        fun onChangeEmailText(email: String)
        fun onChangePasswordText(password: String)
        fun onChangePasswordConfirmText(password: String)
    }
}
