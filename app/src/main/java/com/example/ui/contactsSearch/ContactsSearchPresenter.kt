package com.example.ui.contactsSearch

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.models.ContactSearch
import com.example.repository.DummyRepository
import com.example.ui.base.BasePresenter
import com.example.util.pagination.PaginationDataSourceFactory
import com.example.util.pagination.PaginationResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ContactsSearchPresenter
@Inject constructor(
        private val dummyRepository: DummyRepository
) : BasePresenter<ContactsSearchContract.View>(), ContactsSearchContract.Presenter {

    private var scrollPosition = 0
    private var scrollOffset = 0
    private var searchText = ""

    private val factory = PaginationDataSourceFactory { limit, offset ->
        Maybe.fromCallable {
            val searchText = this.searchText
            if (searchText.isBlank()) return@fromCallable PaginationResponse(totalCount = null, data = listOf<ContactSearch>())

            val filtered = dummyUsers.filter { it.name.contains(searchText, true) }
            val total = filtered.size
            val from = if (offset <= total) offset else total
            val to = if (from + limit <= total) from + limit else total

            PaginationResponse(totalCount = null, data = filtered.subList(from, to)
                    .mapIndexed { index, user ->
                        ContactSearch(user, when {
                            offset + index < 1 -> ContactSearch.Type.FAVORITE
                            offset + index < 3 -> ContactSearch.Type.CHAT
                            else -> ContactSearch.Type.CONTACT
                        },
                                searchText)
                    })
        }
    }

    private val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()

    private val dummyUsers = (0..100).map { dummyRepository.getUser(it) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        RxPagedListBuilder(factory, config)
                .buildFlowable(BackpressureStrategy.LATEST)
                .debounce(200, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe({ viewState.apply { setData(it) } }, { it.printStackTrace() })
                .call(compositeDisposable)
    }

    override fun onScrollChange(position: Int, offset: Int) {
        scrollPosition = position
        scrollOffset = offset
    }

    override fun onQueryTextSubmit(text: String) = search(text)

    override fun onQueryTextChange(text: String) = search(text)

    private fun search(text: String) {
        searchText = text
        factory.invalidateFromStart()
    }

    override fun onSearchCollapsed() = viewState.navigateUp()
}
