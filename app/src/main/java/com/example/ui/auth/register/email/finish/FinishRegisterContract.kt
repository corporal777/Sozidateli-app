package com.example.ui.auth.register.email.finish

import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FinishRegisterContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setData(
            email: String?,
            firstName: String?,
            lastName: String?,
            middleName: String?
        )

        @OneExecution
        fun setTimeLeft(seconds: Int)

        @OneExecution
        fun setCanResend(canResend: Boolean)

        @OneExecution
        fun enableRegisterBtn(isEnable: Boolean)

        @AddToEndSingle
        fun enableMiddleNameInput(enable: Boolean)

        @AddToEndSingle
        fun codeError()

        @Skip
        fun showWrongPhoneError(canShow : Boolean)

        @Skip
        fun showWrongEmailError(canShow : Boolean)

        @OneExecution
        fun setDescriptionText(loginType: String, login : String)

        @OneExecution
        fun showHideDescriptionText(canShow: Boolean)

        @AddToEndSingle
        fun openHome()

        @AddToEndSingle
        fun logout()

        @Skip
        fun showEmailPhoneNotUnique(email: String, loginType : String)

        @Skip
        fun connectToSocket()

    }

    interface Presenter : BaseContract.Presenter {
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
