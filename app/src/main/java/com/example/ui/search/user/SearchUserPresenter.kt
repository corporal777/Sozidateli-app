package com.example.ui.search.user

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.SearchFilter
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.search.SearchPresenter
import com.example.util.pagination.PaginationDataSourceFactory
import javax.inject.Inject

@InjectViewState
class SearchUserPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : SearchPresenter<SearchUserContract.View, User, SearchFilter.User>(), SearchUserContract.Presenter {

    override val pagination = PaginationDataSourceFactory { limit, offset ->
        userRepository.searchUser(mapOf(), limit, offset)
    }

    override fun onUserClick(user: User) {
        viewState.showUser(user)
    }

    private fun buildFilter(): Map<String, Any> = mutableMapOf<String, Any>(
            FILTER_CONTENT to searchText
    ).apply {

    }

    override fun createFilter() = SearchFilter.User()
    override fun copyFilter(filter: SearchFilter.User) = filter.copy()

    companion object {
        private const val FILTER_CONTENT = "content"
    }
}