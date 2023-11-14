package com.example.ui.favoritesTab.users

import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface FavoriteUsersContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setData(data: List<UserDetail?>)

        @Skip
        fun setUsersFavoriteEmptyPlaceholder()

        @Skip
        fun showUser(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onUserClick(user: UserDetail)
        fun onUserRemoveFromFavoritesClick(user: UserDetail)
        fun onRefreshRequest()
    }
}
