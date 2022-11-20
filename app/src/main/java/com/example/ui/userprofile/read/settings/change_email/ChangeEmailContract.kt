package com.example.ui.userprofile.read.settings.change_email

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ChangeEmailContract {
    interface View : BaseBottomSheetContract.View {

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setCurrentEmail(currentEmail : String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotValid(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEmailNotUnique(email: String)

        @StateStrategyType(SkipStrategy::class)
        fun showChangeEmailComplete()

        @StateStrategyType(SkipStrategy::class)
        fun showEmailConfirm(email: String)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun checkEmailIsUnique(email: String)
        fun updateEmail(email: String)

    }
}