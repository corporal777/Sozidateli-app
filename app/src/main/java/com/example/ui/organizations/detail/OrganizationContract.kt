package com.example.ui.organizations.detail

import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface OrganizationContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setMainData(organization: OrganizationNew)

        @AddToEndSingle
        fun setInformationData(organization: OrganizationNew)

        @AddToEndSingle
        fun setEventsData(events: List<EventNew>)

        @AddToEndSingle
        fun setMembersData(members: List<OrganizationMemberModel>, totalSize: Int)

        @OneExecution
        fun showAllEvents(organizationId: String)

        @OneExecution
        fun updateOrganizationSubscription(organization: OrganizationNew)

        @OneExecution
        fun showAllUsers(organizationId: String)

        @OneExecution
        fun showUser(id: String)

        @OneExecution
        fun showCurrentUser(id: String)

        @Skip
        fun updateUserSubscription(userId: Int, isSubscribed: Boolean)

        @Skip
        fun updateEvent(event: EventNew)

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @Skip
        fun showAgreementRegisterDialog(event: String, url: String)
    }

    interface Presenter : BaseContract.Presenter {

        fun onShowMoreEventsClick()
        fun onShowMoreUsersClick()

        fun onUserClick(user: String?)
        fun onAddUserFavoriteCLick(member: OrganizationMemberModel)
        fun onAddOrganizationFavoriteClick(organization: OrganizationNew)
        fun onRefreshRequest()

        fun onActionRegister(event: String, url: String?)
        fun onActionCancel(event: String, registrationId: String?)
        fun onShowEventClick(event: String)
        fun onAcceptRegistrationAgreement(event: String)
    }
}