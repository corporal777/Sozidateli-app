package com.example.ui.contactsSearch

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ContactSearch
import com.example.data.models.user.User
import com.example.repository.DummyRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import com.example.util.pagination.SimplePagination
import io.reactivex.BackpressureStrategy
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ContactsSearchPresenter
@Inject constructor(
        private val userRepository: UserRepository
) : BasePresenter<ContactsSearchContract.View>(), ContactsSearchContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    private var searchText = ""

    private val searchCompositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showLoadingDialog()
        search("")
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onQueryTextSubmit(text: String){
        viewState.hideKeyboard()
    }

    override fun onQueryTextChange(text: String) = search(text)

    private fun search(text: String) {
        searchText = text
        searchCompositeDisposable.clear()
        SimplePagination { limit, offset -> userRepository.searchUser(searchText,searchText,limit, offset) }
                .build()
                .subscribe({ viewState.apply {
                    viewState.hideLoadingDialog()
                    setData(it)
                } }, {
                    viewState.hideLoadingDialog()
                    it.printStackTrace()
                })
                .call(searchCompositeDisposable)
    }

    override fun onSearchCollapsed() = viewState.navigateUp()

    override fun onDestroy() {
        super.onDestroy()
        searchCompositeDisposable.clear()
    }
}
