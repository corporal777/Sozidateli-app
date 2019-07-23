package com.example.ui.search.searchType

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.SearchTypeEvent
import com.example.holders.SearchTypeItem
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_search_type.*
import javax.inject.Inject
import javax.inject.Provider

class SearchTypeFragment : BaseFragment(), SearchTypeContract.View {

    @InjectPresenter
    lateinit var presenter: SearchTypePresenter

    @Inject
    lateinit var presenterProvider: Provider<SearchTypePresenter>

    @ProvidePresenter
    fun providePresenter(): SearchTypePresenter = presenterProvider.get().apply {
        val args = SearchTypeFragmentArgs.fromBundle(arguments!!)
        this.setData(args.data.array, args.data.isOrganization)
    }

    private var groupAdapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = groupAdapter
            if (itemDecorationCount == 0) addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun setData(data: MutableList<SearchTypeEvent>) {
        groupAdapter.update(data.map {
            SearchTypeItem(it) { item, isSelect ->
                presenter.selectItem(isSelect, item)
            }
        })
    }

    override fun layout() = R.layout.fragment_search_type
}
