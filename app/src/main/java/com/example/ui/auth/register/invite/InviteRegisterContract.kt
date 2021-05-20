package com.example.ui.auth.register.invite

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.auth.base.BaseAuthContract

interface InviteRegisterContract {
    interface View : BaseAuthContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUserAgreement()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordConfirmError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongPhoneError(show: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun phoneConfirmEnabled(enabled: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updatePhoneConfirmationStatus(confirmed: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updateFieldsInUI(firstName: String, lastName: String, middleName: String, email: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun showEmailDialog(email: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun unblockTokenListener()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun openHome()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun blockTokenListener()
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickRegister(
                email: String?,
                firstName: String?,
                lastName: String?,
                password: String?,
                isAgree: Boolean
                //passwordConfirm: String?
        )

        fun onClickClose()
        fun onClickUserAgreement()
        fun onSaveCode(code: String)

        fun onNoMiddleNameChecked(checked: Boolean)
        fun onAgreeChecked(checked: Boolean)

        fun onSaveEmailText(email: String, name: String, lastName: String, middleName: String)
        fun onChangeEmailText(email: String)
        fun onChangeFirstNameText(firstName: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeMiddleNameText(middleName: String)
        fun onChangePasswordText(password: String, isValid: Boolean)
        fun onChangePasswordConfirmText(password: String)
        fun onChangePhoneText(phone: String)
        fun onPhoneConfirmClick()
        fun getData()
    }
}