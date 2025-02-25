package com.example.ui.favoritesTab.users

import androidx.paging.PagingData
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface FavoriteUsersContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setData(data: PagingData<UserDetail>)

        @OneExecution
        fun showUser(user: UserDetail)

        @Skip
        fun updateUser(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter {
        fun onUserClick(user: UserDetail)
        fun onUserRemoveFavoritesClick(user: UserDetail)
        fun onRefreshRequest()
    }
}
