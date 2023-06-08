package com.example.ui.organizations.members

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.OrganizationMember
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.OrganizationNewMemberModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface OrganizationMembersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(members: List<OrganizationMemberModel?>)

        @StateStrategyType(SkipStrategy::class)
        fun showUser(userId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showCurrentUser(userId: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onMemberClick(memberId: Int?)
        fun onAddUserFavoriteCLick(member : OrganizationMemberModel)
        fun onRefreshRequest()
    }
}
