package com.example.ui.contactsSearch

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.user.User
import com.example.extensions.build
import com.example.repository.ChatRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import performOnBackgroundOutOnMain
import withLoadingDialog
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ContactsSearchPresenter
@Inject constructor(
        private val userRepository: UserRepository,
        private val chatRepository: ChatRepository
) : BasePresenter<ContactsSearchContract.View>(), ContactsSearchContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    private var searchText = ""

    private val searchCompositeDisposable = CompositeDisposable()
    private val pagination = PaginationDataSourceFactory { limit, offset -> userRepository.searchUser(searchText, searchText, limit, offset) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination.build()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.apply {
                        viewState.hideLoadingDialog()
                        setData(it)
                    }
                }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
                .call(compositeDisposable)
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
        searchText = text
        searchCompositeDisposable.clear()
        Observable.timer(350, TimeUnit.MILLISECONDS)
                .performOnBackgroundOutOnMain()
                .subscribe {
                    pagination.invalidate()
                }
                .call(searchCompositeDisposable)

    }

    override fun onSearchCollapsed() = viewState.navigateUp()

    override fun onUserClick(user: User) {
        chatRepository.startChat(user.user_id)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.openChat(it.chat_id.toString(), user.user_id.toString(), user.fullName)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchCompositeDisposable.clear()
    }
}
