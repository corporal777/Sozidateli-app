package com.example.ui.organizations

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Event
import com.example.data.models.Organization
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface OrganizationContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setOrganization(organization: Organization, events: List<Event>, eventsTotal: Int, users: List<User>, usersTotal: Int, listsLimit: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvents(id: String)

        @StateStrategyType(SkipStrategy::class)
        fun changeScrollY(scroll: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: Event)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowMoreEventsClick()
        fun onEventClick(event: Event)
        fun onGoToEventClick(event: Event)

        fun onScrollPositionChange(scroll: Int)
    }
}
