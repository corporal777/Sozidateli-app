package com.example.ui.profile

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface ProfileContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showProfile(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFavorites()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvents()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutApp()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showBanned()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openSupportEmail(uid: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openPlayMarket()
    }

    interface Presenter : BaseContract.Presenter {
        fun onProfileClick()
        fun onFavoritesClick()
        fun onEventsClick()
        fun onAboutApplicationClick()
        fun onBannedClick()
        fun onSupportClick()
        fun onRateClick()
        fun onLogoutClick()
    }
}
