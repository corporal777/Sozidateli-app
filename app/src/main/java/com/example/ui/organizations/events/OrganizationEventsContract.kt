package com.example.ui.organizations.events

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventNew
import com.example.ui.base.BaseContract
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface OrganizationEventsContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setData(events: List<EventNew?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showRegistrationFieldsRequest(fields: List<String>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEditProfile(id: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onShowEditProfileClick()
        fun onRefreshRequest()
        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
    }
}
