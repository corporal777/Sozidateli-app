package com.example.ui.search.chat

import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.search.user.SearchUserContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface SearchChatContract {
    interface View : SearchUserContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "list data")
        fun setItems(favorites: List<User>, chats: List<User>, another: List<User>)
    }

    interface Presenter : SearchUserContract.Presenter
}
