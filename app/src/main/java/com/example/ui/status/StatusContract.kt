package com.example.ui.status

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface StatusContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setStatus(status: User.Status, isCurrentStatus: Boolean, phone: String?, isProfileComplete: Boolean)
    }

    interface Presenter : BaseContract.Presenter {

    }
}
