package com.example.ui.auth.register.sn

import com.example.data.models.SnUser
import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.snAuth.SnType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface RegisterSnContract {
    interface View : BaseAuthContract.View {
        @AddToEndSingle
        fun setUserData(snType: SnType, name: String?, avatar: String?)

        @OneExecution
        fun setEmail(email: String?)

        @OneExecution
        fun setPassword(password: String?, passwordConfirm: String?)

        @AddToEndSingle
        fun enableContinueButton(isEnable: Boolean)

        @OneExecution
        fun showEmailConfirmation(email: String, snUser: SnUser?)

        @OneExecution
        fun showPasswordError(show: Boolean)

        @OneExecution
        fun showPasswordConfirmError(show: Boolean)

        @OneExecution
        fun showAgreementError(show: Boolean)

        @OneExecution
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
