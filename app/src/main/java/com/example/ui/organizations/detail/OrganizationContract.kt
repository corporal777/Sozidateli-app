package com.example.ui.organizations.detail

import com.example.data.models.EventNew
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNew
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface OrganizationContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setOrganizationsData(organization: OrganizationNew)

        @OneExecution
        fun setEventsData(events: List<EventNew>, totalSize: Int)

        @OneExecution
        fun setMembersData(members: List<OrganizationMemberModel>, totalSize: Int)

        @OneExecution
        fun showAllEvents(organizationId: String)

        @OneExecution
        fun showAllUsers(organizationId: String)

        @OneExecution
        fun showUser(id: String)

        @OneExecution
        fun showCurrentUser()

        @OneExecution
        fun showAboutEvent(event: String)

        @OneExecution
        fun showEventRequest(event: String)

        @OneExecution
        fun showAuthorization()

        @Skip
        fun updateOrganization(organization: OrganizationNew)

        @Skip
        fun updateUser(member: OrganizationMemberModel)

        @Skip
        fun updateEvent(event: EventNew)

        @Skip
        fun showAgreementRegisterDialog(event: EventNew)
    }

    interface Presenter : BaseContract.Presenter {

        fun onShowMoreEventsClick()
        fun onShowMoreUsersClick()

        fun onUserClick(user: String?)
        fun onAddUserFavoriteCLick(member: OrganizationMemberModel)
        fun onAddOrganizationFavoriteClick(organization: OrganizationNew)
        fun onRefreshRequest()

        fun onActionRegister(event: EventNew, withAccept : Boolean)
        fun onActionCancel(event: EventNew)
        fun onShowEventClick(event: String)
        fun onShowAuthorization(event: String)
    }
}