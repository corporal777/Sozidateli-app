package com.example.ui.organizations.members

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.OrganizationMember
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface OrganizationMembersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(members: List<OrganizationMember>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showUser(userId: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onMemberClick(member: OrganizationMember)
        fun onScrollChange(position: Int, offset: Int)
    }
}
