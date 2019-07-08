package com.example.ui.contactsSearch

import com.arellomobile.mvp.InjectViewState
import com.example.data.models.user.User
import com.example.extensions.buildList
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_FILTER
import com.example.ui.contactsSearch.ContactsSearchFragment.Companion.SEARCH_ACTION_INPUT
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationListGroupAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ContactsSearchPresenter
@Inject constructor(
        private val chatRepository: ChatRepository
) : BasePresenter<ContactsSearchContract.View>(), ContactsSearchContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {

    var startAction: Int = ContactsSearchFragment.SEARCH_ACTION_NONE

    private var scrollPosition = 0
    private var scrollOffset = 0

    private val searchSubject = PublishSubject.create<String>()
    private var searchText = ""

    private val pagination = PaginationDataSourceFactory { limit, offset ->
        chatRepository.searchUser(getUserFilter(), limit, offset)
    }.buildList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Observable.create(pagination)
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.hideLoadingDialog()
                    dispatchListUpdate(it)
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })

        compositeDisposable += searchSubject.debounce(300, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe {
                    if (searchText != it) {
                        searchText = it
                        pagination.invalidate()
                    }
                }

        when (startAction) {
            SEARCH_ACTION_INPUT -> viewState.apply {
                focusOnInput()
                showKeyboard()
            }
            SEARCH_ACTION_FILTER -> viewState.showFilter()
        }
    }

    override fun onItemTake(position: Int) {
        pagination.onItemTake(position)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onQueryTextSubmit(text: String) {
        viewState.hideKeyboard()
    }

    override fun onQueryTextChange(text: String) = search(text)

    private fun search(text: String) {
        text.trim().apply {
            if (!isEmpty()) searchSubject.onNext(this)
        }
    }

    override fun onSearchCollapsed() = viewState.navigateUp()

    override fun onUserClick(user: User) {
        viewState.openUserInfo(user.user_id.toString())
    }

    private fun dispatchListUpdate(users: List<User>) {
        val favorites = mutableListOf<User>()
        val chats = mutableListOf<User>()
        val another = mutableListOf<User>()
        users.forEach {
            when {
                it.is_in_favorite -> favorites.add(it)
                it.is_has_chat -> chats.add(it)
                else -> another.add(it)
            }
        }

        viewState.setItems(favorites, chats, another)
    }

    private fun getUserFilter(): Map<String, Any> {
        return mapOf("user_fio" to searchText)
    }
}
