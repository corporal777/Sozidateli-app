package com.example.ui.search

import androidx.viewbinding.ViewBinding
import com.example.app.R
import com.example.data.models.SearchFilter
import com.example.app.databinding.LayoutListSearchBinding
import com.example.interfaces.SearchInterfaceProvider
import com.example.ui.search.tabs.SearchTabsFragment
import com.example.ui.base.BaseBindingFragment
import by.kirich1409.viewbindingdelegate.viewBinding

abstract class SearchFragment<P : SearchContract.Presenter<F>, F : SearchFilter>(layoutRes: Int) :
    BaseBindingFragment(layoutRes), SearchContract.View<F> {


    abstract var presenter: P

    override fun onResume() {
        super.onResume()
        (this as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }
        (parentFragment as? SearchInterfaceProvider)?.apply {
            presenter.onResume(provideSearchInterface())
            return
        }
    }

    override fun setHasFilter() {
        (parentFragment as? SearchTabsFragment)?.apply {
            setFiltersChosen(this@SearchFragment.presenter.isHasFilter())
        }
    }

    override fun setDataEmpty(isEmpty: Boolean, title: String) {
        (parentFragment as? SearchTabsFragment)?.showEmptyDataText(isEmpty, title)
    }
}