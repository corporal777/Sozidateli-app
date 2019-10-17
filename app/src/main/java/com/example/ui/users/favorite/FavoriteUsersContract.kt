package com.example.ui.users.favorite

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.pagination.PaginationListGroupAdapter

interface FavoriteUsersContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setData(data: List<User>)

        @StateStrategyType(SkipStrategy::class)
        fun showUser(user: User)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onUserClick(user: User)
        fun onUserRemoveFromFavoritesClick(user: User)
    }
}
