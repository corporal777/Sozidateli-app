package com.example.ui.profile

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.Event
import com.example.data.models.User
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

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
    }
}
