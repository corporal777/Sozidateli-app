package com.example.ui.profile

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ProfileContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutStatus()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFullProfile()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showFavorite()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showMyEvents()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showTabEvents()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCurrentEvent(event: Event)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAboutApp()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChatSetting()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "NOTIFICATION")
        fun showLastNotification(text: String, notificationCount: Int)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "NOTIFICATION")
        fun hideLastNotification()

        @StateStrategyType(SkipStrategy::class)
        fun showNotifications()
    }

    interface Presenter : BaseContract.Presenter {
        fun clickAboutStatus()
        fun clickFullProfile()
        fun clickFavorite()
        fun clickMyEvents()
        fun clickTabEvents()
        fun clickCurrentEvent(event: Event)
        fun clickAboutApp()
        fun clickChatSetting()
        fun onNotificationClick()
    }
}
