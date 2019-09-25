package com.example.ui.status

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface StatusContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setStatus(status: User.Status, isCurrentStatus: Boolean, phone: String?, isProfileComplete: Boolean)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "Phone verification")
        fun checkPassword(action: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "Phone verification")
        fun showChangePhone(action: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "Phone verification")
        fun showCode(phone: String, action: Int, saveFlag: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "Phone verification")
        fun showRemovePhone(phone: String)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "Phone verification")
        fun cancelVerification()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showVerificationError(error: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSentNewCodeMessage(phone: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onAddPhoneClick()
        fun onChangePhoneClick()
        fun onRemovePhoneClick()

        fun onPasswordInputComplete(password: String, action: Int)
        fun onPhoneInputComplete(phone: String, action: Int, saveFlag: Int)
        fun onCodeInputComplete(phone: String, code: String, saveFlag: Int)
        fun onDoNotReceiveCodeClick(phone: String)
        fun onPhoneRemoveAccept()
    }
}
