package com.example.ui.auth.register.email.finish

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.auth.base.BaseAuthContract

interface FinishRegisterContract {
    interface View : BaseAuthContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(
            email: String?,
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

        @StateStrategyType(SkipStrategy::class)
        fun showWrongPhoneError(canShow : Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showWrongEmailError(canShow : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setDescriptionText(canShow: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun openHome()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun logout()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailPhoneNotUnique(email: String, loginType : String)

    }

    interface Presenter : BaseAuthContract.Presenter {
        fun sendCodeAgain()
        fun onChangeCodeText(code: String)
        fun onChangeNameText(name: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeMiddleNameText(lastName: String)
        fun onChangeEmailText(email: String)
        fun onHandleAuthLink()
        fun onNoMiddleNameChecked(checked: Boolean)
        fun checkEmailPhoneUnique()
        fun onCloseClick()
    }
}
