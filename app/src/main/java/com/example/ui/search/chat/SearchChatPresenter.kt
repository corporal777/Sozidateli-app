package com.example.ui.search.chat

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.UserDetail
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.repository.CommonRepository
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.search.user.AbstractSearchUserPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class SearchChatPresenter
@Inject constructor(
        appData: AppData,
        userRepository: UserRepository,
        commonRepository: CommonRepository,
        private val chatRepository: ChatRepository,
        private val eventRepository: EventRepository
) : AbstractSearchUserPresenter<SearchChatContract.View>(appData, userRepository, commonRepository, eventRepository), SearchChatContract.Presenter {

    private var isDataLoadWithFilter = false

    /*override val pagination = PaginationDataSourceFactory { limit, offset ->
        val filter = buildFilter()

        if (filter.isEmpty()) {
            isDataLoadWithFilter = false
            chatRepository.searchUser(limit, offset)
        } else {
            isDataLoadWithFilter = true
            userRepository.usersList(limit, offset, filter)
        }
    }*/

    override fun onDataLoaded(data: List<UserDetail?>) {
        if (isDataLoadWithFilter) {
            super.onDataLoaded(data)
        } else {
            dispatchListUpdate(data)
        }
    }

    private fun dispatchListUpdate(users: List<UserDetail?>) {
        val favorites = mutableListOf<UserDetail>()
        val chats = mutableListOf<UserDetail>()
        val another = mutableListOf<UserDetail>()
        users.forEach {
            when {
                it == null -> {
                    // do nothing
                }
                it.binds?.userFavorite != null -> favorites.add(it)
                //it.is_has_chat -> chats.add(it)
                else -> another.add(it)
            }
        }

        viewState.setItems(favorites, chats, another)
    }
}