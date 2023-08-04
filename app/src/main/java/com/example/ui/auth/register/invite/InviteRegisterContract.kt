package com.example.ui.auth.register.invite

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.auth.base.BaseAuthContract

interface InviteRegisterContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(firstName: String?, lastName: String?, middleName: String?, email: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserAgreement()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showEmailDialog(email: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun openHome()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun loggedOut()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickRegister()

        fun onClickClose()
        fun onClickUserAgreement()

        fun onNoMiddleNameChecked(checked: Boolean)
        fun onAgreeChecked(checked: Boolean)

        fun onChangeFirstNameText(value: String)
        fun onChangeLastNameText(value: String)
        fun onChangeMiddleNameText(value: String)
        fun onChangePasswordText(value: String?, isValid: Boolean)
    }
}