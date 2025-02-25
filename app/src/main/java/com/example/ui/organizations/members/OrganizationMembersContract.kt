package com.example.ui.organizations.members

import androidx.paging.PagingData
import com.example.data.models.OrganizationMemberModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface OrganizationMembersContract {
    interface View : BaseContract.View {

        @OneExecution
        fun setData(data: PagingData<UserDetail>)

        @OneExecution
        fun showUser(userId: String)

        @OneExecution
        fun showCurrentUser(userId: String)

        @Skip
        fun updateUser(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter {
        fun onMemberClick(user: UserDetail)
        fun onAddUserFavoriteCLick(user: UserDetail)
        fun onRefreshRequest()
    }
}
