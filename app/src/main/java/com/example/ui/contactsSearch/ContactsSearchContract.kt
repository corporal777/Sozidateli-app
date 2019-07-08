package com.example.ui.contactsSearch

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ContactsSearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setItems(favorites: List<User>, chats: List<User>, another: List<User>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun openUserInfo(userId: String)

        @StateStrategyType(SkipStrategy::class)
        fun focusOnInput()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "filter")
        fun showFilter()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "filter")
        fun hideFilter()
    }

    interface Presenter : BaseContract.Presenter {
        fun onScrollChange(position: Int, offset: Int)
        fun onQueryTextSubmit(text: String)
        fun onQueryTextChange(text: String)
        fun onSearchCollapsed()
        fun onUserClick(user: User)
    }
}
