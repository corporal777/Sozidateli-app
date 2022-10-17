package com.example.ui.userprofile.read.settings.change_password

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.ui.base.bottomSheet.BaseBottomSheetContract
import com.example.ui.userprofile.base.BaseUserProfileContract

interface ChangePasswordContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPasswordIsCorrect()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setRecoverPassword(code: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setPasswordIsNotCorrect(attempts: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPasswordSuccessUpdated()

        @StateStrategyType(SkipStrategy::class)
        fun showLoginAgainDialog()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showRecoveryPassword(email: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onChangePasswordClickConfirm(newPassword: String)
        fun onRecoverPasswordClickConfirm(code : String, newPassword: String)
        fun checkPasswordValid(password: String)
        fun logoutFromAccount()
        fun onRecoveryPasswordClick()
    }
}