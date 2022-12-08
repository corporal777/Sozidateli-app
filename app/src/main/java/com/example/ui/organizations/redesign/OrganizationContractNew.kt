package com.example.ui.organizations.redesign

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract
import com.example.ui.views.UserSubscribeButton

interface OrganizationContractNew {
    interface View : BaseContract.View {

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setMainData(organization: OrganizationNew)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setInformationData(organization: OrganizationNew)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setEventsData(events: List<EventNew?>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setMembersData(members: List<OrganizationMemberModel>, totalSize : Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAllEvents(organizationId: String)

        @StateStrategyType(SkipStrategy::class)
        fun setSubscribed(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAllUsers(organizationId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUser(id: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCurrentUser(id: String)

        @StateStrategyType(SkipStrategy::class)
        fun updateUserSubscription(userId : Int, isSubscribed: Boolean)

        @StateStrategyType(SkipStrategy::class)
        fun updateEvent(event : EventNew)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)
    }

    interface Presenter : BaseContract.Presenter, BaseContract.OnChangeElevation {

        fun onShowMoreEventsClick()
        fun onShowMoreUsersClick()

        fun onUserClick(user: String?)
        fun onUserActionCLick(userId : String)
        fun onSubscribeClick(action : UserSubscribeButton.Action)
        fun onRefreshRequest()

        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)

        fun onShowEventClick(event: String)
    }
}