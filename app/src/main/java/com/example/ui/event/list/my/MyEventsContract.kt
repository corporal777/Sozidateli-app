package com.example.ui.event.list.my

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.event.list.EventListContract
import com.example.util.OneExecutionByTagStateStrategy

interface MyEventsContract {
    interface View : EventListContract.View {
        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setNoFilterHeader()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setAcceptedHeader()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setPendingHeader()

        @StateStrategyType(OneExecutionByTagStateStrategy::class, tag = "header")
        fun setDeclinedHeader()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAccepted()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showPending()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showDeclined()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun selectEvent(event: Event)
    }

    interface Presenter : EventListContract.Presenter {
        fun onAcceptedClick()
        fun onPendingClick()
        fun onDeclinedClick()
    }
}
