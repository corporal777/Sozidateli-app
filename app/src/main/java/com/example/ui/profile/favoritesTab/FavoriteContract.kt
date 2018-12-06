package com.example.ui.profile.favoritesTab

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface FavoriteContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectTab(position: Int)
    }

    interface Presenter : BaseContract.Presenter{
        fun setSelectedTab(position: Int)
    }
}
