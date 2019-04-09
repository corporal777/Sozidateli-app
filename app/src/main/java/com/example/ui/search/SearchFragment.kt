package com.example.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.DataArgsSearchType
import com.example.data.models.Event
import com.example.holders.PagedListGroup
import com.example.holders.SearchEventResultItem
import com.example.holders.SearchHeaderItem
import com.example.ui.base.BaseFragment
import com.example.util.ARG_EVENT
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject
import javax.inject.Provider

class SearchFragment : BaseFragment(), SearchContract.View {

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchPresenter>

    @ProvidePresenter
    fun providePresenter(): SearchPresenter = presenterProvider.get().apply {
        header = SearchHeaderItem(childFragmentManager, this)
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
        setHasOptionsMenu(true)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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
        section.notifyItemChanged(0)
    }

    override fun updateOrganizationList(searchHolder: SearchHolder) {
        header.updatePlacesList(searchHolder)
    }

    override fun updateCategoryList(searchHolder: SearchHolder) {
        header.updateTypeEventsList(searchHolder)
    }

    override fun showSearchResult(data: PagedList<SearchEventResultItem>, totalCount: Int?) {
        pagedList.submitList(data)
        header.showResultHeader(true, totalCount)
        section.notifyItemChanged(0)
    }

    override fun hideSearchResultLabel() {
        header.showResultHeader(false, null)
        pagedList.submitList(null)
    }

    override fun showEvent(event: Event) {
        findNavController().navigate(R.id.search_to_event_screen, bundleOf(
                ARG_EVENT to event
        ))
    }

    override fun showDateDialog(date: Long, type: String) {
        header.showDateDialog(date, type)
    }

    override fun showQrScan() {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToQrScannerFragment())
    }

    override fun showEventRequest(event: Event) {
        findNavController().navigate(R.id.request_fragment, bundleOf(ARG_EVENT to event))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_clear, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> presenter.clearFilter()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_search
}
