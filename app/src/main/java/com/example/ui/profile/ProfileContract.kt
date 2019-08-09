package com.example.ui.profile

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ProfileContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "NOTIFICATION")
        fun highlightNotifications(notificationCount: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "NOTIFICATION")
        fun hideLastNotification()

        @StateStrategyType(SkipStrategy::class)
        fun showNotifications()

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
    }

    interface Presenter : BaseContract.Presenter {
        fun onProfileClick()
        fun onNotificationClick()
        fun onFavoritesClick()
        fun onEventsClick()
        fun onAboutApplicationClick()
        fun onBannedClick()
        fun onSupportClick()
        fun onRateClick()
        fun onLogoutClick()
    }
}
