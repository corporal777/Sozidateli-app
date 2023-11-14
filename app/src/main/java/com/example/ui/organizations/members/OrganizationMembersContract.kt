package com.example.ui.organizations.members

import com.example.data.models.OrganizationMemberModel
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrganizationMembersContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setData(members: List<OrganizationMemberModel?>)

        @OneExecution
        fun showUser(userId: String)

        @OneExecution
        fun showCurrentUser(userId: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onMemberClick(memberId: Int?)
        fun onAddUserFavoriteCLick(member : OrganizationMemberModel)
        fun onRefreshRequest()
    }
}
