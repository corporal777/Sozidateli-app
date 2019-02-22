package com.example.ui.contactsSearch

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ContactSearch
import com.example.data.models.user.User
import com.example.repository.ChatRepository
import com.example.repository.DummyRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.SimplePagination
import io.reactivex.BackpressureStrategy
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    private val pagination = SimplePagination { limit, offset -> userRepository.searchUser(searchText, searchText, limit, offset) }


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        pagination
                .build()
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
        Observable.timer(350,TimeUnit.MILLISECONDS)
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
                    viewState.openChat(it.chat_id.toString(), it.user_id.toString(), user.fullName)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchCompositeDisposable.clear()
    }
}
