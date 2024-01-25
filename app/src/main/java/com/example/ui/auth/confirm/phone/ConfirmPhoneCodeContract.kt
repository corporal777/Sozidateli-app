package com.example.ui.auth.confirm.phone

import com.example.ui.auth.base.BaseAuthContract
import com.example.ui.base.BaseContract
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.util.OneExecutionByTagStateStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ConfirmPhoneCodeContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setMobilePhone(mobilePhone: String?)

        @Skip
        fun setTimeLeft(seconds: Int)

        @Skip
        fun setCanCallAgain(canCall: Boolean)

        @Skip
        fun setConfirmButton(enabled: Boolean)

        @Skip
        fun showCodeError(show: Boolean)

        @Skip
        fun showCustomLoading(type: Int)

        @Skip
        fun hideCustomLoading(type: Int)

        @OneExecution
        fun setFinishRegister(isFinish : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onConfirmMobilePhone()
        fun onSendCallAgain()
        fun onChangeCode(code : String)
    }
}