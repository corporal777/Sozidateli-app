package com.example.ui.userprofile.read.settings.change_name

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.bottomSheet.BaseBottomSheetContract

interface ChangeNameContract {
    interface View : BaseBottomSheetContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUserName(
            name: String?,
            lastName: String?,
            middleName: String?,
            isMiddleNameAbsent: Boolean
        )
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableMiddleNameInput(enable : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun enableBtnSave(isEnable :Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFirstNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showLastNameError(show: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMiddleNameError(show: Boolean)
    }

    interface Presenter : BaseBottomSheetContract.Presenter {
        fun onSaveNameClick()
        fun performChangeName(value: String?)
        fun performChangeLastName(value: String?)
        fun performChangeMiddleName(value: String?)
        fun performSetNoMiddleName(isHas : Boolean)
    }
}