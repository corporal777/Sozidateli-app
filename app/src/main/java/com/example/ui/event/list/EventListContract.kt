package com.example.ui.event.list

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EmailAffiliation
import com.example.data.models.Event
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface EventListContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setData(events: List<Event?>)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun showEmptyListPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EmailAffiliation>)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EmailAffiliation)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onScrollChange(position: Int, offset: Int)
        fun onRefreshRequest()

        fun onActionRegister(event: String)
        fun onActionCancel(event: String)
        fun onActionWriteToOrganization(emails: List<EmailAffiliation>)
        fun onActionShowEvent(event: String)
        fun onShowEventClick(event: String)
        fun onShowFilterClick(format: Int)

        fun onWriteToOrganizationEmailChosen(email: EmailAffiliation)
    }
}
