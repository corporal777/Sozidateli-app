package com.example.ui.users.favorite

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface FavoriteUsersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: List<UserDetail?>)

        @StateStrategyType(SkipStrategy::class)
        fun setUsersFavoriteEmptyPlaceholder()

        @StateStrategyType(SkipStrategy::class)
        fun showUser(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onUserClick(user: UserDetail)
        fun onUserRemoveFromFavoritesClick(user: UserDetail)
        fun onRefreshRequest()
    }
}
