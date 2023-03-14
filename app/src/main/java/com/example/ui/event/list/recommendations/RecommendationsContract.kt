package com.example.ui.event.list.recommendations

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.EventNew
import com.example.data.models.EventPhoneModel
import com.example.ui.base.BaseContract
import com.example.ui.event.list.EventListContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface RecommendationsContract {
    interface View : BaseContract.View{

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "data")
        fun setData(events: List<EventNew?>, isNeedUpdateApp : Boolean?)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class)
        fun showEmptyListPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showSearch()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updateActionButton(event: EventNew?)

    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onSearchClick()
        fun onRefreshRequest()

        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
    }
}
