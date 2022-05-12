package com.example.ui.search.chat

import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.ui.search.user.SearchUserContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface SearchChatContract {
    interface View : SearchUserContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list data")
        fun setItems(favorites: List<UserDetail>, chats: List<UserDetail>, another: List<UserDetail>)
    }

    interface Presenter : SearchUserContract.Presenter
}
