package com.example.ui.contactsSearch

import androidx.paging.PagedList
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatStartResponse
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface ContactsSearchContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setData(contactSearch: PagedList<User>)

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(SkipStrategy::class)
        fun openChat(chatId: String, userId: String, userName: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onScrollChange(position: Int, offset: Int)
        fun onQueryTextSubmit(text: String)
        fun onQueryTextChange(text: String)
        fun onSearchCollapsed()
        fun onUserClick(user: User)
    }
}
