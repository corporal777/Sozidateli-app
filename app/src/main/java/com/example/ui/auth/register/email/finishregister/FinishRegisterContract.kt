package com.example.ui.auth.register.email.finishregister

import android.content.Context
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface FinishRegisterContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(email: String?, firstName: String?, middleName: String?, lastName: String?, phone: String?, isAgree: Boolean, phoneVerified: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnRegistration(snUser: SnUser)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserAgreement()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updatePhoneConfirmationStatus(confirmed: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun phoneConfirmEnabled(enabled: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongPhoneError(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun openHome()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun codeSuccess()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun codeError()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun logedout()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun unblockTokenListener()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setTimeLeft(seconds: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCanResend(canResend: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showAlertLoadingDialog()

        @StateStrategyType(SkipStrategy::class)
        fun hideAlertLoadingDialog()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickClose()
        fun onClickUserAgreement()
        fun onClickAgree(isAgree: Boolean)

        fun onChangePhoneText(phone: String)
        fun onChangeMiddleNameText(middleName: String)
        fun onChangeCodeText(code: String)
        fun onChangeNameText(name: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeEmailText(email: String)
        fun onSaveCode(code: String)
        fun phoneConfirmed(isConfirmed: Boolean)
        fun sendCodeAgain()
        fun onHandleAuthLink()
        fun onPhoneConfirmClick()
        fun onNoMiddleNameChecked(checked: Boolean)
        fun getData()
        fun logout()
    }
}