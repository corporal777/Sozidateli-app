package com.example.ui.auth.register.email.finishregister.newbuild

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface FinishRegisterNewContract {
    interface View : BaseAuthContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(
            email: String?,
            phone: String?,
            firstName: String?,
            lastName: String?,
            middleName: String?
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTimeLeft(seconds: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCanResend(canResend: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun codeError()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun openHome()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun logout()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun sendCodeAgain()
        fun onClickClose()
        fun onChangeCodeText(code: String)
        fun onChangeNameText(name: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeMiddleNameText(lastName: String)
        fun onHandleAuthLink()
        fun onNoMiddleNameChecked(checked: Boolean)
        fun logout()
    }
}
