package com.example.ui.search

import android.app.DatePickerDialog
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import androidx.navigation.fragment.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.DataArgsSearchType
import com.example.data.models.Event
import com.example.holders.SearchEventResultItem
import com.example.holders.SearchHeaderItem
import com.example.holders.SelectedSearchTypeItem
import com.example.ui.base.BaseFragment
import com.example.util.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class SearchFragment : BaseFragment(), SearchContract.View {

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchPresenter = presenterProvider.get().apply {
        header = SearchHeaderItem(this)
        section.setHeader(header)
        groupAdapter.add(section)
    }

    private lateinit var header: SearchHeaderItem

    private val pagedList = PagedListGroup<SearchEventResultItem>()

    private val section = Section().apply {
        add(pagedList)
    }

    private val groupAdapter = GroupAdapter<ViewHolder>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        }
    }

    override fun showTypeEvent(data: DataArgsSearchType) {
        findNavController().navigate(SearchFragmentDirections.searchToSearchType(data))
    }

    override fun showPlaces(data: DataArgsSearchType) {
        findNavController().navigate(SearchFragmentDirections.searchToSearchType(data))
    }

    override fun setSearchData(searchHolder: SearchHolder) {
        header.setSearchData(searchHolder)
    }

    override fun updatePlacesList(searchHolder: SearchHolder) {
        header.updatePlacesList(searchHolder)
    }

    override fun updateTypeEventsList(searchHolder: SearchHolder) {
        header.updateTypeEventsList(searchHolder)
    }

    override fun showSearchResult(data: PagedList<SearchEventResultItem>) {
        pagedList.submitList(data)
        header.showResultHeader(true)
    }

    override fun showEvent(event: Event) {
        findNavController().navigate(R.id.search_to_event_screen,bundleOf(
                ARG_EVENT to event
        ))
    }

    override fun showDateDialog(date: Long, type: String) {
        header.showDateDialog(date, type)
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_search
}
