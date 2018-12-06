package com.example.ui.aboutEvent

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.base.BaseContract

interface AboutEventContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventData(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutForum(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showNews(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showDocuments(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showContacts(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showTransfer(event: Event)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setChatLabel(label: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onAboutForumClick()
        fun onNewsClick()
        fun onDocumentsClick()
        fun onContactsClick()
        fun onTransferClick()
    }
}
