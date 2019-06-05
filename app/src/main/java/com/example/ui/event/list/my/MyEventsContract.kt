package com.example.ui.event.list.my

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.ui.event.list.EventListContract

interface MyEventsContract {
    interface View : EventListContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun selectEvent(event: Event)
    }

    interface Presenter : EventListContract.Presenter
}
