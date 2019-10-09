package com.example.ui.search

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.R
import com.example.data.models.SearchFilter
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.base.BaseFragment
import com.example.ui.views.BottomDialog
import com.example.util.pagination.PaginationListGroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_search.*

abstract class SearchFragment<P : SearchContract.Presenter<I>, I, F : SearchFilter> : BaseFragment(), SearchContract.View<I, F> {

    abstract var presenter: P

    private var filterDialog: BottomDialog? = null
    private var filterView: View? = null

    protected val adapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        (parentFragment as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = this@SearchFragment.adapter
        }
    }

    override fun setData(data: List<I>) {
        adapter.update(data.map(::createItem))
    }

    override fun showFilter(filter: F) {
        val filterContainer = (layoutInflater.inflate(R.layout.layout_filter, null) as ViewGroup).apply {
            findViewById<ViewGroup>(R.id.flFilters).apply {
                val filterView = createFilterView(filter)
                this@SearchFragment.filterView = filterView
                addView(filterView)
            }

            findViewById<View>(R.id.btnApply).setOnClickListener { presenter.onFilterApplyClick() }
            findViewById<View>(R.id.btnClear).setOnClickListener { presenter.onFilterClearClick() }
            findViewById<View>(R.id.btnClose).setOnClickListener { filterDialog?.dismiss() }
        }

        filterDialog = BottomDialog(requireContext(), filterContainer)
                .apply {
                    setOnDismissListener { presenter.onFilterCancel() }
                    show()
                }
    }

    override fun hideFilter() {
        filterDialog?.apply {
            setOnDismissListener(null)
            dismiss()
        }
    }

    override fun clearFilter() {
        filterView?.let { clearFilterView(it) }
    }

    protected abstract fun createItem(itemData: I): Item
    protected abstract fun createFilterView(filter: F): View
    protected abstract fun clearFilterView(filterView: View)

    override fun layout() = R.layout.fragment_search
}