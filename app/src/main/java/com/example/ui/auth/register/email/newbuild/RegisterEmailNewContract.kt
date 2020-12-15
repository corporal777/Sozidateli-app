package com.example.ui.auth.register.email.newbuild

import android.content.Context
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract

interface RegisterEmailNewContract {
    interface View : BaseAuthContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(
                email: String?,
                firstName: String?,
                lastName: String?,
                middleName: String?,
                noMiddleNameChecked: Boolean,
                password: String?,
                passwordConfirm: String?,
                phone: String?,
                phoneVerified: Boolean
        )

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableRegisterBtn(isEnable: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSnRegistration(snUser: SnUser)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailConfirmation(email: String, password: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEmailError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordConfirmError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAgreementError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showWrongPhoneError(show: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updatePhoneConfirmationStatus(confirmed: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun showPhoneConfirm(phone: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun phoneConfirmEnabled(enabled: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun enableMiddleNameInput(enable: Boolean)
    }

    interface Presenter : BaseAuthContract.Presenter {
        fun onClickClose()
        fun onClickRegister(
                email: String?,
                firstName: String?,
                lastName: String?,
                password: String?,
                passwordConfirm: String?
        )

        fun onChangeEmailText(email: String)
        fun onChangeFirstNameText(firstName: String)
        fun onChangeLastNameText(lastName: String)
        fun onChangeMiddleNameText(middleName: String)
        fun onNoMiddleNameChecked(checked: Boolean)
        fun onChangePasswordText(password: String)
        fun onChangePasswordConfirmText(password: String)
        fun onChangePhoneText(phone: String)
        fun onPhoneConfirmClick()
    }
}
