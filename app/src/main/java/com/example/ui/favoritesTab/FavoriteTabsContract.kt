package com.example.ui.favoritesTab

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.ui.base.BaseContract

interface FavoriteTabsContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setCurrentFragment(position : Int)
    }

    interface Presenter : BaseContract.Presenter{

    }
}
