package com.example.ui.auth.register.sn

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.snAuth.SnType
import com.example.util.AddToEndSingleByTagStateStrategy

interface RegisterSnContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUserData(snType: SnType, name: String?, avatar: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEmail(email: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPassword(password: String?, passwordConfirm: String?)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableContinueButton(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailConfirmation(email: String, snUser: SnUser?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordConfirmError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserAgreement()
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
